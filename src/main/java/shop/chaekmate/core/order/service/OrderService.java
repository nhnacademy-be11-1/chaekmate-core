package shop.chaekmate.core.order.service;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.chaekmate.core.book.entity.Book;
import shop.chaekmate.core.book.exception.BookNotFoundException;
import shop.chaekmate.core.book.exception.InsufficientStockException;
import shop.chaekmate.core.book.repository.BookRepository;
import shop.chaekmate.core.member.entity.Member;
import shop.chaekmate.core.member.repository.MemberRepository;
import shop.chaekmate.core.order.dto.request.CanceledBooksRequest;
import shop.chaekmate.core.order.dto.request.OrderSaveRequest;
import shop.chaekmate.core.order.dto.request.OrderedBookSaveRequest;
import shop.chaekmate.core.order.dto.response.OrderSaveResponse;
import shop.chaekmate.core.order.entity.Order;
import shop.chaekmate.core.order.entity.OrderedBook;
import shop.chaekmate.core.order.entity.Wrapper;
import shop.chaekmate.core.order.entity.type.OrderedBookStatusType;
import shop.chaekmate.core.order.event.DeliveryEventPublisher;
import shop.chaekmate.core.order.event.ShippingStartedEvent;
import shop.chaekmate.core.order.exception.NotFoundWrapperException;
import shop.chaekmate.core.order.repository.OrderRepository;
import shop.chaekmate.core.order.repository.OrderedBookRepository;
import shop.chaekmate.core.order.repository.WrapperRepository;
import shop.chaekmate.core.payment.dto.response.PaymentCancelResponse;
import shop.chaekmate.core.payment.exception.NotFoundOrderNumberException;
import shop.chaekmate.core.point.exception.MemberNotFoundException;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderService {

    private final MemberRepository memberRepository;
    private final BookRepository bookRepository;
    private final WrapperRepository wrapperRepository;
    private final OrderRepository orderRepository;
    private final OrderedBookRepository orderedBookRepository;
    private final DeliveryEventPublisher eventPublisher;

    @Transactional
    public OrderSaveResponse createOrder(Long memberId, OrderSaveRequest request) {

        //예외 검증
        for (OrderedBookSaveRequest item : request.orderedBooks()) {

            Book book = bookRepository.findById(item.bookId()).orElseThrow(BookNotFoundException::new);

            if (!book.hasStock(item.quantity())) {
                throw new InsufficientStockException();
            }
        }

        //주문번호
        String orderNumber = NanoIdUtils.randomNanoId();

        // null -> 비회원
        Member member = null;

        if (memberId != null) {
            member = memberRepository.findById(memberId).orElseThrow(MemberNotFoundException::new);
        }


        Map<Long, Book> bookMap = bookRepository.findAllById(
                        request.orderedBooks().stream().map(OrderedBookSaveRequest::bookId).toList()
                ).stream()
                .collect(Collectors.toMap(Book::getId, b -> b));

        Map<Long, Wrapper> wrapperMap = wrapperRepository.findAllById(
                        request.orderedBooks().stream()
                                .map(OrderedBookSaveRequest::wrapperId)
                                .filter(Objects::nonNull)
                                .toList()
                ).stream()
                .collect(Collectors.toMap(Wrapper::getId, w -> w));

        Order order = Order.createOrderReady(
                member,
                orderNumber,
                request.ordererName(),
                request.ordererPhone(),
                request.ordererEmail(),
                request.recipientName(),
                request.recipientPhone(),
                request.zipcode(),
                request.streetName(),
                request.detail(),
                request.deliveryRequest(),
                request.deliveryAt(),
                request.deliveryFee(),
                request.totalPrice()
        );

        Order savedOrder = orderRepository.save(order);

        for (OrderedBookSaveRequest obRequest : request.orderedBooks()) {

            Book book = bookMap.get(obRequest.bookId());
            if (book == null) {
                throw new BookNotFoundException();
            }

            Wrapper wrapper = null;
            if (obRequest.wrapperId() != null) {
                wrapper = wrapperMap.get(obRequest.wrapperId());
                if (wrapper == null) {
                    throw new NotFoundWrapperException();
                }
            }

            OrderedBook orderedBook = OrderedBook.createOrderDetailReady(
                    savedOrder,
                    book,
                    obRequest.quantity(),
                    obRequest.originalPrice(),
                    obRequest.salesPrice(),
                    obRequest.discountPrice(),
                    wrapper,
                    obRequest.wrapperPrice(),
                    obRequest.issuedCouponId(),
                    obRequest.couponDiscount(),
                    obRequest.pointUsed(),
                    obRequest.finalUnitPrice(),
                    obRequest.totalPrice()
            );

            orderedBookRepository.save(orderedBook);
        }

        return new OrderSaveResponse(orderNumber, request.totalPrice());
    }

    @Transactional(readOnly = true)
    public Order getOrderEntity(String orderNumber) {
        return orderRepository.findByOrderNumberFetch(orderNumber)
                .orElseThrow(NotFoundOrderNumberException::new);
    }

    @Transactional(readOnly = true)
    public void verifyOrderStock(String orderNumber) {
        Order order = orderRepository.findByOrderNumber(orderNumber).orElseThrow(NotFoundOrderNumberException::new);

        List<OrderedBook> items = orderedBookRepository.findAllByOrder(order);

        for (OrderedBook item : items) {
            Book book = item.getBook();

            if (!book.hasStock(item.getQuantity())) {
                throw new InsufficientStockException();
            }
        }
    }

    @Transactional
    public void applyPaymentSuccess(String orderNumber) {
        Order order = orderRepository.findByOrderNumber(orderNumber).orElseThrow(NotFoundOrderNumberException::new);

        List<OrderedBook> orderedBooks = orderedBookRepository.findAllByOrder((order));

        for (OrderedBook item : orderedBooks) {
            item.markPaymentCompleted();

            Book book = item.getBook();
            book.decreaseStock(item.getQuantity());
            log.info("책 주문 상태 {} {}", book.getId(), item.getUnitStatus());
        }

        order.markPaymentSuccess();
        log.info("결제 및 주문 완료");
    }

    @Transactional
    public void applyPaymentFail(String orderNumber) {
        Order order = orderRepository.findByOrderNumber(orderNumber).orElseThrow(NotFoundOrderNumberException::new);

        List<OrderedBook> orderedBooks = orderedBookRepository.findAllByOrder((order));

        for (OrderedBook item : orderedBooks) {
            item.markPaymentFailed();
        }

        order.markPaymentFailed();
        log.info("결제 및 주문 실패");
    }

    @Transactional
    public void applyOrderCancel(PaymentCancelResponse response) {

        Order order = orderRepository.findByOrderNumber(response.orderNumber())
                .orElseThrow(NotFoundOrderNumberException::new);

        List<OrderedBook> orderedBooks = orderedBookRepository.findAllByOrder(order);

        Map<Long, Integer> canceledMap = response.canceledBooks().stream()
                .collect(Collectors.toMap(
                        CanceledBooksRequest::orderedBookId,
                        CanceledBooksRequest::canceledQuantity
                ));

        for (OrderedBook item : orderedBooks) {

            long id = item.getId();

            // 취소 요청에 포함x
            if (!canceledMap.containsKey(id)) {
                continue;
            }

            int qty = canceledMap.get(id);

            // 주문 상품 상태 변경
            item.markCanceled();

            // 재고 복구
            Book book = item.getBook();
            book.increaseStock(qty);

            log.info("[ORDER] 취소 처리 - orderedBookId={}, bookId={}, qty={}",
                    id, book.getId(), qty);
        }

        // 전부 취소 이면
        boolean allCanceled = orderedBooks.stream()
                .allMatch(ob -> ob.getUnitStatus() == OrderedBookStatusType.CANCELED);

        // 취소로 변경
        if (allCanceled) {
            order.markCanceled();
        }
        // 아님 그대로 유지
    }
    @Transactional
    public void applyOrderReturnRequest(List<OrderedBook> returnBooks) {
        for (OrderedBook item : returnBooks) {
            item.markReturnRequest();
        }
    }

    @Transactional
    public void applyOrderReturn(String orderNumber, List<OrderedBook> returnBooks) {

        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(NotFoundOrderNumberException::new);

        // 승인된 책 상태 변경 및 재고 복구
        for (OrderedBook item : returnBooks) {
            item.markReturned();

            Book book = item.getBook();
            book.increaseStock(item.getQuantity());

            log.info("[ORDER] 반품 승인 - orderedBookId={}, bookId={}, qty={}",
                    item.getId(), book.getId(), item.getQuantity());
        }

        // 전체 반품인지 부분반품인지 체크
        boolean allReturned = order.getOrderedBooks().stream()
                .allMatch(ob -> ob.getUnitStatus() == OrderedBookStatusType.RETURNED);

        if (allReturned) {
            order.markReturned();
            log.info("[ORDER] 전체 반품 완료 - orderNumber={}", orderNumber);
        }
    }

    @Transactional
    public void applyOrderedBookShipping(Long orderedBookId) {
        OrderedBook ob = orderedBookRepository.findById(orderedBookId)
                .orElseThrow(NotFoundOrderNumberException::new);

        // 개별 상품 배송 시작
        ob.markShipping();

        // 주문 대표 상태 변경
        Order order = ob.getOrder();
        order.markShipping();

        log.info("[ORDER] 개별 상품 배송 시작 - orderedBookId={}, orderNumber={}", orderedBookId, order.getOrderNumber());
        eventPublisher.publishShippingStarted(new ShippingStartedEvent(order.getOrderNumber(),ob.getBook().getTitle()));
    }
}
