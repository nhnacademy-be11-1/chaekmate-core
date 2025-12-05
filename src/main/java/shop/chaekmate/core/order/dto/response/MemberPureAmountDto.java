package shop.chaekmate.core.order.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 회원별 순수 주문금액 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberPureAmountDto {

    private Long memberId;
    private Long pureAmount;

    public MemberPureAmountDto(Long memberId, Number pureAmount) {
        this.memberId = memberId;
        this.pureAmount = pureAmount != null ? pureAmount.longValue() : 0L;
    }
}
