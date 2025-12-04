package shop.chaekmate.core.payment.client;

import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(
        name = "chaekmate-coupon",
        url = "${chaekmate.coupon.url}"
)
public interface CouponClient {

    @PostMapping("/issued-coupons/use/bulk")
    ResponseEntity<Void> useCouponsBulk(
            @RequestHeader("X-Member-Id") Long memberId,
            @RequestBody List<Long> couponIds
    );
}
