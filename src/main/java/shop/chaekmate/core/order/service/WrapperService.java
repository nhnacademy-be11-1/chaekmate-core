package shop.chaekmate.core.order.service;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.chaekmate.core.order.dto.request.WrapperRequest;
import shop.chaekmate.core.order.dto.response.WrapperResponse;
import shop.chaekmate.core.order.entity.Wrapper;
import shop.chaekmate.core.order.exception.InvalidWrapperPriceException;
import shop.chaekmate.core.order.exception.WrapperAlreadyExistsException;
import shop.chaekmate.core.order.exception.WrapperNotFoundException;
import shop.chaekmate.core.order.repository.WrapperRepository;

@Service
@RequiredArgsConstructor
public class WrapperService {
    private final WrapperRepository wrapperRepository;

    //포장지 이름, 가격 추가
    @Transactional
    public WrapperResponse createWrapper(WrapperRequest wrapperRequest) {
        if (wrapperRepository.existByNameAndDeletedAtNull(wrapperRequest.name())) {
            throw new WrapperAlreadyExistsException(wrapperRequest.name());
        }
        Wrapper wrapper = new Wrapper(wrapperRequest.name(), wrapperRequest.price());
        wrapperRepository.save(wrapper);

        return new WrapperResponse(wrapper.getId(), wrapper.getName(), wrapper.getPrice());
    }

    //포장지 수정(이름+가격)
    @Transactional
    public WrapperResponse modifyWrapper(Long id, WrapperRequest wrapperRequest) {
        Wrapper wrapper = wrapperRepository.findById(id).orElseThrow(() -> new WrapperNotFoundException(id));

        if (wrapperRepository.existByNameAndDeletedAtNull(wrapperRequest.name())) {
            throw new WrapperAlreadyExistsException(wrapperRequest.name());
        }

        if (wrapperRequest.price()<0) {
            throw new InvalidWrapperPriceException();
        }

        wrapper.updateWrapper(wrapperRequest.name(), wrapperRequest.price());
        wrapperRepository.save(wrapper);

        return new WrapperResponse(wrapper.getId(), wrapper.getName(), wrapper.getPrice());
    }

    //포장지 삭제
    @Transactional
    public void deleteWrapper(Long id) {
        Wrapper wrapper = wrapperRepository.findById(id).orElseThrow(() -> new WrapperNotFoundException(id));
        wrapperRepository.deleteById(wrapper.getId());
    }

    //포장지 단일 조회
    public WrapperResponse getWrapperById(Long id) {
        Wrapper wrapper = wrapperRepository.findById(id).orElseThrow(() -> new WrapperNotFoundException(id));
        return new WrapperResponse(wrapper.getId(), wrapper.getName(), wrapper.getPrice());
    }


    //포장지 전체 조회
    public List<WrapperResponse> getWrappers() {
         return wrapperRepository.findAll().stream()
                 .map(wrapper -> new WrapperResponse(
                         wrapper.getId(),
                         wrapper.getName(),
                         wrapper.getPrice()))
                .collect(Collectors.toList());
    }
}
