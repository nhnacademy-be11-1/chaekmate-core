package shop.chaekmate.core.external.aladin;

public enum AladinSearchType {
    TITLE("Title"),
    AUTHOR("Author"),
    PUBLISHER("Publisher"),
    TITLE_AUTHOR("Keyword")
    ;

    private final String value;

    AladinSearchType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
