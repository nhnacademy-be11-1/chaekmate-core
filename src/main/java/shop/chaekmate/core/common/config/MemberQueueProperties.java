package shop.chaekmate.core.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "rabbitmq.member")
public record MemberQueueProperties(
        String exchange
) {
}
