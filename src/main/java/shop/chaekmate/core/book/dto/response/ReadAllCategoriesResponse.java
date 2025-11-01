package shop.chaekmate.core.book.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;


@Getter
@Schema(description = "전체 카테고리 조회 응답")
public class ReadAllCategoriesResponse {
    @Schema(description = "카테고리 ID", example = "12")
    private final Long id;

    @Schema(description = "카테고리명", example = "소설")
    private final String name;

    @Schema(description = "자식 카테고리 목록")
    private final List<ReadAllCategoriesResponse> children;

    public ReadAllCategoriesResponse(Long id, String name) {
        this.id = id;
        this.name = name;
        this.children = new ArrayList<>();
    }

    public void addChild(ReadAllCategoriesResponse child) {
        this.children.add(child);
    }
}
