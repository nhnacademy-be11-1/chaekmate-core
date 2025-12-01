package shop.chaekmate.core.event;

import java.io.Serializable;

public record MemberSignedUpEvent(
        Long memberId
) implements Serializable {
}
