package shop.chaekmate.core.book.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.chaekmate.core.book.dto.request.BookCreateRequest;
import shop.chaekmate.core.book.dto.request.BookSearchCondition;
import shop.chaekmate.core.book.dto.request.BookUpdateRequest;
import shop.chaekmate.core.book.dto.response.BookCreateResponse;
import shop.chaekmate.core.book.dto.response.BookListResponse;
import shop.chaekmate.core.book.dto.response.BookResponse;
import shop.chaekmate.core.book.dto.response.BookSummaryResponse;
import shop.chaekmate.core.book.entity.*;
import shop.chaekmate.core.book.event.BookCreatedEvent;
import shop.chaekmate.core.book.event.BookDeletedEvent;
import shop.chaekmate.core.book.event.BookUpdatedEvent;
import shop.chaekmate.core.book.exception.BookNotFoundException;
import shop.chaekmate.core.book.exception.CategoryNotFoundException;
import shop.chaekmate.core.book.exception.TagNotFoundException;
import shop.chaekmate.core.book.repository.*;
import shop.chaekmate.core.external.aladin.AladinBook;
import shop.chaekmate.core.external.aladin.AladinClient;
import shop.chaekmate.core.external.aladin.AladinSearchType;
import shop.chaekmate.core.external.aladin.dto.request.AladinBookRegisterRequest;
import shop.chaekmate.core.external.aladin.dto.response.AladinApiResponse;
import shop.chaekmate.core.external.aladin.dto.response.BookSearchResponse;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final BookImageRepository bookImageRepository;
    private final BookCategoryRepository bookCategoryRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;
    private final BookTagRepository bookTagRepository;

    private final AladinClient aladinClient;

    // 트랜잭션 끝 난 뒤 서비스 호출 이벤트 발행
    private final ApplicationEventPublisher eventPublisher;

    private final StringRedisTemplate redisTemplate;
    private static final String VIEW_KEY_PREFIX = "book:views:";

    @Value("${aladin.api.key}")
    private String aladinApiKey;

    @Transactional
    public BookCreateResponse createBook(BookCreateRequest request) {
        if (bookRepository.existsByIsbn(request.isbn())) {
            throw new IllegalArgumentException("이미 등록된 ISBN입니다: " + request.isbn());
        }

        Book book = Book.builder()
                .title(request.title())
                .index(request.index())
                .description(request.description())
                .author(request.author())
                .publisher(request.publisher())
                .publishedAt(request.publishedAt())
                .isbn(request.isbn())
                .price(request.price())
                .salesPrice(request.salesPrice())
                .isWrappable(request.isWrappable())
                .views(0)
                .isSaleEnd(request.isSaleEnd())
                .stock(request.stock())
                .build();

        Book saved = bookRepository.save(book);

        List<Category> categories = categoryRepository.findAllById(request.categoryIds());

        if (categories.size() != request.categoryIds().size()) {
            throw new CategoryNotFoundException();
        }

        List<Tag> tags = tagRepository.findAllById(request.tagIds());

        if (tags.size() != request.tagIds().size()) {
            throw new TagNotFoundException();
        }

        List<BookCategory> bookCategories = categories.stream().map(c -> new BookCategory(book,c)).toList();
        bookCategoryRepository.saveAll(bookCategories);

        List<BookTag> bookTags = tags.stream().map(t -> new BookTag(book, t)).toList();
        bookTagRepository.saveAll(bookTags);


        // RabbitMQ 이벤트 발행
        eventPublisher.publishEvent(new BookCreatedEvent(saved));

        return new BookCreateResponse(book.getId());
    }

    @Transactional
    public void updateBook(Long bookId, BookUpdateRequest request) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(BookNotFoundException::new);

        book.update(request);

        // 책 카테고리 업데이트
        if (request.categoryIds() != null) {
            updateBookCategory(book, request.categoryIds());
        }

        // 책 태그 업데이트
        if (request.tagIds() != null) {
            updateBookTag(book, request.tagIds());
        }

        // 책 업데이트 이벤트 발행
        eventPublisher.publishEvent(new BookUpdatedEvent(book));
    }

    @Transactional
    public void deleteBook(Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(BookNotFoundException::new);

        // 북 속성 엔티티들 삭제
        bookCategoryRepository.deleteAll(bookCategoryRepository.findByBook(book));
        bookImageRepository.deleteAll(bookImageRepository.findByBook(book));
        bookTagRepository.deleteAll(bookTagRepository.findByBook(book));

        // 북 삭제
        bookRepository.delete(book);

        // 삭제 이벤트 발행 (검색서버 동기화)
        eventPublisher.publishEvent(new BookDeletedEvent(bookId));

    }

    public BookResponse getBook(Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(BookNotFoundException::new);

        List<BookCategory> bookCategories = bookCategoryRepository.findByBook(book);
        List<Long> categoryIds = new ArrayList<>();
        for (BookCategory bookCategory : bookCategories) {
            categoryIds.add(bookCategory.getCategory().getId());
        }

        List<BookTag> bookTags = bookTagRepository.findByBook(book);
        List<Long> tagIds = new ArrayList<>();
        for (BookTag bookTag : bookTags) {
            tagIds.add(bookTag.getTag().getId());
        }

        // 레디스에 캐시된 조회수 있으면 증가시킴
        String redisKey = VIEW_KEY_PREFIX + bookId;
        String redisValue = redisTemplate.opsForValue().get(redisKey);
        long increment = 0L;
        if (redisValue != null) {
            try {
                increment = Long.parseLong(redisValue);
            } catch (NumberFormatException e) {
                // 잘못된 값이면 무시
            }
        }
        long totalViews = book.getViews() + increment;


        return BookResponse.from(book, categoryIds, tagIds, totalViews);
    }

    public List<BookSummaryResponse> getBooksByIds(List<Long> bookIds) {
        List<Book> books = bookRepository.findAllById(bookIds);

        Map<Long, Book> bookMap = new HashMap<>();
        for (Book book : books) {
            bookMap.put(book.getId(), book);
        }

        List<BookSummaryResponse> result = new ArrayList<>();
        for (Long bookId : bookIds) {
            Book book = bookMap.get(bookId);

            if (book != null) {
                result.add(BookSummaryResponse.from(book));
            }
        }

        return result;
    }

    public List<BookSummaryResponse> getBooksByIds(List<Long> bookIds) {
        List<Book> books = bookRepository.findAllById(bookIds);

        Map<Long, Book> bookMap = new HashMap<>();
        for (Book book : books) {
            bookMap.put(book.getId(), book);
        }

        List<BookSummaryResponse> result = new ArrayList<>();
        for (Long bookId : bookIds) {
            Book book = bookMap.get(bookId);

            if (book != null) {
                result.add(BookSummaryResponse.from(book));
            }
        }

        return result;
    }

    public Page<BookListResponse> getBookList(BookSearchCondition condition, Pageable pageable) {
        return bookRepository.searchBooks(condition, pageable);
    }

    public Page<BookSearchResponse> searchFromAladin(String query, AladinSearchType searchType, Pageable pageable) {
        int pageSize = pageable.getPageSize();
        int pageNumber = pageable.getPageNumber();
        int aladinPage = pageNumber + 1;

        AladinApiResponse apiResponse = aladinClient.searchBooks(
                aladinApiKey,
                query,
                searchType.getValue(),
                pageSize,
                aladinPage,
                "js",
                "20131101"
        );

        if (apiResponse == null || apiResponse.items() == null) {
            return new PageImpl<>(new ArrayList<>(), pageable, 0);
        }

        List<BookSearchResponse> books = new ArrayList<>();

        for (AladinBook item : apiResponse.items()) {
            BookSearchResponse result = new BookSearchResponse(
                    item.title(),
                    item.author(),
                    item.publisher(),
                    item.pubDate(),
                    item.isbn13(),
                    item.priceStandard(),
                    item.priceSales(),
                    item.cover(),
                    item.description(),
                    item.categoryName()
            );
            books.add(result);
        }

        return new PageImpl<>(books, pageable, apiResponse.totalResults());
    }

    @Transactional
    public void registerFromAladin(AladinBookRegisterRequest request) {
        if (bookRepository.existsByIsbn(request.isbn())) {
            throw new IllegalArgumentException("이미 등록된 ISBN입니다: " + request.isbn());
        }

        LocalDateTime publishedAt = LocalDateTime.parse(
                request.publishedAt() + "T00:00:00"
        );

        Book book = Book.builder()
                .title(request.title())
                .author(request.author())
                .publisher(request.publisher())
                .publishedAt(publishedAt)
                .isbn(request.isbn())
                .description(request.description())
                .index(request.index())
                .price(request.price())
                .salesPrice(request.salesPrice())
                .stock(request.stock())
                .isWrappable(request.isWrappable())
                .isSaleEnd(request.isSaleEnd())
                .views(0)
                .build();

        bookRepository.save(book);

        BookImage bookImage = new BookImage(book, request.imageUrl());
        bookImageRepository.save(bookImage);

        List<Category> categories = categoryRepository.findAllById(request.categoryIds());

        if (categories.size() != request.categoryIds().size()) {
            throw new CategoryNotFoundException();
        }

        for (Category category : categories) {
            BookCategory bookCategory = new BookCategory(book, category);
            bookCategoryRepository.save(bookCategory);
        }

        if (request.tagIds() != null && !request.tagIds().isEmpty()) {
            List<Tag> tags = tagRepository.findAllById(request.tagIds());

            if (tags.size() != request.tagIds().size()) {
                throw new TagNotFoundException();
            }

            for (Tag tag : tags) {
                BookTag bookTag = new BookTag(book, tag);
                bookTagRepository.save(bookTag);
            }
        }
    }

    private void updateBookCategory(Book book, List<Long> newCategoryIds) {
        // 기존에 책과 연결된 카테고리 넣음
        Set<Long> existingCategoryIds = new HashSet<>();
        List<BookCategory> existingBookCategories = bookCategoryRepository.findByBook(book);

        for (BookCategory existingBookCategory : existingBookCategories) {
            existingCategoryIds.add(existingBookCategory.getCategory().getId());
        }

        // 새로운 카테고리 아이디들만 넣음
        Set<Long> newIds = new HashSet<>(newCategoryIds);

        Set<Long> idsToAdd = new HashSet<>(newIds);
        idsToAdd.removeAll(existingCategoryIds);

        if (!idsToAdd.isEmpty()) {
            // 카테고리 테이블에서 새로 더할 카테고리들 가져오기
            List<Category> categoriesToAdd = categoryRepository.findAllById(idsToAdd);

            if (categoriesToAdd.size() != idsToAdd.size()) {
                throw new CategoryNotFoundException();
            }

            // 책과 매핑해서 담기
            for (Category category : categoriesToAdd) {
                bookCategoryRepository.save(new BookCategory(book, category));
            }
        }

        // 삭제할 카테고리 아이디들 담기
        Set<Long> idsToRemove = new HashSet<>(existingCategoryIds);
        idsToRemove.removeAll(newIds);


        // 삭제할 책과 연관된 카테고리들 삭제
        if (!idsToRemove.isEmpty()) {
            bookCategoryRepository.deleteByBookIdAndCategoryIdIn(book.getId(), idsToRemove);
        }
    }

    private void updateBookTag(Book book, List<Long> newTagIds) {
        Set<Long> existingTagIds = new HashSet<>();
        List<BookTag> existingBookTags = bookTagRepository.findByBook(book);

        for (BookTag existingBookTag : existingBookTags) {
            existingTagIds.add(existingBookTag.getTag().getId());
        }

        Set<Long> newIds = new HashSet<>(newTagIds);

        Set<Long> idsToAdd = new HashSet<>(newIds);
        idsToAdd.removeAll(existingTagIds);

        if (!idsToAdd.isEmpty()) {
            List<Tag> tagsToAdd = tagRepository.findAllById(idsToAdd);

            if (tagsToAdd.size() != idsToAdd.size()) {
                throw new TagNotFoundException();
            }

            for (Tag tag : tagsToAdd) {
                bookTagRepository.save(new BookTag(book, tag));
            }
        }

        Set<Long> idsToRemove = new HashSet<>(existingTagIds);
        idsToRemove.removeAll(newIds);

        if (!idsToRemove.isEmpty()) {
            bookTagRepository.deleteByBookIdAndTagIdIn(book.getId(), idsToRemove);
        }
    }
}
