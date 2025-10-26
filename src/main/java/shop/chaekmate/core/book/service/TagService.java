package shop.chaekmate.core.book.service;

import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.stereotype.Service;
import shop.chaekmate.core.book.dto.CreateTagRequest;
import shop.chaekmate.core.book.dto.CreateTagResponse;
import shop.chaekmate.core.book.dto.TagResponse;
import shop.chaekmate.core.book.dto.UpdateTagRequest;
import shop.chaekmate.core.book.dto.UpdateTagResponse;
import shop.chaekmate.core.book.entity.Tag;
import shop.chaekmate.core.book.repository.TagRepository;

@Service
public class TagService {

    private final TagRepository tagRepository;

    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Transactional
    public CreateTagResponse createTag(CreateTagRequest request) {

        // Find by 는 (soft delete가 적용 되지만, exists 는 count 쿼리라서 적용안됨)
        if (tagRepository.findByName(request.name()).isPresent()) {
            throw new RuntimeException(" 이미 존재하는 Tag 입니다.");
        }

        Tag tag = new Tag(request.name());
        tagRepository.save(tag);

        return new CreateTagResponse(tag.getId(), tag.getName());
    }

    @Transactional
    public TagResponse readTagById(Long targetId) {

        // soft delete 적용 됨
        Tag targetTag = tagRepository.findById(targetId)
                .orElseThrow(() -> new RuntimeException("해당하는 Id 의 Tag 를 찾을 수 없습니다."));
        return new TagResponse(targetTag.getId(), targetTag.getName());
    }


    public List<TagResponse> readAllTags() {

        return tagRepository.findAll().stream().map(tag -> new TagResponse(tag.getId(), tag.getName()))
                .toList();
    }

    @Transactional
    public UpdateTagResponse updateTag(Long targetId, UpdateTagRequest request) {

        Tag targetTag = tagRepository.findById(targetId)
                .orElseThrow(() -> new RuntimeException("해당하는 Id 의 Tag 를 찾을 수 없습니다."));
        targetTag.setName(request.name());
        tagRepository.save(targetTag);

        return new UpdateTagResponse(targetTag.getId(), targetTag.getName());
    }

    @Transactional
    public void deleteTagById(Long targetId) {

        Tag targetTag = tagRepository.findById(targetId)
                .orElseThrow(() -> new RuntimeException("해당하는 Id의 Tag를 찾을 수 없습니다."));
        tagRepository.delete(targetTag);

    }

}
