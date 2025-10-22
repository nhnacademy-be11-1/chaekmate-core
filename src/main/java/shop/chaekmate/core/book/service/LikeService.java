package shop.chaekmate.core.book.service;

import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.stereotype.Service;
import shop.chaekmate.core.book.dto.CreateLikeRequest;
import shop.chaekmate.core.book.dto.DeleteLikeRequest;
import shop.chaekmate.core.book.dto.LikeResponse;
import shop.chaekmate.core.book.entity.Book;
import shop.chaekmate.core.book.entity.Like;
import shop.chaekmate.core.book.repository.BookRepository;
import shop.chaekmate.core.book.repository.LikeRepository;
import shop.chaekmate.core.member.entity.Member;
import shop.chaekmate.core.member.repository.MemberRepository;

@Service
public class LikeService {

    private final MemberRepository memberRepository;
    private final LikeRepository likeRepository;
    private final BookRepository bookRepository;

    public LikeService(MemberRepository memberRepository, LikeRepository likeRepository,
                       BookRepository bookRepository) {
        this.memberRepository = memberRepository;
        this.likeRepository = likeRepository;
        this.bookRepository = bookRepository;
    }


    @Transactional
    public LikeResponse createLike(Long bookId, CreateLikeRequest request) {
        Long memberId = request.memberId();

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("해당하는 도서를 찾을 수 없습니다"));

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("해당하는 회원을 찾을 수 없습니다"));

        Like like = new Like(book, member);

        likeRepository.save(like);

        return new LikeResponse(like.getId(), like.getBook().getId(), like.getMember().getId());
    }

    @Transactional
    public LikeResponse readLikeById(Long likeId) {

        Like like = likeRepository.findById(likeId)
                .orElseThrow(() -> new RuntimeException("해당하는 Id의 like 를 찾을 수 없습니다"));

        return new LikeResponse(like.getId(), like.getBook().getId(), like.getMember().getId());
    }

    @Transactional
    public List<LikeResponse> getBookLikes(Long bookId) {
        if (!bookRepository.existsById(bookId)) {
            throw new RuntimeException("해당하는 Id의 book을 찾을 수 없습니다");
        }

        List<Like> likeList = likeRepository.findByBook_Id(bookId);

        return likeList.stream()
                .map(like -> new LikeResponse(like.getId(), like.getBook().getId(), like.getMember().getId())).toList();
    }

    @Transactional
    public List<LikeResponse> getMemberLikes(Long memberId) {
        if (!memberRepository.existsById(memberId)) {
            throw new RuntimeException("해당하는 Id의 member를 찾을 수 없습니다");
        }

        List<Like> likeList = likeRepository.findByMember_Id(memberId);

        return likeList.stream()
                .map(like -> new LikeResponse(like.getId(), like.getBook().getId(), like.getMember().getId())).toList();
    }

    @Transactional
    public void deleteLikeById(Long likeId) {

        Like like = likeRepository.findById(likeId)
                .orElseThrow(() -> new RuntimeException("해당하는 Id의 like 를 찾을 수 없습니다"));

        likeRepository.delete(like);
    }

    @Transactional
    public void deleteLikeByBookIdAndMemberId(Long bookId, DeleteLikeRequest request) {

        Like like = likeRepository.findByBook_IdAndMember_Id(bookId, request.memberId())
                .orElseThrow(() -> new RuntimeException("해당하는 bookId, memberId 의 like 가 없습니다"));

        likeRepository.delete(like);
    }

}
