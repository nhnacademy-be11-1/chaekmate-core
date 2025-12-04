package shop.chaekmate.core.common.config;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(MemberQueueProperties.class)
public class MemberRabbitConfig {

    private final MemberQueueProperties memberQueueProperties;

    @Bean
    public FanoutExchange memberFanoutExchange() {
        return new FanoutExchange(
                memberQueueProperties.exchange(),
                true,
                false
        );
    }
}
