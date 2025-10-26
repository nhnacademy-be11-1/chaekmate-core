package shop.chaekmate.core.book.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import shop.chaekmate.core.book.dto.CreateLikeRequest;
import shop.chaekmate.core.book.dto.DeleteLikeRequest;
import shop.chaekmate.core.book.dto.LikeResponse;
import shop.chaekmate.core.book.entity.Book;
import shop.chaekmate.core.book.entity.Like;
import shop.chaekmate.core.book.repository.BookRepository;
import shop.chaekmate.core.book.repository.LikeRepository;
import shop.chaekmate.core.member.entity.Member;
import shop.chaekmate.core.member.repository.MemberRepository;

@ExtendWith(MockitoExtension.class)
class LikeServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private LikeService likeService;

    @Test
    void 좋아요_생성_성공() {
        // given
        long bookId = 1L;
        long memberId = 1L;
        long likeId = 1L;
        var request = new CreateLikeRequest(memberId);
        var book = mock(Book.class);
        var member = mock(Member.class);
        var savedLike = mock(Like.class);

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));

        doAnswer(invocation -> {
            Like actualLike = invocation.getArgument(0);
            java.lang.reflect.Field idField = Like.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(actualLike, likeId);
            return actualLike;
        }).when(likeRepository).save(any(Like.class));

        when(book.getId()).thenReturn(bookId);
        when(member.getId()).thenReturn(memberId);

        // when
        var response = likeService.createLike(bookId, request);

        // then
        assertNotNull(response);
        assertEquals(likeId, response.id());
        assertEquals(bookId, response.bookId());
        assertEquals(memberId, response.memeberId());
    }

    @Test
    void ID로_좋아요_조회_성공() {
        // given
        long likeId = 1L;
        long bookId = 2L;
        long memberId = 3L;
        var book = mock(Book.class);
        var member = mock(Member.class);
        var like = mock(Like.class);

        when(likeRepository.findById(likeId)).thenReturn(Optional.of(like));
        when(like.getId()).thenReturn(likeId);
        when(like.getBook()).thenReturn(book);
        when(like.getMember()).thenReturn(member);
        when(book.getId()).thenReturn(bookId);
        when(member.getId()).thenReturn(memberId);

        // when
        var response = likeService.readLikeById(likeId);

        // then
        assertNotNull(response);
        assertEquals(likeId, response.id());
        assertEquals(bookId, response.bookId());
        assertEquals(memberId, response.memeberId());
    }

    @Test
    void ID로_좋아요_조회_실패_찾을_수_없음() {
        // given
        long likeId = 1L;
        when(likeRepository.findById(likeId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(RuntimeException.class, () -> likeService.readLikeById(likeId));
    }

    @Test
    @DisplayName("책 ID로 좋아요 목록 조회 성공")
    void 책_ID로_좋아요_목록_조회_성공() {
        // given
        long bookId = 1L;
        var book = mock(Book.class);
        var member = mock(Member.class);
        var like = mock(Like.class);
        var likes = Collections.singletonList(like);

        when(bookRepository.existsById(bookId)).thenReturn(true);
        when(likeRepository.findByBook_Id(bookId)).thenReturn(likes);

        when(like.getId()).thenReturn(1L);
        when(like.getBook()).thenReturn(book);
        when(like.getMember()).thenReturn(member);
        when(book.getId()).thenReturn(bookId);
        when(member.getId()).thenReturn(1L);

        // when
        List<LikeResponse> responses = likeService.getBookLikes(bookId);

        // then
        assertEquals(1, responses.size());
        assertEquals(1L, responses.get(0).id());
        assertEquals(bookId, responses.get(0).bookId());
    }

    @Test
    void 회원_ID로_좋아요_목록_조회_성공() {
        // given
        long memberId = 1L;
        var book = mock(Book.class);
        var member = mock(Member.class);
        var like = mock(Like.class);
        var likes = Collections.singletonList(like);

        when(memberRepository.existsById(memberId)).thenReturn(true);
        when(likeRepository.findByMember_Id(memberId)).thenReturn(likes);

        when(like.getId()).thenReturn(1L);
        when(like.getBook()).thenReturn(book);
        when(like.getMember()).thenReturn(member);
        when(book.getId()).thenReturn(1L);
        when(member.getId()).thenReturn(memberId);

        // when
        List<LikeResponse> responses = likeService.getMemberLikes(memberId);

        // then
        assertEquals(1, responses.size());
        assertEquals(memberId, responses.get(0).memeberId());
    }

    @Test
    void ID로_좋아요_삭제_성공() {
        // given
        long likeId = 1L;
        var like = mock(Like.class);
        when(likeRepository.findById(likeId)).thenReturn(Optional.of(like));
        doNothing().when(likeRepository).delete(like);

        // when
        likeService.deleteLikeById(likeId);

        // then
        verify(likeRepository, times(1)).delete(like);
    }

    @Test
    void 책_및_회원_ID로_좋아요_삭제_성공() {
        // given
        long bookId = 1L;
        long memberId = 1L;
        var request = new DeleteLikeRequest(memberId);
        var like = mock(Like.class);

        when(likeRepository.findByBook_IdAndMember_Id(bookId, memberId)).thenReturn(Optional.of(like));
        doNothing().when(likeRepository).delete(like);

        // when
        likeService.deleteLikeByBookIdAndMemberId(bookId, request);

        // then
        verify(likeRepository, times(1)).delete(like);
    }
}
