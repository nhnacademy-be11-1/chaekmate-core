package shop.chaekmate.core.book.dto.rabbit;

import lombok.Builder;

@Builder
public record BookDeleteMqRequest(
        String dtoType,
        Long id
) {
    public static BookDeleteMqRequest of(Long id){
        return BookDeleteMqRequest.builder()
                .dtoType("BOOK_DELETE")
                .id(id)
                .build();
    }
}
