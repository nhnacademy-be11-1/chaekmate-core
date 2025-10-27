package shop.chaekmate.core.book.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReadAllCategoriesResponse {
    private Long id;
    private String name;
    private List<ReadAllCategoriesResponse> children;

    public ReadAllCategoriesResponse(Long id, String name) {
        this.id = id;
        this.name = name;
        this.children = new ArrayList<>();
    }

    public void addChild(ReadAllCategoriesResponse child) {
        this.children.add(child);
    }
}
