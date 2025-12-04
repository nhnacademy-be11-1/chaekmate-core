package shop.chaekmate.core.common.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "rabbitmq.book")
public class RabbitBookProperties {

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
        private String queueName;
        private String routingKey;
    }
}
