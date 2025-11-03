package shop.chaekmate.core.book.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import shop.chaekmate.core.book.dto.request.CreateTagRequest;
import shop.chaekmate.core.book.dto.request.UpdateTagRequest;
import shop.chaekmate.core.book.dto.response.CreateTagResponse;
import shop.chaekmate.core.book.dto.response.TagResponse;
import shop.chaekmate.core.book.dto.response.UpdateTagResponse;
import shop.chaekmate.core.book.entity.Tag;
import shop.chaekmate.core.book.exception.DuplicateTagNameException;
import shop.chaekmate.core.book.exception.TagNotFoundException;
import shop.chaekmate.core.book.repository.TagRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;

    @Transactional
    public CreateTagResponse createTag(CreateTagRequest request) {

        // Find by 는 (soft delete가 적용 되지만, exists 는 count 쿼리라서 적용안됨)
        // Tag name Unique 취급
        if (tagRepository.findByName(request.name()).isPresent()) {
            throw new DuplicateTagNameException();
        }

        Tag tag = new Tag(request.name());
        tagRepository.save(tag);

        return new CreateTagResponse(tag.getId(), tag.getName());
    }

    @Transactional
    public TagResponse readTagById(Long targetId) {

        // soft delete 적용 됨
        Tag targetTag = tagRepository.findById(targetId)
                .orElseThrow(() -> new TagNotFoundException("Target tag not found"));
        return new TagResponse(targetTag.getId(), targetTag.getName());
    }


    public List<TagResponse> readAllTags() {

        return tagRepository.findAll().stream().map(tag -> new TagResponse(tag.getId(), tag.getName()))
                .toList();
    }

    @Transactional
    public UpdateTagResponse updateTag(Long targetId, UpdateTagRequest request) {

        Tag targetTag = tagRepository.findById(targetId)
                .orElseThrow(() -> new TagNotFoundException("Target tag not found"));
        targetTag.updateName(request.name());
        tagRepository.save(targetTag);

        return new UpdateTagResponse(targetTag.getId(), targetTag.getName());
    }

    @Transactional
    public void deleteTagById(Long targetId) {

        Tag targetTag = tagRepository.findById(targetId)
                .orElseThrow(() -> new TagNotFoundException("Target tag not found"));
        tagRepository.delete(targetTag);

    }

}
