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
import shop.chaekmate.core.book.repository.BookRepository;
import shop.chaekmate.core.member.entity.Member;
import shop.chaekmate.core.member.repository.MemberRepository;
import shop.chaekmate.core.order.dto.request.OrderSaveRequest;
import shop.chaekmate.core.order.dto.request.OrderedBookSaveRequest;
import shop.chaekmate.core.order.dto.response.OrderSaveResponse;
import shop.chaekmate.core.order.entity.Order;
import shop.chaekmate.core.order.entity.OrderedBook;
import shop.chaekmate.core.order.entity.Wrapper;
import shop.chaekmate.core.order.exception.WrapperNotFoundException;
import shop.chaekmate.core.order.repository.OrderRepository;
import shop.chaekmate.core.order.repository.OrderedBookRepository;
import shop.chaekmate.core.order.repository.WrapperRepository;
import shop.chaekmate.core.payment.dto.response.impl.PaymentApproveResponse;
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

    @Transactional
    public OrderSaveResponse createOrder(Long memberId, OrderSaveRequest request) {

        //주문번호
        String orderNumber = NanoIdUtils.randomNanoId();

        // null -> 비회원
        Member member = null;

        if (memberId != null) {
            member = memberRepository.findById(memberId).orElseThrow(MemberNotFoundException::new);
        }


        List<Long> bookIds = request.orderedBooks().stream()
                .map(OrderedBookSaveRequest::bookId)
                .toList();

        List<Long> wrapperIds = request.orderedBooks().stream()
                .map(OrderedBookSaveRequest::wrapperId)
                .filter(Objects::nonNull)
                .toList();

        Map<Long, Book> bookMap = bookRepository.findAllById(bookIds)
                .stream()
                .collect(Collectors.toMap(Book::getId, b -> b));

        Map<Long, Wrapper> wrapperMap = wrapperIds.isEmpty()
                ? Map.of()
                : wrapperRepository.findAllById(wrapperIds)
                        .stream()
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
                    throw new WrapperNotFoundException();
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
                    obRequest.finalUnitPrice()
            );

            orderedBookRepository.save(orderedBook);
        }

        return new OrderSaveResponse(orderNumber, request.totalPrice());
    }

}
