package shop.chaekmate.core.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

@Configuration
public class RedisConfig {


    @Bean
    public LettuceConnectionFactory redisConnectionFactory(RedisProperties redisProperties) {
        LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
                .build();
        RedisStandaloneConfiguration serverConfig = new RedisStandaloneConfiguration();
        serverConfig.setHostName(redisProperties.getHost());
        serverConfig.setPort(redisProperties.getPort());
        serverConfig.setPassword(RedisPassword.of(redisProperties.getPassword()));
        serverConfig.setDatabase(redisProperties.getDatabase());

        return new LettuceConnectionFactory(serverConfig, clientConfig);
    }
}
