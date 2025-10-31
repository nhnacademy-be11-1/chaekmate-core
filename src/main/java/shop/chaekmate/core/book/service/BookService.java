package shop.chaekmate.core.book.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.chaekmate.core.book.dto.request.BookCreateRequest;
import shop.chaekmate.core.book.dto.request.BookUpdateRequest;
import shop.chaekmate.core.book.dto.response.BookResponse;
import shop.chaekmate.core.book.entity.*;
import shop.chaekmate.core.book.exception.*;
import shop.chaekmate.core.book.repository.*;
import shop.chaekmate.core.external.aladin.AladinBook;
import shop.chaekmate.core.external.aladin.AladinClient;
import shop.chaekmate.core.external.aladin.AladinSearchType;
import shop.chaekmate.core.external.aladin.dto.request.AladinBookRegisterRequest;
import shop.chaekmate.core.external.aladin.dto.response.AladinApiResponse;
import shop.chaekmate.core.external.aladin.dto.response.BookSearchResponse;
import shop.chaekmate.core.member.repository.AdminRepository;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final BookImageRepository bookImageRepository;
    private final AdminRepository adminRepository;
    private final BookCategoryRepository bookCategoryRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;
    private final BookTagRepository bookTagRepository;

    private final AladinClient aladinClient;

    @Value("${aladin.api.key}")
    private String aladinApiKey;

    @Transactional
    public void createBook(BookCreateRequest request) {
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

        bookRepository.save(book);

        BookImage bookImage = new BookImage(book, request.imageUrl());
        bookImageRepository.save(bookImage);

        List<Category> categories = categoryRepository.findAllById(request.categoryIds());

        if (categories.size() != request.categoryIds().size()) {
            throw new CategoryNotFoundException("일부 카테고리 ID를 찾을 수 없습니다.");
        }

        for (Category category : categories) {
            bookCategoryRepository.save(new BookCategory(book, category));
        }

        List<Tag> tags = tagRepository.findAllById(request.tagIds());

        if (tags.size() != request.tagIds().size()) {
            throw new TagNotFoundException("일부 태그 ID를 찾을 수 없습니다.");
        }

        for (Tag tag : tags) {
            bookTagRepository.save(new BookTag(book, tag));
        }
    }

    @Transactional
    public void updateBook(Long bookId, BookUpdateRequest request) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException(String.format("Book id %s not found", bookId)));

        book.update(request);

        BookImage bookImage = bookImageRepository.findByBookId(bookId)
                .orElse(new BookImage(book, request.imageUrl())); // 이미지 교체
        bookImage.updateUrl(request.imageUrl());

        bookImageRepository.save(bookImage);

        // 책 카테고리 업데이트
        if (request.categoryIds() != null) {
            updateBookCategory(book, request.categoryIds());
        }

        // 책 태그 업데이트
        if (request.tagIds() != null) {
            updateBookTag(book, request.tagIds());
        }
    }

    @Transactional
    public void deleteBook(Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException(String.format("삭제할 책을 찾을 수 없습니다. Book id : %d", bookId)));

        bookRepository.delete(book);
    }

    public BookResponse getBook(Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException(String.format("Book id %d not found", bookId)));

        // 책 이미지 없는 책 있음
        BookImage bookImage = bookImageRepository.findByBookId(bookId)
                .orElse(null);

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

        String imageUrl = null;
        if (bookImage != null) {
            imageUrl = bookImage.getImageUrl();
        }
        return BookResponse.from(book, imageUrl, categoryIds, tagIds);
    }

    public Page<BookResponse> getBookList(Pageable pageable) {
        Page<Book> bookPage = bookRepository.findAll(pageable);

        List<BookResponse> bookResponses = new ArrayList<>();

        for (Book book : bookPage.getContent()) {
            BookImage bookImage = bookImageRepository.findByBookId(book.getId())
                    .orElseThrow(() -> new BookImageNotFoundException("책 이미지를 찾을 수 없습니다."));

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

            bookResponses.add(BookResponse.from(book, bookImage.getImageUrl(), categoryIds, tagIds));
        }

        return new PageImpl<>(bookResponses, pageable, bookPage.getTotalElements());
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
    public void registerFromAladin(Long adminId, AladinBookRegisterRequest request) {
        if (!adminRepository.existsById(adminId)) {
            throw new AdminNotFoundException("관리자를 찾을 수 없습니다: " + adminId);
        }

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
            throw new CategoryNotFoundException("일부 카테고리를 찾을 수 없습니다.");
        }

        for (Category category : categories) {
            BookCategory bookCategory = new BookCategory(book, category);
            bookCategoryRepository.save(bookCategory);
        }

        if (request.tagIds() != null && !request.tagIds().isEmpty()) {
            List<Tag> tags = tagRepository.findAllById(request.tagIds());

            if (tags.size() != request.tagIds().size()) {
                throw new TagNotFoundException("일부 태그를 찾을 수 없습니다.");
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
                throw new CategoryNotFoundException("일부 카테고리를 찾을 수 없습니다.");
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
                throw new TagNotFoundException("일부 태그를 찾을 수 없습니다.");
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
