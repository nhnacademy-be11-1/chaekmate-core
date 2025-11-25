package shop.chaekmate.core.book.dto.response;

import java.util.List;
import org.springframework.data.domain.Slice;

public record BookQuerySliceResponse(
    List<BookQueryResponse> content,
    boolean hasNext
) {
    public static BookQuerySliceResponse from(Slice<BookQueryResponse> slice) {
        return new BookQuerySliceResponse(slice.getContent(), slice.hasNext());
    }
}
