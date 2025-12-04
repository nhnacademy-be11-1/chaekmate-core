package shop.chaekmate.core.payment.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import shop.chaekmate.core.payment.client.DoorayClient;
import shop.chaekmate.core.payment.client.DoorayMessageType;
import shop.chaekmate.core.payment.dto.request.DoorayAttachment;
import shop.chaekmate.core.payment.dto.request.DoorayMessageRequest;

@Service
@Slf4j
@RequiredArgsConstructor
public class DoorayService {
    private final DoorayClient doorayClient;

    public void sendMessage(String orderNumber,List<DoorayMessageType> items) {

        List<DoorayAttachment> attachments = items.stream()
                .map(item -> new DoorayAttachment(
                        "주문번호:" + orderNumber,
                        item.text(),
                        null,
                        "https://static.dooray.com/static_images/dooray-bot.png",
                        item.color()
                )).toList();

        DoorayMessageRequest request = new DoorayMessageRequest(
                "Chaekmate 주문",
                null,
                attachments
        );

      doorayClient.sendMessage(request);
    }
}

