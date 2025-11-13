package shop.chaekmate.core.book.dto.rabbit;

import com.fasterxml.jackson.annotation.JsonProperty;

public record BookMqMapping(
        EventType eventType,
        @JsonProperty("taskData")
        BookMqRequest bookMqRequest
) {
}
