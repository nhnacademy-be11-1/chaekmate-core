package shop.chaekmate.core.book.dto.rabbit;

public record BookTaskMqMapping<T>(
        EventType eventType,
        T taskData
) {
}
