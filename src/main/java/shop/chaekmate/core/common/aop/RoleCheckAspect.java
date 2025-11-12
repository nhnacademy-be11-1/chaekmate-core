package shop.chaekmate.core.common.aop;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import shop.chaekmate.core.common.exception.CommonErrorCode;
import shop.chaekmate.core.common.exception.CoreException;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class RoleCheckAspect {

    private static final String X_MEMBER_ID = "X-Member-Id";
    private static final String X_USER_ROLE = "X-User-Role";

    @Around("@annotation(shop.chaekmate.core.common.annotation.RequiredMember)")
    public Object checkMember(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = getRequest();
        if (request == null) {
            throw new CoreException(CommonErrorCode.TOKEN_INVALID);
        }

        String memberId = request.getHeader(X_MEMBER_ID);
        String role = request.getHeader(X_USER_ROLE);
        if (memberId == null || role == null) {
            throw new CoreException(CommonErrorCode.TOKEN_INVALID);
        }

        // USER 또는 ADMIN이면 통과
        if ("USER".equals(role) || "ADMIN".equals(role)) {
            log.debug("회원 권한 체크 통과: memberId={}, role={}", memberId, role);
            return joinPoint.proceed();
        }

        throw new CoreException(CommonErrorCode.TOKEN_INVALID);
    }

    @Around("@annotation(shop.chaekmate.core.common.annotation.RequiredAdmin)")
    public Object checkAdmin(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = getRequest();
        if (request == null) {
            throw new CoreException(CommonErrorCode.TOKEN_INVALID);
        }

        String memberId = request.getHeader(X_MEMBER_ID);
        String role = request.getHeader(X_USER_ROLE);
        if (memberId == null || role == null) {
            throw new CoreException(CommonErrorCode.TOKEN_INVALID);
        }

        // ADMIN 권한 체크
        if ("ADMIN".equals(role)) {
            log.debug("관리자 권한 체크 통과: memberId={}", memberId);
            return joinPoint.proceed();
        }

        throw new CoreException(CommonErrorCode.TOKEN_INVALID);
    }

    private HttpServletRequest getRequest() {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return null;
        }

        return attributes.getRequest();
    }
}

