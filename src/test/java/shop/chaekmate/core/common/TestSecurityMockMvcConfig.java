package shop.chaekmate.core.common;

import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcBuilderCustomizer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;


@TestConfiguration
@ActiveProfiles("test")
public class TestSecurityMockMvcConfig {

    @Bean
    public MockMvcBuilderCustomizer defaultRequestCustomizer() {
        return builder -> builder.defaultRequest(
                MockMvcRequestBuilders.get("/")
                        .with(csrf())
        );
    }
}
