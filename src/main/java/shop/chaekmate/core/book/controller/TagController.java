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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import shop.chaekmate.core.book.controller.docs.TagControllerDocs;
import shop.chaekmate.core.book.dto.request.CreateTagRequest;
import shop.chaekmate.core.book.dto.response.CreateTagResponse;
import shop.chaekmate.core.book.dto.response.TagResponse;
import shop.chaekmate.core.book.dto.request.UpdateTagRequest;
import shop.chaekmate.core.book.dto.response.UpdateTagResponse;
import shop.chaekmate.core.book.service.TagService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class TagController implements TagControllerDocs {

    private final TagService tagService;

    @PostMapping("/admin/tags")
    public ResponseEntity<CreateTagResponse> createTag(@Valid @RequestBody CreateTagRequest createTagRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tagService.createTag(createTagRequest));
    }

    @GetMapping("/tags/{id}")
    public ResponseEntity<TagResponse> readTag(@PathVariable(name = "id") Long targetId) {
        return ResponseEntity.ok(tagService.readTagById(targetId));
    }

    @GetMapping("/tags")
    public ResponseEntity<List<TagResponse>> readAllTags() {
        return ResponseEntity.ok(tagService.readAllTags());
    }

    @PutMapping("/admin/tags/{id}")
    public ResponseEntity<UpdateTagResponse> updateTag(@PathVariable(name = "id") Long targetId,
                                                       @Valid @RequestBody UpdateTagRequest request) {
        return ResponseEntity.ok(tagService.updateTag(targetId, request));
    }

    @DeleteMapping("/admin/tags/{id}")
    public ResponseEntity<Void> deleteTag(@PathVariable(name = "id") Long targetId) {
        tagService.deleteTagById(targetId);
        return ResponseEntity.noContent().build();
    }
}
