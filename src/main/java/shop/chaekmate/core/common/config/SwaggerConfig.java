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
    @Bean
    public GroupedOpenApi adminTagApi() {
        return GroupedOpenApi.builder()
                .group("Admin Tag API")
                .addOpenApiCustomizer(openApi -> openApi
                        .info(new Info()
                                .title("관리자 태그 관리 API")
                                .description("태그 추가, 수정, 삭제 기능 (관리자용)")
                                .version("v1.0")))
                .pathsToMatch("/admin/tags/**")
                .build();
    }

    @Bean
    public GroupedOpenApi userTagApi() {
        return GroupedOpenApi.builder()
                .group("User Tag API")
                .addOpenApiCustomizer(openApi -> openApi
                        .info(new Info()
                                .title("사용자 태그 조회 API")
                                .description("태그 조회 기능 (사용자용)")
                                .version("v1.0")))
                .pathsToMatch("/tags/**")
                .build();
    }

    @Bean
    public GroupedOpenApi adminCategoryApi() {
        return GroupedOpenApi.builder()
                .group("Admin Category API")
                .addOpenApiCustomizer(openApi -> openApi
                        .info(new Info()
                                .title("관리자 카테고리 관리 API")
                                .description("카테고리 추가, 수정, 삭제 기능 (관리자용)")
                                .version("v1.0")))
                .pathsToMatch("/admin/categories/**")
                .build();
    }

    @Bean
    public GroupedOpenApi userCategoryApi() {
        return GroupedOpenApi.builder()
                .group("User Category API")
                .addOpenApiCustomizer(openApi -> openApi
                        .info(new Info()
                                .title("사용자 카테고리 조회 API")
                                .description("카테고리 조회 기능 (사용자용)")
                                .version("v1.0")))
                .pathsToMatch("/categories/**")
                .build();
    }

    @Bean
    public GroupedOpenApi likeApi() {
        return GroupedOpenApi.builder()
                .group("Like API")
                .addOpenApiCustomizer(openApi -> openApi
                        .info(new Info()
                                .title("좋아요 관리 API")
                                .description("좋아요 생성, 삭제, 조회 기능")
                                .version("v1.0")))
                .pathsToMatch("/books/**/likes", "/likes/**", "/members/**/likes")
                .build();
    }
}
