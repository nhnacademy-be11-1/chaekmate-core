package shop.chaekmate.core.payment.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import shop.chaekmate.core.payment.dto.request.DoorayMessageRequest;

@FeignClient(name = "dooray-client", url = "${dooray.webhook.url}")
public interface DoorayClient {

    @PostMapping
    String sendMessage(@RequestBody DoorayMessageRequest request);
}
