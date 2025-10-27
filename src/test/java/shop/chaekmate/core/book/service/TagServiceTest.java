package shop.chaekmate.core.book.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import shop.chaekmate.core.book.dto.request.CreateTagRequest;
import shop.chaekmate.core.book.dto.response.TagResponse;
import shop.chaekmate.core.book.dto.request.UpdateTagRequest;
import shop.chaekmate.core.book.entity.Tag;
import shop.chaekmate.core.book.repository.TagRepository;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class TagServiceTest {

    @Mock
    private TagRepository tagRepository;

    @InjectMocks
    private TagService tagService;

    @Test
    void 태그_생성_성공() {
        // given
        CreateTagRequest request = new CreateTagRequest("New Tag");
        Tag tag = new Tag("New Tag");

        when(tagRepository.findByName("New Tag")).thenReturn(Optional.empty());
        when(tagRepository.save(any(Tag.class))).thenReturn(tag);

        // when
        var response = tagService.createTag(request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.name()).isEqualTo("New Tag");
    }

    @Test
    void 중복된_이름으로_태그_생성_실패() {
        // given
        CreateTagRequest request = new CreateTagRequest("Existing Tag");

        when(tagRepository.findByName("Existing Tag")).thenReturn(Optional.of(new Tag("Existing Tag")));

        // when & then
        assertThrows(RuntimeException.class, () -> tagService.createTag(request));
    }

    @Test
    void ID로_태그_조회_성공() {
        // given
        Long tagId = 1L;
        Tag tag = new Tag("Test Tag");

        when(tagRepository.findById(tagId)).thenReturn(Optional.of(tag));

        // when
        TagResponse response = tagService.readTagById(tagId);

        // then
        assertThat(response).isNotNull();
        assertThat(response.name()).isEqualTo("Test Tag");
    }

    @Test
    void 존재하지_않는_ID로_태그_조회_실패() {
        // given
        Long tagId = 1L;

        when(tagRepository.findById(tagId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(RuntimeException.class, () -> tagService.readTagById(tagId));
    }

    @Test
    void 모든_태그_조회_성공() {
        // given
        Tag tag = new Tag("Test Tag");
        when(tagRepository.findAll()).thenReturn(Collections.singletonList(tag));

        // when
        List<TagResponse> responses = tagService.readAllTags();

        // then
        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).name()).isEqualTo("Test Tag");
    }

    @Test
    void 태그_수정_성공() {
        // given
        Long tagId = 1L;
        UpdateTagRequest request = new UpdateTagRequest("Updated Tag");
        Tag tag = new Tag("Old Tag");

        when(tagRepository.findById(tagId)).thenReturn(Optional.of(tag));
        when(tagRepository.save(any(Tag.class))).thenReturn(tag);

        // when
        var response = tagService.updateTag(tagId, request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.name()).isEqualTo("Updated Tag");
    }

    @Test
    void ID로_태그_삭제_성공() {
        // given
        Long tagId = 1L;
        Tag tag = new Tag("Test Tag");

        when(tagRepository.findById(tagId)).thenReturn(Optional.of(tag));
        doNothing().when(tagRepository).delete(tag);

        // when
        tagService.deleteTagById(tagId);

        // then
        verify(tagRepository, times(1)).delete(tag);
    }
}
