package shop.chaekmate.core.order.controller;


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
import shop.chaekmate.core.order.dto.response.WrapperResponse;
import shop.chaekmate.core.order.service.WrapperService;

@RestController
// #todo 경로 수정
@RequestMapping("/api/wrappers")
@RequiredArgsConstructor
public class WrapperController {
    private final WrapperService wrapperService;

    // 추가
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WrapperResponse createWrapper(@RequestBody WrapperRequest wrapperRequest) {
        return wrapperService.createWrapper(wrapperRequest);
    }

    //수정
    // #todo admin 경로 수정
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public WrapperResponse modifyWrapper(@PathVariable(name = "id") Long wrapperId, @RequestBody WrapperRequest wrapperRequest) {
        return wrapperService.modifyWrapper(wrapperId, wrapperRequest);
    }

    //삭제
    // #todo admin 경로 수정
    @DeleteMapping("/{id}")
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
