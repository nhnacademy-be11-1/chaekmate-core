package shop.chaekmate.core.book.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import shop.chaekmate.core.book.dto.request.AdminBookPagedRequest;
import shop.chaekmate.core.book.dto.response.AdminBookResponse;
import shop.chaekmate.core.book.entity.Book;
import shop.chaekmate.core.book.entity.QBook;

import java.util.List;
import shop.chaekmate.core.book.entity.QBookImage;
import shop.chaekmate.core.order.entity.QReview;

@Repository
@RequiredArgsConstructor
public class AdminBookRepositoryImpl {

    private final JPAQueryFactory queryFactory;

    public List<Book> findRecentBooks(int limit) {
        QBook book = QBook.book;
        return queryFactory.selectFrom(book)
                .orderBy(book.createdAt.desc())
                .limit(limit)
                .fetch();
    }

    public Page<AdminBookResponse> findBooks(AdminBookPagedRequest req) {

        QBook book = QBook.book;
        QReview review = QReview.review;
        QBookImage bookImage = QBookImage.bookImage;

        // 검색 조건
        BooleanBuilder builder = new BooleanBuilder();
        if (req.getKeyword() != null && !req.getKeyword().isBlank()) {
            builder.and(
                    book.title.containsIgnoreCase(req.getKeyword())
                            .or(book.author.containsIgnoreCase(req.getKeyword()))
            );
        }

        // thumbnail subquery
        var thumbnail = JPAExpressions
                .select(bookImage.imageUrl)
                .from(bookImage)
                .where(bookImage.book.eq(book))
                .orderBy(bookImage.createdAt.asc())
                .limit(1);

        // 정렬 조건
        OrderSpecifier<?> orderSpecifier = switch (req.getSortType()) {
            case RECENT -> book.createdAt.desc();
            case TITLE -> book.title.asc();
            case REVIEW_COUNT -> review.count().desc();
        };

        // 목록 조회
        List<AdminBookResponse> content = queryFactory
                .select(Projections.constructor(
                        AdminBookResponse.class,
                        book.id,
                        book.title,
                        book.author,
                        thumbnail,
                        // 리뷰 개수
                        review.count().intValue()
                ))
                .from(book)
                .leftJoin(review).on(review.orderedBook.book.eq(book))
                .where(builder)
                .groupBy(book.id)
                .orderBy(orderSpecifier)
                .offset((long) req.getPage() * req.getSize())
                .limit(req.getSize())
                .fetch();

        // total count (검색 포함)
        Long total = queryFactory
                .select(book.count())
                .from(book)
                .where(builder)
                .fetchOne();

        return new PageImpl<>(content,
                PageRequest.of(req.getPage(), req.getSize()),
                total != null ? total : 0);
    }

}
