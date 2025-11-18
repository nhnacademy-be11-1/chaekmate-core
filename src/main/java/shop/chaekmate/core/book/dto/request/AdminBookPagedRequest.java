package shop.chaekmate.core.book.dto.request;

import lombok.Data;

@Data
public class AdminBookPagedRequest {

    private int page = 0;
    private int size = 20;
    private BookSortType sortType = BookSortType.RECENT;

    // 제목 검색 조건
    private String keyword;
}
