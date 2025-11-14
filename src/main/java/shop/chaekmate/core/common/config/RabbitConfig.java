package shop.chaekmate.core.common.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    private final String exchangeName;
    private final String queueName;
    private final String routingKey;

    public RabbitConfig(RabbitProperties rabbitProperties) {
        this.exchangeName = rabbitProperties.getExchange().getName();
        this.queueName = rabbitProperties.getQueues().getQueueName();
        this.routingKey = rabbitProperties.getQueues().getRoutingKey();
    }

    @Bean
    public DirectExchange bookExchange() {
        return new DirectExchange(exchangeName);
    }

    @Bean
    public Queue bookQueue() {
        return new Queue(queueName, true); // durable
    }

    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public Binding binding(Queue bookQueue, DirectExchange bookExchange) {
        return BindingBuilder.bind(bookQueue)
                .to(bookExchange)
                .with(routingKey);
    }
}
