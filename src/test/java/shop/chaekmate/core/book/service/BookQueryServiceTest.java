package shop.chaekmate.core.book.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.test.context.ActiveProfiles;
import shop.chaekmate.core.book.dto.request.BookSearchCondition;
import shop.chaekmate.core.book.dto.request.RankingType;
import shop.chaekmate.core.book.dto.response.BookQueryResponse;
import shop.chaekmate.core.book.dto.response.BookQuerySliceResponse;
import shop.chaekmate.core.book.repository.BookRepository;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class BookQueryServiceTest {

    @InjectMocks
    private BookQueryService bookQueryService;

    @Mock
    private BookRepository bookRepository;

    private BookQueryResponse createMockResponse() {
        return new BookQueryResponse(1L, "title", "author", 10000, 9000, 4.5, 10L, "url", 100L);
    }

    @Nested
    @DisplayName("도서 목록 조회")
    class FindBooks {

        @Test
        void 최근_추가된_도서_조회_성공() {
            // given
            Pageable pageable = PageRequest.of(0, 10);
            List<BookQueryResponse> content = Collections.singletonList(createMockResponse());
            Slice<BookQueryResponse> mockSlice = new SliceImpl<>(content, pageable, true);
            when(bookRepository.findRecentlyAddedBooks(pageable)).thenReturn(mockSlice);

            // when
            BookQuerySliceResponse result = bookQueryService.findRecentlyAddedBooks(pageable);

            // then
            assertThat(result.content()).hasSize(1);
            assertThat(result.hasNext()).isTrue();
            verify(bookRepository).findRecentlyAddedBooks(pageable);
        }

        @Test
        void 맞춤_추천_도서_조회_성공() {
            // given
            Pageable pageable = PageRequest.of(0, 5);
            List<BookQueryResponse> content = Collections.nCopies(5, createMockResponse());
            Slice<BookQueryResponse> mockSlice = new SliceImpl<>(content, pageable, false);
            when(bookRepository.findRandomInStockBooks(pageable)).thenReturn(mockSlice);

            // when
            BookQuerySliceResponse result = bookQueryService.findPersonalizedRecommendedBooks(pageable);

            // then
            assertThat(result.content()).hasSize(5);
            assertThat(result.hasNext()).isFalse();
            verify(bookRepository).findRandomInStockBooks(pageable);
        }

        @Test
        void 삼십일간_리뷰_많은_책_조회_성공() {
            // given
            Pageable pageable = PageRequest.of(0, 10);
            List<BookQueryResponse> content = Collections.singletonList(createMockResponse());
            Slice<BookQueryResponse> mockSlice = new SliceImpl<>(content, pageable, true);
            when(bookRepository.findTopReviewedBooksForLast30Days(pageable)).thenReturn(mockSlice);

            // when
            BookQuerySliceResponse result = bookQueryService.findTopReviewedBooksForLast30Days(pageable);

            // then
            assertThat(result.content()).hasSize(1);
            assertThat(result.hasNext()).isTrue();
            verify(bookRepository).findTopReviewedBooksForLast30Days(pageable);
        }
        
        @Test
        void 얼리어답터들의_픽_조회_성공() {
            // given
            Pageable pageable = PageRequest.of(0, 10);
            List<BookQueryResponse> content = Collections.singletonList(createMockResponse());
            Slice<BookQueryResponse> mockSlice = new SliceImpl<>(content, pageable, true);
            when(bookRepository.findEarlyAdopterPicks(pageable)).thenReturn(mockSlice);

            // when
            BookQuerySliceResponse result = bookQueryService.findEarlyAdopterPicks(pageable);

            // then
            assertThat(result.content()).hasSize(1);
            assertThat(result.hasNext()).isTrue();
            verify(bookRepository).findEarlyAdopterPicks(pageable);
        }

        @Test
        void 책메이트_추천_도서_조회_성공() {
            // given
            Pageable pageable = PageRequest.of(0, 5);
            List<BookQueryResponse> content = Collections.nCopies(5, createMockResponse());
            Slice<BookQueryResponse> mockSlice = new SliceImpl<>(content, pageable, false);
            when(bookRepository.findRandomInStockBooks(pageable)).thenReturn(mockSlice);

            // when
            BookQuerySliceResponse result = bookQueryService.findChaekmateRecommendedBooks(pageable);

            // then
            assertThat(result.content()).hasSize(5);
            assertThat(result.hasNext()).isFalse();
            verify(bookRepository).findRandomInStockBooks(pageable);
        }

        @Test
        void 신간도서_조회_성공() {
            // given
            Pageable pageable = PageRequest.of(0, 10);
            List<BookQueryResponse> content = Collections.singletonList(createMockResponse());
            Slice<BookQueryResponse> mockSlice = new SliceImpl<>(content, pageable, true);
            when(bookRepository.findNewBooks(pageable)).thenReturn(mockSlice);

            // when
            BookQuerySliceResponse result = bookQueryService.findNewBooks(pageable);

            // then
            assertThat(result.content()).hasSize(1);
            assertThat(result.hasNext()).isTrue();
            verify(bookRepository).findNewBooks(pageable);
        }

        @Test
        void 전체도서_조회_성공() {
            // given
            Pageable pageable = PageRequest.of(0, 10);
            BookSearchCondition condition = new BookSearchCondition(1L, 1L, "keyword");
            List<BookQueryResponse> content = Collections.singletonList(createMockResponse());
            Slice<BookQueryResponse> mockSlice = new SliceImpl<>(content, pageable, true);
            when(bookRepository.findAllBooks(condition, pageable)).thenReturn(mockSlice);

            // when
            BookQuerySliceResponse result = bookQueryService.findAllBooks(condition, pageable);

            // then
            assertThat(result.content()).hasSize(1);
            assertThat(result.hasNext()).isTrue();
            verify(bookRepository).findAllBooks(condition, pageable);
        }

        @Test
        void 베스트셀러_조회_성공() {
            // given
            Pageable pageable = PageRequest.of(0, 10);
            List<BookQueryResponse> content = Collections.singletonList(createMockResponse());
            Slice<BookQueryResponse> mockSlice = new SliceImpl<>(content, pageable, true);
            when(bookRepository.findBestsellers(pageable)).thenReturn(mockSlice);

            // when
            BookQuerySliceResponse result = bookQueryService.findBestsellers(pageable);

            // then
            assertThat(result.content()).hasSize(1);
            assertThat(result.hasNext()).isTrue();
            verify(bookRepository).findBestsellers(pageable);
        }

        @Nested
        @DisplayName("랭킹 조회")
        class Ranking {

            @Test
            void 조회수_랭킹_조회_성공() {
                // given
                Pageable pageable = PageRequest.of(0, 10);
                List<BookQueryResponse> content = Collections.singletonList(createMockResponse());
                Slice<BookQueryResponse> mockSlice = new SliceImpl<>(content, pageable, true);
                when(bookRepository.findBooksByViews(pageable)).thenReturn(mockSlice);

                // when
                BookQuerySliceResponse result = bookQueryService.findBookRanking(RankingType.VIEWS, pageable);

                // then
                assertThat(result.content()).hasSize(1);
                assertThat(result.hasNext()).isTrue();
                verify(bookRepository).findBooksByViews(pageable);
            }

            @Test
            void 판매량_랭킹_조회_성공() {
                // given
                Pageable pageable = PageRequest.of(0, 10);
                List<BookQueryResponse> content = Collections.singletonList(createMockResponse());
                Slice<BookQueryResponse> mockSlice = new SliceImpl<>(content, pageable, true);
                when(bookRepository.findBestsellers(pageable)).thenReturn(mockSlice);

                // when
                BookQuerySliceResponse result = bookQueryService.findBookRanking(RankingType.SALES, pageable);

                // then
                assertThat(result.content()).hasSize(1);
                assertThat(result.hasNext()).isTrue();
                verify(bookRepository).findBestsellers(pageable);
            }
        }
    }
}
