package shop.chaekmate.core.order.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import shop.chaekmate.core.order.controller.docs.WrapperControllerDocs;
import shop.chaekmate.core.order.dto.request.WrapperRequest;
import shop.chaekmate.core.order.dto.request.WrapperDto;
import shop.chaekmate.core.order.dto.response.WrapperResponse;
import shop.chaekmate.core.order.service.WrapperService;

@RestController
@RequiredArgsConstructor
public class WrapperController implements WrapperControllerDocs {
    private final WrapperService wrapperService;

    //admin
    // 추가
    @PostMapping("/admin/wrappers")
    public ResponseEntity<WrapperResponse> createWrapper(@Valid @RequestBody WrapperRequest wrapperRequest) {
        WrapperDto dto = new WrapperDto(wrapperRequest.name(), wrapperRequest.price());
        WrapperResponse response = wrapperService.createWrapper(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    //수정
    @PutMapping("/admin/wrappers/{id}")
    public ResponseEntity<WrapperResponse> modifyWrapper(@PathVariable(name = "id") Long wrapperId,
                                         @Valid @RequestBody WrapperRequest wrapperRequest) {
        WrapperDto dto = new WrapperDto(wrapperRequest.name(), wrapperRequest.price());
        WrapperResponse response = wrapperService.modifyWrapper(wrapperId, dto);
        return ResponseEntity.ok(response);
    }

    //삭제
    @DeleteMapping("/admin/wrappers/{id}")
    public ResponseEntity<Void> deleteWrapper(@PathVariable(name = "id") Long id) {
        wrapperService.deleteWrapper(id);
        return ResponseEntity.noContent().build();
    }

    //user
    //전체 조회
    @GetMapping("/wrappers")
    public ResponseEntity<List<WrapperResponse>> getWrappers() {
        List<WrapperResponse> wrappers = wrapperService.getWrappers();
        return ResponseEntity.ok(wrappers);
    }

    //단일 조회
    @GetMapping("/wrappers/{id}")
    public ResponseEntity<WrapperResponse> getWrapperById(@PathVariable(name = "id") Long id) {
        WrapperResponse wrapper = wrapperService.getWrapperById(id);
        return ResponseEntity.ok(wrapper);
    }
}
