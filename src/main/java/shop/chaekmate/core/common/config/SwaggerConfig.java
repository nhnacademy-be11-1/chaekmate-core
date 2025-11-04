package shop.chaekmate.core.common.config;

import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
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

    @Bean
    public GroupedOpenApi memberApi() {
        return GroupedOpenApi.builder()
                .group("Member API")
                .addOpenApiCustomizer(openApi -> openApi
                        .info(new Info()
                                .title("회원 관련 API")
                                .description("회원 가입, 조회, 수정, 탈퇴 기능")
                                .version("v1.0")))
                .pathsToMatch(
                        "/members/**"
                )
                .build();
    }

}
