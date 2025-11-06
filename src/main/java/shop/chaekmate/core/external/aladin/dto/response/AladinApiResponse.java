package shop.chaekmate.core.external.aladin.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import shop.chaekmate.core.external.aladin.AladinBook;

import java.util.List;

public record AladinApiResponse(
        @JsonProperty("totalResults")
        Integer totalResults,

        @JsonProperty("startIndex")
        Integer startIndex,

        @JsonProperty("itemsPerPage")
        Integer itemsPerPage,

        @JsonProperty("item")
        List<AladinBook> items
) {
}
