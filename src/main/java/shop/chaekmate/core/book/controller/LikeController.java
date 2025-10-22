package shop.chaekmate.core.book.controller;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import shop.chaekmate.core.book.dto.CreateLikeRequest;
import shop.chaekmate.core.book.dto.DeleteLikeRequest;
import shop.chaekmate.core.book.dto.LikeResponse;
import shop.chaekmate.core.book.service.LikeService;

@RestController
public class LikeController {

    private final LikeService likeService;

    public LikeController(LikeService likeService) {
        this.likeService = likeService;
    }

    // 특정 책, 특정 회원 좋아요 생성
    @PostMapping(path = "/books/{bookId}/likes")
    @ResponseStatus(HttpStatus.CREATED)
    public LikeResponse createLike(@PathVariable(name = "bookId") Long bookId,
                                   @Valid @RequestBody CreateLikeRequest createLikeRequest) {

        return likeService.createLike(bookId, createLikeRequest);
    }

    // 식별자로 좋아요 조회
    @GetMapping(path = "/likes/{likeId}")
    @ResponseStatus(HttpStatus.OK)
    public LikeResponse readLike(@PathVariable(name = "likeId") Long likeId) {

        return likeService.readLikeById(likeId);
    }

    // 특정 도서의 전체 Like 조회
    @GetMapping(path = "/books/{bookId}/likes")
    @ResponseStatus(HttpStatus.OK)
    public List<LikeResponse> getBookLikes(@PathVariable(name = "bookId") Long bookId) {

        return likeService.getBookLikes(bookId);
    }

    // 특정 회원의 전체 Like 조회
    @GetMapping(path = "/members/{memberId}/likes")
    @ResponseStatus(HttpStatus.OK)
    public List<LikeResponse> getMemberLikes(@PathVariable(name = "memberId") Long memberId) {

        return likeService.getMemberLikes(memberId);
    }

    // 식별자로 좋아요 삭제
    @DeleteMapping(path = "/likes/{likeId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLikeById(@PathVariable(name = "likeId") Long likeId) {

        likeService.deleteLikeById(likeId);
    }

    // 회원의 특정 책 좋아요 취소
    @DeleteMapping(path = "/books/{bookId}/likes")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLikeByBookIdAndMemberId(@PathVariable(name = "bookId") Long bookId,
                                              @RequestBody DeleteLikeRequest deleteLikeRequest) {

        likeService.deleteLikeByBookIdAndMemberId(bookId, deleteLikeRequest);
    }

}
