package shop.chaekmate.core.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Chaekmate Core API",
                version = "v1.0",
                description = "Chaekmate 서비스의 핵심 도메인 API 문서입니다."
        )
)
public class SwaggerConfig {

    @Bean
    public GroupedOpenApi bookApi() {
        return GroupedOpenApi.builder()
                .group("Book API") // Swagger UI에서 표시될 그룹 이름
                .pathsToMatch("/books/**") // 이 경로의 API만 포함
                .build();
    }

    @Bean
    public GroupedOpenApi memberApi() {
        return GroupedOpenApi.builder()
                .group("Member API")
                .pathsToMatch("/members/**")
                .build();
    }

    @Bean
    public GroupedOpenApi adminApi() {
        return GroupedOpenApi.builder()
                .group("Admin API")
                .pathsToMatch("/admin/**")
                .build();
    }

    @Bean
    public GroupedOpenApi cartApi() {
        return GroupedOpenApi.builder()
                .group("Cart API")
                .pathsToMatch("/carts/**")
                .build();
    }

    @Bean
    public GroupedOpenApi couponApi() {
        return GroupedOpenApi.builder()
                .group("Coupon API")
                .pathsToMatch("/coupons/**")
                .build();
    }

    @Bean
    public GroupedOpenApi orderApi() {
        return GroupedOpenApi.builder()
                .group("Order API")
                .pathsToMatch("/orders/**")
                .build();
    }
}
