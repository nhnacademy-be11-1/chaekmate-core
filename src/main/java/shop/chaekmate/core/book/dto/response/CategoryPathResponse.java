package shop.chaekmate.core.book.dto.response;

public record CategoryPathResponse(
        Long id,
        String name,
        int depth
) {
}
