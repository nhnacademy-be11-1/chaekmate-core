package shop.chaekmate.core.common.advice;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import shop.chaekmate.core.common.dto.ErrorResponse;
import shop.chaekmate.core.common.dto.SuccessResponse;

@RestControllerAdvice(basePackages = "shop.chaekmate.core")
@RequiredArgsConstructor
public class SuccessResponseAdvice implements ResponseBodyAdvice<Object> {

    private final ObjectMapper objectMapper;

    @Override
    public boolean supports(@NonNull MethodParameter returnType,
                            @NonNull Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @SneakyThrows
    @Override
    public Object beforeBodyWrite(Object body, @NonNull MethodParameter returnType,
                                  @NonNull MediaType selectedContentType,
                                  @NonNull Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  @NonNull ServerHttpRequest request, @NonNull ServerHttpResponse response) {
        // ErrorResponse, 이미 SuccessResponse 로 래핑된 경우 그대로 반환
        if (body instanceof ErrorResponse || body instanceof SuccessResponse) {
            return body;
        }

        // String 타입의 경우 ObjectMapper를 통해 변환하여 ClassCastException 방지
        if (body instanceof String) {
            return objectMapper.writeValueAsString(SuccessResponse.of(body));
        }

        return SuccessResponse.of(body);
    }
}
