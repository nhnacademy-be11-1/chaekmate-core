package shop.chaekmate.core.common.resolver;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class CategoryTagPageableResolver implements HandlerMethodArgumentResolver {

    private final PageableHandlerMethodArgumentResolver defaultResolver = new PageableHandlerMethodArgumentResolver();
    private static final int MIN_PAGE = 0;
    private static final int MIN_SIZE = 0;
    private static final int MAX_SIZE = 30;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return Pageable.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(@NonNull MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  @NonNull NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) {

        HttpServletRequest request = ((ServletRequestAttributes)
                RequestContextHolder.currentRequestAttributes()).getRequest();

        String uri = request.getRequestURI();

        if (uri.startsWith("/categories") || uri.startsWith("/tags")) {
            return resolveCustomPageable(request);
        }

        return defaultResolver.resolveArgument(parameter, mavContainer, webRequest, binderFactory);
    }

    private Pageable resolveCustomPageable(HttpServletRequest request) {

        String pageStr = request.getParameter("page");
        String sizeStr = request.getParameter("size");

        int page = (pageStr != null) ? Integer.parseInt(pageStr) : 0;
        int size = (sizeStr != null) ? Integer.parseInt(sizeStr) : 10;

        page = Math.max(page, MIN_PAGE);
        size = Math.max(size, MIN_SIZE);
        size = Math.min(size, MAX_SIZE);

        return PageRequest.of(page, size);
    }
}
