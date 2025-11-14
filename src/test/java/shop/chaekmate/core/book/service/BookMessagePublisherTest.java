package shop.chaekmate.core.book.service;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.test.context.ActiveProfiles;
import shop.chaekmate.core.book.dto.rabbit.BookTaskMqMapping;
import shop.chaekmate.core.book.dto.rabbit.EventType;
import shop.chaekmate.core.common.config.RabbitProperties;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class BookMessagePublisherTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private DirectExchange exchange;

    @Mock
    private RabbitProperties rabbitProperties;

    @Mock
    private RabbitProperties.Queues queues;

    private BookMessagePublisher publisher;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        when(exchange.getName()).thenReturn("book.exchange");
        when(rabbitProperties.getQueues()).thenReturn(queues);
        when(queues.getRoutingKey()).thenReturn("book.routingKey");

        publisher = new BookMessagePublisher(
                rabbitProperties,
                rabbitTemplate,
                exchange,
                new Jackson2JsonMessageConverter()
        );
    }

    @Test
    void 메시지_전송_테스트() {
        // given
        EventType eventType = EventType.INSERT;
        TestRequest request = new TestRequest("123");

        // when
        publisher.sendBookTaskMessage(eventType, request);

        // then
        var captor = ArgumentCaptor.forClass(BookTaskMqMapping.class);


        verify(rabbitTemplate).convertAndSend(
                eq("book.exchange"),
                eq("book.routingKey"),
                captor.capture()
        );

        BookTaskMqMapping<?> sentMessage = captor.getValue();

        // 메시지 내부값 검증
        assert sentMessage.eventType() == EventType.INSERT;
        assert ((TestRequest) sentMessage.taskData()).bookId.equals("123");
    }

    // 테스트용 DTO
    record TestRequest(String bookId) {}
}
