package shop.chaekmate.core.order.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import shop.chaekmate.core.order.dto.request.WrapperRequest;
import shop.chaekmate.core.order.dto.request.WrapperDto;
import shop.chaekmate.core.order.dto.response.WrapperResponse;
import shop.chaekmate.core.order.service.WrapperService;

@RestController
@RequestMapping("/wrappers")
@RequiredArgsConstructor
public class WrapperController implements WrapperApi {
    private final WrapperService wrapperService;

    // 추가
    @PostMapping("/admin")
    @ResponseStatus(HttpStatus.CREATED)
    public WrapperResponse createWrapper(@Valid @RequestBody WrapperRequest wrapperRequest) {
        WrapperDto dto = new WrapperDto(wrapperRequest.name(), wrapperRequest.price());
        return wrapperService.createWrapper(dto);
    }

    //수정
    @PutMapping("/admin/{id}")
    @ResponseStatus(HttpStatus.OK)
    public WrapperResponse modifyWrapper(@PathVariable(name = "id") Long wrapperId,
                                         @Valid @RequestBody WrapperRequest wrapperRequest) {
        WrapperDto dto = new WrapperDto(wrapperRequest.name(), wrapperRequest.price());
        return wrapperService.modifyWrapper(wrapperId, dto);
    }

    //삭제
    @DeleteMapping("/admin/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteWrapper(@PathVariable(name = "id") Long id) {
        wrapperService.deleteWrapper(id);
    }

    //전체 조회
    @GetMapping
    public List<WrapperResponse> getWrappers() {
        return wrapperService.getWrappers();
    }

    //단일 조회
    @GetMapping("/{id}")
    public WrapperResponse getWrapperById(@PathVariable(name = "id") Long id) {
        return wrapperService.getWrapperById(id);
    }
}
