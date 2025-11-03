package shop.chaekmate.core.common.config;

import java.util.List;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class PageableConfig implements WebMvcConfigurer {

    // Pageable 기본 설정
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        PageableHandlerMethodArgumentResolver resolver = new PageableHandlerMethodArgumentResolver();
        resolver.setMaxPageSize(30);    // 최대 30개 제한
        resolver.setFallbackPageable(PageRequest.of(0, 10)); // 기본값: page=0, size=10
        resolvers.add(resolver);
    }
}
