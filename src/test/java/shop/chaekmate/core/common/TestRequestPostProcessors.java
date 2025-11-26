package shop.chaekmate.core.common;

import org.springframework.test.web.servlet.request.RequestPostProcessor;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

public final class TestRequestPostProcessors {

    private TestRequestPostProcessors() {}

    public static RequestPostProcessor asAdmin() {
        return request -> {
            request.addHeader("X-Member-Id", "1");
            request.addHeader("X-User-Role", "ADMIN");
            return user("admin").roles("ADMIN").postProcessRequest(request);
        };
    }

    public static RequestPostProcessor asUser() {
        return request -> {
            request.addHeader("X-Member-Id", "2");
            request.addHeader("X-User-Role", "USER");
            return user("member").roles("USER").postProcessRequest(request);
        };
    }
}
