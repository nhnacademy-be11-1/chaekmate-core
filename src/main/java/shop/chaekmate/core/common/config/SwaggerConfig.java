package shop.chaekmate.core.common.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @io.swagger.v3.oas.annotations.info.Info(title = "chaekmate core API", version = "v1", description = "chaekmate core 서버 API 문서")
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

    @Bean
    public GroupedOpenApi bookApi() {
        return GroupedOpenApi.builder()
                .group("Book API")
                .addOpenApiCustomizer(openApi -> openApi
                        .info(new Info()
                                .title("도서 관리 API")
                                .description("도서 생성, 수정, 삭제, 조회 기능")
                                .version("v1.0")))
                .pathsToMatch("/books/**", "/admin/books/**")
                .build();
    }

    @Bean
    public GroupedOpenApi pointPolicyApi() {
        return GroupedOpenApi.builder()
                .group("Point Policy API")
                .addOpenApiCustomizer(openApi -> openApi
                        .info(new Info()
                                .title("포인트 정책 API")
                                .description("포인트 정책 조회 및 관리 API")
                                .version("v1.0")))
                .pathsToMatch("/admin/point-policies/**", "/point-policies/**")
                .build();
    }

    @Bean
    public GroupedOpenApi pointHistoryApi() {
        return GroupedOpenApi.builder()
                .group("Point History API")
                .addOpenApiCustomizer(openApi -> openApi
                        .info(new Info()
                                .title("포인트 History API")
                                .description("포인트 History 조회 API")
                                .version("v1.0")))
                .pathsToMatch("/admin/point-history/**", "/point-history/**")
                .build();
    }

    // payment, paymentHistory
    @Bean
    public GroupedOpenApi paymentApi() {
        return GroupedOpenApi.builder()
                .group("Payment API")
                .addOpenApiCustomizer(openApi -> openApi
                        .info(new Info()
                                .title("결제 관련 API")
                                .description("결제 승인, 취소 맟 (관리자)결제내역 조회 기능")
                                .version("v1.0")))
                .pathsToMatch("/payments/**", "/admin/payments/histories/**")
                .build();
    }

    @Bean
    public GroupedOpenApi orderApi() {
        return GroupedOpenApi.builder()
                .group("Order API")
                .addOpenApiCustomizer(openApi -> openApi
                        .info(new Info()
                                .title("주문 관련 API")
                                .description("주문 관련 기능")
                                .version("v1.0")))
                .pathsToMatch("/orders/**", "/admin/orders/**")
                .build();
    }
}
