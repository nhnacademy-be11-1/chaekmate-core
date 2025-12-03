package shop.chaekmate.core.common.config;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class RabbitBookConfig {

    private final RabbitBookProperties bookProperties;

    @Bean("bookExchange")
    public DirectExchange bookExchange() {
        return new DirectExchange(bookProperties.getExchange().getName());
    }

    @Bean("bookQueue")
    public Queue bookQueue() {
        return new Queue(bookProperties.getQueues().getQueueName(), true);
    }

    @Bean
    public Binding bookBinding(@Qualifier("bookQueue") Queue bookQueue,
                               @Qualifier("bookExchange") DirectExchange bookExchange) {
        return BindingBuilder.bind(bookQueue)
                .to(bookExchange)
                .with(bookProperties.getQueues().getRoutingKey());
    }
}
