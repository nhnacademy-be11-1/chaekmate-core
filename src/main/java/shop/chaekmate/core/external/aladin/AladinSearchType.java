package shop.chaekmate.core.external.aladin;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AladinSearchType {
    TITLE("Title"),
    AUTHOR("Author"),
    PUBLISHER("Publisher"),
    TITLE_AUTHOR("Keyword")
    ;

    private final String value;
}
