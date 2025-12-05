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
public class RabbitCartConfig {

    private final RabbitCartProperties cartProperties;

    @Bean("cartExchange")
    public DirectExchange cartExchange() {
        return new DirectExchange(cartProperties.getExchange().getName());
    }

    @Bean("loginQueue")
    public Queue loginQueue() {
        return new Queue(cartProperties.getQueues().getLoginQueueName(), true);
    }

    @Bean("logoutQueue")
    public Queue logoutQueue() {
        return new Queue(cartProperties.getQueues().getLogoutQueueName(), true);
    }

    @Bean
    public Binding loginBinding(@Qualifier("loginQueue") Queue loginQueue,
                                @Qualifier("cartExchange") DirectExchange cartExchange) {
        return BindingBuilder.bind(loginQueue)
                .to(cartExchange)
                .with(cartProperties.getQueues().getLoginRoutingKey());
    }

    @Bean
    public Binding logoutBinding(@Qualifier("logoutQueue") Queue logoutQueue,
                                 @Qualifier("cartExchange") DirectExchange cartExchange) {
        return BindingBuilder.bind(logoutQueue)
                .to(cartExchange)
                .with(cartProperties.getQueues().getLogoutRoutingKey());
    }
}
