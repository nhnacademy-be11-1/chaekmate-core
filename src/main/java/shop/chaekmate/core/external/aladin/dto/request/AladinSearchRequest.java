package shop.chaekmate.core.external.aladin.dto.request;

import jakarta.validation.constraints.NotBlank;

public record AladinSearchRequest(
        @NotBlank(message = "검색어는 필수입니다.")
        String query
) {
}
