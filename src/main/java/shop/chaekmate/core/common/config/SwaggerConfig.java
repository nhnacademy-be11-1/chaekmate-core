package shop.chaekmate.core.common.config;

import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public GroupedOpenApi WrapperApi() {
        return GroupedOpenApi.builder()
                .group("Wrapper API")
                .addOpenApiCustomizer(openApi -> openApi
                        .info(new Info()
                                .title("포장지 관련 API")
                                .description("포장지 추가, 수정, 삭제, 조회 기능")
                                .version("v1.0")))
                .pathsToMatch("/admin/wrappers/**", "/wrappers/**")
                .build();
    }

    @Bean
    public GroupedOpenApi DeliveryPolicyApi() {
        return GroupedOpenApi.builder()
                .group("Delivery-Policy API")
                .addOpenApiCustomizer(openApi -> openApi
                        .info(new Info()
                                .title("배달 정책 관련 API")
                                .description("배달 정책 등록, 조회, 삭제(등록시 자동 삭제)")
                                .version("v1.0")))
                .pathsToMatch("/admin/delivery-policy/**","/delivery-policy/**")
                .build();
    }

    //swagger API 추가
}
