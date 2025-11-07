package shop.chaekmate.core.book.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shop.chaekmate.core.book.controller.docs.BookControllerDocs;
import shop.chaekmate.core.book.dto.request.BookCreateRequest;
import shop.chaekmate.core.book.dto.request.BookSearchCondition;
import shop.chaekmate.core.book.dto.request.BookUpdateRequest;
import shop.chaekmate.core.book.dto.response.BookListResponse;
import shop.chaekmate.core.book.dto.response.BookResponse;
import shop.chaekmate.core.book.exception.InvalidSearchConditionException;
import shop.chaekmate.core.book.service.BookService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/books")
public class BookController implements BookControllerDocs {

    private final BookService bookService;

    @PostMapping
    public ResponseEntity<Void> createBook(@Valid @RequestBody BookCreateRequest request) {
        bookService.createBook(request);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{bookId}")
    public ResponseEntity<Void> updateBook(@PathVariable Long bookId,
                                           @Valid @RequestBody BookUpdateRequest request) {
        bookService.updateBook(bookId, request);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{bookId}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long bookId) {
        bookService.deleteBook(bookId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{bookId}")
    public ResponseEntity<BookResponse> getBook(@PathVariable Long bookId) {
        BookResponse response = bookService.getBook(bookId);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<BookListResponse>> getBookList(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long tagId,
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 12, page = 0) Pageable pageable) {

        validateSearchParameters(categoryId, tagId, keyword);

        BookSearchCondition condition = new BookSearchCondition(
                categoryId,
                tagId,
                keyword
        );

        Page<BookListResponse> page = bookService.getBookList(condition, pageable);

        return ResponseEntity.ok(page);
    }

    private void validateSearchParameters(Long categoryId, Long tagId, String keyword) {
        int paramCount = 0;

        if (categoryId != null) {
            paramCount++;
        }

        if (tagId != null) {
            paramCount++;
        }

        if (keyword != null && !keyword.isBlank()) {
            paramCount++;
        }

        if (paramCount == 0) {
            throw new InvalidSearchConditionException(
                    "검색 조건이 필요합니다. categoryId, tagId, keyword 중 하나를 입력해주세요."
            );
        }

        if (paramCount > 1) {
            throw new InvalidSearchConditionException(
                    "검색 조건은 1개만 사용할 수 있습니다. categoryId, tagId, keyword 중 하나만 입력해주세요."
            );
        }
    }
}
