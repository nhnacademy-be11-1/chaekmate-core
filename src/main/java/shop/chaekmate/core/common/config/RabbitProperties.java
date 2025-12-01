package shop.chaekmate.core.common.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "rabbitmq")
public class RabbitProperties {

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
        private String queueNameOdd;
        private String routingKeyOdd;
        private String queueNameEven;
        private String routingKeyEven;

        @Value("${server.port}")
        private int serverPort;

        public String getRoutingKey() {
            return (serverPort % 2 == 1) ? routingKeyOdd : routingKeyEven;
        }

        public String getQueueName() {
            return (serverPort % 2 == 1) ? queueNameOdd : queueNameEven;
        }
    }
}
