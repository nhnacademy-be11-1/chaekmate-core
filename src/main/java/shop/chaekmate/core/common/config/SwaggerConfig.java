package shop.chaekmate.core.common.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @io.swagger.v3.oas.annotations.info.Info(title = "chaekmate core API", version = "v1", description = "chaekmate core 서버 API 문서")
)
@SecurityScheme(
        name = "bearerAuth",               // 사용할 인증 이름
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
public class SwaggerConfig {

    //wrapper
    @Bean
    public GroupedOpenApi wrapperApi() {
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

    //delivery-policy
    @Bean
    public GroupedOpenApi deliveryPolicyApi() {
        return GroupedOpenApi.builder()
                .group("Delivery-Policy API")
                .addOpenApiCustomizer(openApi -> openApi
                        .info(new Info()
                                .title("배달 정책 관련 API")
                                .description("배달 정책 등록, 조회, 삭제(등록시 자동 삭제)")
                                .version("v1.0")))
                .pathsToMatch("/admin/delivery-policy/**", "/delivery-policy/**")
                .build();
    }

    @Bean
    public GroupedOpenApi tagApi() {
        return GroupedOpenApi.builder()
                .group("Tag API")
                .addOpenApiCustomizer(openApi -> openApi
                        .info(new Info()
                                .title("태그 관련 API")
                                .description("태그 추가, 조회, 수정, 삭제 기능")
                                .version("v1.0")))
                .pathsToMatch("/admin/tags/**", "/tags/**")
                .build();
    }

    @Bean
    public GroupedOpenApi categoryApi() {
        return GroupedOpenApi.builder()
                .group("Category API")
                .addOpenApiCustomizer(openApi -> openApi
                        .info(new Info()
                                .title("카테고리 관련 API")
                                .description("카테고리 추가, 조회, 수정, 삭제 기능")
                                .version("v1.0")))
                .pathsToMatch("/admin/categories/**", "/categories/**")
                .build();
    }

    @Bean
    public GroupedOpenApi likeApi() {
        return GroupedOpenApi.builder()
                .group("Like API")
                .addOpenApiCustomizer(openApi -> openApi
                        .info(new Info()
                                .title("좋아요 관련 API")
                                .description("좋아요 생성, 삭제, 조회 기능")
                                .version("v1.0")))
                .pathsToMatch("/books/**/likes", "/likes/**", "/members/**/likes")
                .build();
    }
}
