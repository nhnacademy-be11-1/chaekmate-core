package shop.chaekmate.core.payment.dto.request;

import java.util.List;

public record DoorayMessageRequest(
        String botName,
        String text,
        List<DoorayAttachment> attachments
) {
}
