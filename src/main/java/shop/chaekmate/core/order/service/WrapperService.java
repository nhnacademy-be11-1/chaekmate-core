package shop.chaekmate.core.order.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.chaekmate.core.order.dto.request.WrapperDto;
import shop.chaekmate.core.order.dto.response.WrapperResponse;
import shop.chaekmate.core.order.entity.Wrapper;
import shop.chaekmate.core.order.exception.DuplicatedWrapperNameException;
import shop.chaekmate.core.order.exception.WrapperNotFoundException;
import shop.chaekmate.core.order.repository.WrapperRepository;

@Service
@RequiredArgsConstructor
public class WrapperService {

    private final WrapperRepository wrapperRepository;

    //포장지 이름, 가격 추가
    @Transactional
    public WrapperResponse createWrapper(WrapperDto wrapperDto) {
        if (wrapperRepository.existsByName(wrapperDto.name())) {
            throw new DuplicatedWrapperNameException();
        }
        Wrapper wrapper = wrapperRepository.save(new Wrapper(wrapperDto.name(), wrapperDto.price()));

        return new WrapperResponse(wrapper.getId(), wrapper.getName(), wrapper.getPrice());
    }

    //포장지 수정(이름+가격)
    @Transactional
    public WrapperResponse modifyWrapper(Long id, WrapperDto wrapperDto) {
        Wrapper wrapper = wrapperRepository.findById(id).orElseThrow(WrapperNotFoundException::new);

        if (wrapperRepository.existsByNameAndIdNot(wrapperDto.name(), id)) {
            throw new DuplicatedWrapperNameException();
        }

        wrapper.updateWrapper(wrapperDto.name(), wrapperDto.price());

        return new WrapperResponse(wrapper.getId(), wrapper.getName(), wrapper.getPrice());
    }

    //포장지 삭제
    @Transactional
    public void deleteWrapper(Long id) {
        if (!wrapperRepository.existsById(id)) {
            throw new WrapperNotFoundException();
        }

        wrapperRepository.deleteById(id);
    }

    //포장지 단일 조회
    @Transactional(readOnly = true)
    public WrapperResponse getWrapperById(Long id) {
        Wrapper wrapper = wrapperRepository.findById(id).orElseThrow(WrapperNotFoundException::new);
        return new WrapperResponse(wrapper.getId(), wrapper.getName(), wrapper.getPrice());
    }

    //포장지 전체 조회
    @Transactional(readOnly = true)
    public List<WrapperResponse> getWrappers() {
        return wrapperRepository.findAll().stream()
                .map(wrapper -> new WrapperResponse(
                        wrapper.getId(),
                        wrapper.getName(),
                        wrapper.getPrice()))
                .toList();
    }
}
