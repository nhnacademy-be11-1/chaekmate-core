package shop.chaekmate.core.book.controller;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import shop.chaekmate.core.book.dto.CreateTagRequest;
import shop.chaekmate.core.book.dto.CreateTagResponse;
import shop.chaekmate.core.book.dto.TagResponse;
import shop.chaekmate.core.book.dto.UpdateTagRequest;
import shop.chaekmate.core.book.dto.UpdateTagResponse;
import shop.chaekmate.core.book.service.TagService;

@RestController
public class TagController {

    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @PostMapping("/admin/tags")
    @ResponseStatus(HttpStatus.CREATED)
    public CreateTagResponse createTag(@Valid @RequestBody CreateTagRequest createTagRequest) {

        return tagService.createTag(createTagRequest);
    }

    @GetMapping("/tags/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TagResponse readTag(@PathVariable(name = "id") Long targetId) {
        return tagService.readTagById(targetId);
    }

    @GetMapping("/tags")
    @ResponseStatus(HttpStatus.OK)
    public List<TagResponse> readAllTags() {
        return tagService.readAllTags();
    }

    @PutMapping("/admin/tags/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UpdateTagResponse updateTag(@PathVariable(name = "id") Long targetId,
                                       @Valid @RequestBody UpdateTagRequest request) {

        return tagService.updateTag(targetId, request);
    }

    @DeleteMapping("/admin/tags/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTag(@PathVariable(name = "id") Long targetId) {
        tagService.deleteTagById(targetId);
    }

}
