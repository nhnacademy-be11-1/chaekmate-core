package shop.chaekmate.core.book.controller;


import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import shop.chaekmate.core.book.controller.docs.LikeControllerDocs;


import shop.chaekmate.core.book.dto.response.LikeResponse;
import shop.chaekmate.core.book.service.LikeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class LikeController implements LikeControllerDocs {

    private final LikeService likeService;

    @PostMapping("/books/{bookId}/likes")
    public ResponseEntity<LikeResponse> createLike(@PathVariable Long bookId,
                                                   @RequestHeader("X-USER-ID") Long memberId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(likeService.createLike(bookId, memberId));
    }

    @GetMapping("/likes/{likeId}")
    public ResponseEntity<LikeResponse> readLike(@PathVariable(name = "likeId") Long likeId) {
        return ResponseEntity.ok(likeService.readLikeById(likeId));
    }

    @GetMapping("/books/{bookId}/likes")
    public ResponseEntity<List<LikeResponse>> getBookLikes(@PathVariable Long bookId) {
        return ResponseEntity.ok(likeService.getBookLikes(bookId));
    }

    @GetMapping("/members/likes")
    public ResponseEntity<List<LikeResponse>> getMemberLikes(@RequestHeader("X-USER-ID") Long memberId) {
        return ResponseEntity.ok(likeService.getMemberLikes(memberId));
    }

    @DeleteMapping("/likes/{likeId}")
    public ResponseEntity<Void> deleteLikeById(@PathVariable Long likeId) {
        likeService.deleteLikeById(likeId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/books/{bookId}/likes")
    public ResponseEntity<Void> deleteLikeByBookIdAndMemberId(@PathVariable Long bookId,
                                                              @RequestHeader("X-USER-ID") Long memberId) {
        likeService.deleteLikeByBookIdAndMemberId(bookId, memberId);
        return ResponseEntity.noContent().build();
    }
}
