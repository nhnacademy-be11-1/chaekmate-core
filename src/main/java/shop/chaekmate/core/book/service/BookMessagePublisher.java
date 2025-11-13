package shop.chaekmate.core.book.service;

import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import shop.chaekmate.core.book.dto.rabbit.BookMqMapping;
import shop.chaekmate.core.book.dto.rabbit.BookMqRequest;
import shop.chaekmate.core.book.dto.rabbit.EventType;
import shop.chaekmate.core.common.config.RabbitProperties;

@Service
public class BookMessagePublisher {
    // RabbitMQ 사용, 책 등록 시 검색 서버와 동기화

    private final RabbitTemplate rabbitTemplate;
    private final DirectExchange exchange;
    private final String routingKey;

    public BookMessagePublisher(
            RabbitProperties rabbitProperties,
            RabbitTemplate rabbitTemplate, DirectExchange exchange) {
        this.rabbitTemplate = rabbitTemplate;
        this.exchange = exchange;
        this.routingKey = rabbitProperties.getQueues().getRoutingKey();
    }


    public void sendBookRegisterMessage(EventType eventType, BookMqRequest bookMqRequest){
        rabbitTemplate.convertAndSend(
                exchange.getName(),
                routingKey,
                new BookMqMapping(eventType, bookMqRequest)
        );
    }
}
