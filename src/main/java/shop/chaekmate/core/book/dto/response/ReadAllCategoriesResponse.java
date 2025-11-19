package shop.chaekmate.core.book.dto.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;


@Getter
@Schema(description = "전체 카테고리 조회 응답")
public class ReadAllCategoriesResponse implements Serializable {

    @JsonProperty("id")
    @Schema(description = "카테고리 ID", example = "12")
    private final Long id;

    @JsonProperty("name")
    @Schema(description = "카테고리명", example = "소설")
    private final String name;

    @JsonProperty("children")
    @Schema(description = "자식 카테고리 목록")
    private final List<ReadAllCategoriesResponse> children;

    @JsonCreator
    public ReadAllCategoriesResponse(@JsonProperty("id") Long id,
                                     @JsonProperty("name") String name) {
        this.id = id;
        this.name = name;
        this.children = new ArrayList<>();
    }

    public void addChild(ReadAllCategoriesResponse child) {
        this.children.add(child);
    }
}
