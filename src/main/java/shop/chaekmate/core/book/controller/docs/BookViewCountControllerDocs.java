package shop.chaekmate.core.book.controller.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

@Tag(name = "도서 조회수 API", description = "도서 조회수 API")
public interface BookViewCountControllerDocs {

    @Operation(summary = "도서 조회수 증가", description = "특정 도서 조회수 증가")
    @ApiResponse(responseCode = "200", description = "조회수 증가 성공")
    @ApiResponse(responseCode = "400", description = "파라미터 에러")
    ResponseEntity<Void> increaseView(
            @Parameter(description = "도서 ID") @PathVariable Long bookId);

}
