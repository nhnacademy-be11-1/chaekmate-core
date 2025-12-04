package shop.chaekmate.core.common.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "rabbitmq.cart")
public class RabbitCartProperties {

    private Exchange exchange;
    private Queues queues;

    @Getter
    @Setter
    public static class Exchange {
        private String name;
    }

    @Getter
    @Setter
    public static class Queues {
        private String loginQueueName;
        private String logoutQueueName;
        private String loginRoutingKey;
        private String logoutRoutingKey;
    }
}
