package shop.chaekmate.core.common.config;

import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    //wrapperAPI
    @Bean
    public GroupedOpenApi wrapperApi() {
        return GroupedOpenApi.builder()
                .group("Wrapper API")
                .addOpenApiCustomizer(openApi -> openApi
                        .info(new Info()
                                .title("포장지 관리 API 문서")
                                .description("포장지 생성, 조회, 수정, 삭제 기능")
                                .version("v1.0")))
                .pathsToMatch("/wrappers/**")
                .build();
    }

    //swagger API 추가
}
