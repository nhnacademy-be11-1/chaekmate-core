package shop.chaekmate.core.external.aladin;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AladinBook(
        @JsonProperty("title")
        String title,

        @JsonProperty("author")
        String author,

        @JsonProperty("publisher")
        String publisher,

        @JsonProperty("pubDate")
        String pubDate,

        @JsonProperty("isbn13")
        String isbn13,

        @JsonProperty("priceStandard")
        Integer priceStandard,

        @JsonProperty("priceSales")
        Integer priceSales,

        @JsonProperty("cover")
        String cover,

        @JsonProperty("description")
        String description,

        @JsonProperty("categoryName")
        String categoryName
) {
}
