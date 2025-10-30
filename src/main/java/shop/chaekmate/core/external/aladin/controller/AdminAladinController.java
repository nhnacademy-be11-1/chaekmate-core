package shop.chaekmate.core.external.aladin.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shop.chaekmate.core.book.service.BookService;
import shop.chaekmate.core.external.aladin.AladinSearchType;
import shop.chaekmate.core.external.aladin.dto.request.AladinBookRegisterRequest;
import shop.chaekmate.core.external.aladin.dto.response.BookSearchResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/books/aladin")
public class AdminAladinController {

    private final BookService bookService;

    @GetMapping("/search")
    public ResponseEntity<Page<BookSearchResponse>> search(
            @RequestParam String query,
            @RequestParam(defaultValue = "Title") AladinSearchType searchType,
            @PageableDefault(size = 10, page = 0) Pageable pageable) {

        Page<BookSearchResponse> bookSearchResponses = bookService.searchFromAladin(query, searchType, pageable);

        return ResponseEntity.ok(bookSearchResponses);
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid @RequestBody AladinBookRegisterRequest request) {

        bookService.registerFromAladin(request);

        return ResponseEntity.ok().build();
    }
}
