package shop.chaekmate.core.common.config;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
@RequiredArgsConstructor
public class RabbitBookConfig {

    private final RabbitBookProperties bookProperties;
    private final Environment env;

    @Bean("bookExchange")
    public DirectExchange bookExchange() {
        return new DirectExchange(bookProperties.getExchange().getName());
    }

    @Bean("bookQueue")
    public Queue bookQueue() {
        int port = Integer.parseInt(env.getProperty("server.port", "8080")); // default 8080
        String queueName = (port % 2 == 1)
                ? bookProperties.getQueues().getQueueNameOdd()
                : bookProperties.getQueues().getQueueNameEven();
        return new Queue(queueName, true);
    }

    @Bean
    public Binding bookBinding(@Qualifier("bookQueue") Queue bookQueue,
                               @Qualifier("bookExchange") DirectExchange bookExchange) {
        int port = Integer.parseInt(env.getProperty("server.port", "8080"));
        String routingKey = (port % 2 == 1)
                ? bookProperties.getQueues().getRoutingKeyOdd()
                : bookProperties.getQueues().getRoutingKeyEven();

        return BindingBuilder.bind(bookQueue)
                .to(bookExchange)
                .with(routingKey);
    }
}
