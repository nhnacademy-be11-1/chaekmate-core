package shop.chaekmate.core.payment.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shop.chaekmate.core.payment.controller.docs.PaymentHistoryAdminControllerDocs;
import shop.chaekmate.core.payment.dto.PaymentHistoryDto;
import shop.chaekmate.core.payment.entity.type.PaymentMethodType;
import shop.chaekmate.core.payment.entity.type.PaymentStatusType;
import shop.chaekmate.core.payment.service.PaymentHistoryService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/payments/histories")
public class PaymentHistoryAdminController implements PaymentHistoryAdminControllerDocs {

    private final PaymentHistoryService paymentHistoryService;

    @GetMapping
    public ResponseEntity<Page<PaymentHistoryDto>> getFilteredHistories(
            @RequestParam(required = false) PaymentMethodType paymentType,
            @RequestParam(required = false) PaymentStatusType paymentStatus,
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate start,
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end,
            @PageableDefault(size = 20, sort = "occurredAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        LocalDateTime startAt = (start != null) ? start.atStartOfDay() : null;
        LocalDateTime endAt = (end != null) ? end.plusDays(1).atStartOfDay().minusNanos(1) : null;

        Page<PaymentHistoryDto> page = paymentHistoryService.findHistoriesByFilter(
                paymentType,
                paymentStatus,
                startAt,
                endAt,
                pageable
        );

        return ResponseEntity.ok(page);
    }
}
