package shop.chaekmate.core.common.config;

import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    //admin wrapper
    @Bean
    public GroupedOpenApi adminWrapperApi() {
        return GroupedOpenApi.builder()
                .group("Admin Wrapper API")
                .addOpenApiCustomizer(openApi -> openApi
                        .info(new Info()
                                .title("관리자 포장지 관리 API")
                                .description("포장지 추가, 수정, 삭제 기능 (관리자용)")
                                .version("v1.0")))
                .pathsToMatch("/admin/wrappers/**")
                .build();
    }
    //user wrapper
    @Bean
    public GroupedOpenApi wrapperApi() {
        return GroupedOpenApi.builder()
                .group("User Wrapper API")
                .addOpenApiCustomizer(openApi -> openApi
                        .info(new Info()
                                .title("사용자 포장지 조회 API")
                                .description("포장지 조회 기능 (사용자용)")
                                .version("v1.0")))
                .pathsToMatch("/wrappers/**")
                .build();
    }

    //swagger API 추가
}
