package shop.chaekmate.core.book.controller.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import shop.chaekmate.core.book.dto.request.AdminBookPagedRequest;
import shop.chaekmate.core.book.dto.response.AdminBookResponse;

import java.util.List;
import shop.chaekmate.core.book.dto.response.PageResponse;

@Tag(name = "관리자 도서 API", description = "관리자 도서 관련 API")
public interface AdminBookControllerDocs {

    @Operation(summary = "최근 추가된 도서 조회", description = "최근에 추가된 도서를 개수만큼 조회합니다.")
    ResponseEntity<List<AdminBookResponse>> getRecentBooks(int limit);

    @Operation(summary = "관리자용 도서 목록 조회", description = "관리자용 도서 목록을 페이지네이션 조회합니다")
    ResponseEntity<PageResponse<AdminBookResponse>> getAdminBooksPaged(AdminBookPagedRequest request);
}
