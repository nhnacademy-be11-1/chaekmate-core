package shop.chaekmate.core.book.service;

import java.util.Objects;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import shop.chaekmate.core.book.dto.rabbit.BookTaskMqMapping;
import shop.chaekmate.core.book.dto.rabbit.EventType;
import shop.chaekmate.core.common.config.RabbitBookProperties;

@Service
public class BookMessagePublisher {
    // RabbitMQ 사용, 책 등록 시 검색 서버와 동기화

    private final RabbitTemplate rabbitTemplate;
    private final DirectExchange exchange;
    private final String routingKey;

    public BookMessagePublisher(
            RabbitBookProperties rabbitBookProperties,
            RabbitTemplate rabbitTemplate,
            @Qualifier("bookExchange") DirectExchange exchange, // @Qualifier 추가
            Jackson2JsonMessageConverter jsonMessageConverter,
            Environment env) {

        this.rabbitTemplate = rabbitTemplate;
        rabbitTemplate.setMessageConverter(jsonMessageConverter);
        this.exchange = exchange;
        int port = Integer.parseInt(Objects.requireNonNull(env.getProperty("server.port")));
        this.routingKey = (port % 2 == 1)
                ? rabbitBookProperties.getQueues().getRoutingKeyOdd()
                : rabbitBookProperties.getQueues().getRoutingKeyEven();

    }

    public <T> void sendBookTaskMessage(EventType eventType, T bookMqRequest){
        rabbitTemplate.convertAndSend(
                exchange.getName(),
                routingKey,
                new BookTaskMqMapping<>(eventType, bookMqRequest)
        );
    }
}
