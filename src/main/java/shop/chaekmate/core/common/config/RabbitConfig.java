package shop.chaekmate.core.common.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class RabbitConfig {

    private final RabbitProperties rabbitProperties;
    private final Environment env;

    public RabbitConfig(RabbitProperties rabbitProperties, Environment env) {
        this.rabbitProperties = rabbitProperties;
        this.env = env;
    }

    @Bean
    public DirectExchange bookExchange() {
        return new DirectExchange(rabbitProperties.getExchange().getName());
    }

    @Bean
    public Queue bookQueue() {
        int port = Integer.parseInt(env.getProperty("server.port", "8080")); // default 8080
        String queueName = (port % 2 == 1)
                ? rabbitProperties.getQueues().getQueueNameOdd()
                : rabbitProperties.getQueues().getQueueNameEven();
        return new Queue(queueName, true);
    }

    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public Binding binding(Queue bookQueue, DirectExchange bookExchange) {
        int port = Integer.parseInt(env.getProperty("server.port", "8080"));
        String routingKey = (port % 2 == 1)
                ? rabbitProperties.getQueues().getRoutingKeyOdd()
                : rabbitProperties.getQueues().getRoutingKeyEven();

        return BindingBuilder.bind(bookQueue)
                .to(bookExchange)
                .with(routingKey);
    }
}
