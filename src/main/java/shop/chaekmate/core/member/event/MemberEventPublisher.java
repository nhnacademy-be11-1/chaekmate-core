package shop.chaekmate.core.member.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import shop.chaekmate.core.member.dto.response.MemberResponse;

@Slf4j
@Component
@RequiredArgsConstructor
public class MemberEventPublisher {
    private final ApplicationEventPublisher publisher;

    public void publishMemberCreated(MemberResponse response) {
        log.info("[회원 이벤트] 회원가입 이벤트 발행 - 회원ID: {}, 로그인ID: {}", response.id(), response.loginId());
        publisher.publishEvent(new MemberCreatedEvent(response));
        log.info("[회원 이벤트] 회원가입 이벤트 발행 완료 - 회원ID: {}", response.id());
    }
}
