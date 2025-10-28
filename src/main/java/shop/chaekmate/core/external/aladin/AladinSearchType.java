package shop.chaekmate.core.external.aladin;

public enum AladinSearchType {
    TITLE("제목"),
    AUTHOR("저자"),
    PUBLISHER("출판사"),
    TITLE_AUTHOR("제목+저자")
    ;

    private final String value;

    AladinSearchType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
