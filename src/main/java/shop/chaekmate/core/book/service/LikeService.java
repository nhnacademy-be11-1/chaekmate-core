package shop.chaekmate.core.book.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import shop.chaekmate.core.book.dto.response.LikeResponse;
import shop.chaekmate.core.book.entity.Book;
import shop.chaekmate.core.book.entity.Like;
import shop.chaekmate.core.book.exception.BookNotFoundException;
import shop.chaekmate.core.book.exception.LikeNotFoundException;
import shop.chaekmate.core.book.exception.LikeNotFoundForBookAndMemberException;
import shop.chaekmate.core.book.exception.MemberNotFoundException;
import shop.chaekmate.core.book.repository.BookRepository;
import shop.chaekmate.core.book.repository.LikeRepository;
import shop.chaekmate.core.member.entity.Member;
import shop.chaekmate.core.member.repository.MemberRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final MemberRepository memberRepository;
    private final LikeRepository likeRepository;
    private final BookRepository bookRepository;

    @Transactional
    public LikeResponse createLike(Long bookId, Long memberId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("Book not found"));

        Member member = memberRepository.findById(memberId)
                .orElseThrow(MemberNotFoundException::new);

        Like like = new Like(book, member);

        likeRepository.save(like);

        return new LikeResponse(like.getId(), like.getBook().getId(), like.getMember().getId());
    }

    @Transactional
    public LikeResponse readLikeById(Long likeId) {

        Like like = likeRepository.findById(likeId)
                .orElseThrow(LikeNotFoundException::new);

        return new LikeResponse(like.getId(), like.getBook().getId(), like.getMember().getId());
    }

    @Transactional
    public List<LikeResponse> getBookLikes(Long bookId) {
        if (!bookRepository.existsById(bookId)) {
            throw new BookNotFoundException("Book not found");
        }

        List<Like> likeList = likeRepository.findByBook_Id(bookId);

        return likeList.stream()
                .map(like -> new LikeResponse(like.getId(), like.getBook().getId(), like.getMember().getId())).toList();
    }

    @Transactional
    public List<LikeResponse> getMemberLikes(Long memberId) {
        if (!memberRepository.existsById(memberId)) {
            throw new MemberNotFoundException();
        }

        List<Like> likeList = likeRepository.findByMember_Id(memberId);

        return likeList.stream()
                .map(like -> new LikeResponse(like.getId(), like.getBook().getId(), like.getMember().getId())).toList();
    }

    @Transactional
    public void deleteLikeById(Long likeId) {

        Like like = likeRepository.findById(likeId)
                .orElseThrow(LikeNotFoundException::new);

        likeRepository.delete(like);
    }

    @Transactional
    public void deleteLikeByBookIdAndMemberId(Long bookId, Long memberId) {

        Like like = likeRepository.findByBook_IdAndMember_Id(bookId, memberId)
                .orElseThrow(LikeNotFoundForBookAndMemberException::new);

        likeRepository.delete(like);
    }

}
