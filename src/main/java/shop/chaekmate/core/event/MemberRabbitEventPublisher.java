package shop.chaekmate.core.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import shop.chaekmate.core.common.config.MemberQueueProperties;

@Slf4j
@Component
@RequiredArgsConstructor
public class MemberRabbitEventPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final MemberQueueProperties memberQueueProperties;

    public void publishMemberSignedUp(Long memberId) {
        String exchange = memberQueueProperties.exchange();

        MemberSignedUpEvent event = new MemberSignedUpEvent(memberId);

        try {
            rabbitTemplate.convertAndSend(
                    exchange,
                    "",
                    event
            );

            log.info("회원가입 이벤트 발행 (exchange={}): memberId={}",
                    exchange, memberId);
        } catch (Exception e) {
            log.error("회원가입 이벤트 발행 실패: memberId={}", memberId, e);
        }
    }

}
