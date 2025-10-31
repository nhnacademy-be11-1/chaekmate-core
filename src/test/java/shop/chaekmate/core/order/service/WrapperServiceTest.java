package shop.chaekmate.core.order.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import shop.chaekmate.core.order.dto.request.WrapperDto;
import shop.chaekmate.core.order.dto.response.WrapperResponse;
import shop.chaekmate.core.order.entity.Wrapper;
import shop.chaekmate.core.order.exception.DuplicatedWrapperNameException;
import shop.chaekmate.core.order.exception.WrapperNotFoundException;
import shop.chaekmate.core.order.repository.WrapperRepository;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class WrapperServiceTest {

    @Mock
    private WrapperRepository wrapperRepository;

    @InjectMocks
    private WrapperService wrapperService;

    Wrapper wrapper;

    @BeforeEach
    void setUp() {
        wrapper = new Wrapper("테스트 포장지", 1000);
    }

    @Test
    void 포장지_등록_성공() {
        when(wrapperRepository.existsByName(anyString())).thenReturn(false);
        when(wrapperRepository.save(any(Wrapper.class))).thenReturn(wrapper);

        WrapperResponse response = wrapperService.createWrapper(new WrapperDto("테스트 포장지", 1000));

        assertNotNull(response);
        assertAll(
                () -> assertEquals("테스트 포장지", response.name()),
                () -> assertEquals(1000, response.price())
        );

        verify(wrapperRepository, times(1)).save(any(Wrapper.class));
    }

    @Test
    void 포장지_등록_실패_이름_중복() {
        when(wrapperRepository.existsByName(anyString())).thenReturn(true);

        WrapperDto dto = new WrapperDto("테스트 포장지", 1000);
        assertThrows(DuplicatedWrapperNameException.class, () -> wrapperService.createWrapper(dto));

        verify(wrapperRepository, never()).save(any(Wrapper.class));
    }

    @Test
    void 포장지_수정_성공() {
        when(wrapperRepository.findById(anyLong())).thenReturn(Optional.of(wrapper));
        when(wrapperRepository.existsByName(anyString())).thenReturn(false);

        WrapperResponse response = wrapperService.modifyWrapper(1L, new WrapperDto("수정된 포장지", 3000));

        assertNotNull(response);
        assertAll(
                () -> assertEquals("수정된 포장지", response.name()),
                () -> assertEquals(3000, response.price())
        );

        verify(wrapperRepository, times(1)).findById(anyLong());
        verify(wrapperRepository, times(1)).existsByName(anyString());
    }

    @Test
    void 포장지_수정_실패_이름_중복() {
        when(wrapperRepository.findById(anyLong())).thenReturn(Optional.of(wrapper));
        when(wrapperRepository.existsByName(anyString())).thenReturn(true);

        WrapperDto dto = new WrapperDto("수정된 포장지", 2000);
        assertThrows(DuplicatedWrapperNameException.class, () -> wrapperService.modifyWrapper(1L, dto));

        verify(wrapperRepository, times(1)).findById(anyLong());
        verify(wrapperRepository, times(1)).existsByName(anyString());
    }

    @Test
    void 포장지_삭제_성공() {
        when(wrapperRepository.existsById(anyLong())).thenReturn(true);

        wrapperService.deleteWrapper(anyLong());

        verify(wrapperRepository, times(1)).deleteById(anyLong());
    }

    @Test
    void 포장지_삭제_실패_존재x() {
        when(wrapperRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(WrapperNotFoundException.class, () -> wrapperService.deleteWrapper(1L));

        verify(wrapperRepository, never()).deleteById(any());
    }

    @Test
    void 포장지_단일_조회_성공() {
        when(wrapperRepository.findById(anyLong())).thenReturn(Optional.of(wrapper));

        WrapperResponse response = wrapperService.getWrapperById(anyLong());

        assertNotNull(response);
        assertAll(
                () -> assertEquals("테스트 포장지", response.name()),
                () -> assertEquals(1000, response.price())
        );

        verify(wrapperRepository, times(1)).findById(anyLong());
    }

    @Test
    void 포장지_단일_조회_실패_존재x() {
        when(wrapperRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(WrapperNotFoundException.class, () -> wrapperService.getWrapperById(1L));

        verify(wrapperRepository, times(1)).findById(anyLong());
    }

    @Test
    void 포장지_전체_조회_성공() {
        when(wrapperRepository.findAll()).thenReturn(List.of(new Wrapper("포장지1", 1000), new Wrapper("포장지2", 2000)));

        List<WrapperResponse> responses = wrapperService.getWrappers();

        assertThat(responses)
                .hasSize(2)
                .extracting(WrapperResponse::name, WrapperResponse::price)
                .containsExactlyInAnyOrder(
                        tuple("포장지1", 1000),
                        tuple("포장지2", 2000)
                );

        verify(wrapperRepository, times(1)).findAll();
    }
}
