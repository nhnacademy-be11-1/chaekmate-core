package shop.chaekmate.core.book.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import shop.chaekmate.core.book.controller.docs.AdminBookControllerDocs;
import shop.chaekmate.core.book.dto.request.AdminBookPagedRequest;
import shop.chaekmate.core.book.dto.response.AdminBookResponse;
import shop.chaekmate.core.book.dto.response.PageResponse;
import shop.chaekmate.core.book.service.AdminBookService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AdminBookController implements AdminBookControllerDocs {

    private final AdminBookService adminBookService;

    @Override
    @GetMapping("/admin/books/recent")
    public ResponseEntity<List<AdminBookResponse>> getRecentBooks(@RequestParam int limit) {
        List<AdminBookResponse> recentBooks = adminBookService.findRecentBooks(limit);
        return ResponseEntity.ok(recentBooks);
    }

    // 관리자용 도서 검색
    @GetMapping("/admin/books/paged")
    public ResponseEntity<PageResponse<AdminBookResponse>> getAdminBooksPaged(AdminBookPagedRequest request){

        return ResponseEntity.ok(PageResponse.from(adminBookService.findBooks(request)));
    }

}
