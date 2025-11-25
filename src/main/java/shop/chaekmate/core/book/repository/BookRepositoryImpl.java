package shop.chaekmate.core.book.repository;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import shop.chaekmate.core.book.dto.request.BookSearchCondition;
import shop.chaekmate.core.book.dto.response.BookListResponse;
import shop.chaekmate.core.book.dto.response.BookQueryResponse;
import shop.chaekmate.core.book.dto.response.QBookListResponse;

import java.util.List;
import shop.chaekmate.core.book.entity.QBookImage;

import static shop.chaekmate.core.book.entity.QBook.book;
import static shop.chaekmate.core.book.entity.QBookCategory.bookCategory;
import static shop.chaekmate.core.book.entity.QBookTag.bookTag;
import static shop.chaekmate.core.order.entity.QOrder.order;
import static shop.chaekmate.core.order.entity.QOrderedBook.orderedBook;
import static shop.chaekmate.core.review.entity.QReview.review;

@Repository
@RequiredArgsConstructor
public class BookRepositoryImpl implements BookRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<BookListResponse> searchBooks(BookSearchCondition condition, Pageable pageable) {

        QBookImage firstImage = QBookImage.bookImage; // 썸네일

        List<BookListResponse> content = queryFactory
                .select(new QBookListResponse(
                        book.id,
                        book.title,
                        book.author,
                        book.publisher,
                        book.price,
                        book.salesPrice,
                        firstImage.imageUrl
                ))
                .from(book)
                .leftJoin(bookCategory).on(bookCategory.book.eq(book))
                .leftJoin(bookTag).on(bookTag.book.eq(book))
                // 첫 번째 이미지만 LEFT JOIN
                .leftJoin(firstImage)
                .on(firstImage.book.eq(book)
                        .and(firstImage.id.eq(
                                JPAExpressions.select(firstImage.id.min())
                                        .from(firstImage)
                                        .where(firstImage.book.eq(book))
                        ))
                )
                .where(
                        categoryIdEq(condition.categoryId()),
                        tagIdEq(condition.tagId()),
                        keywordContains(condition.keyword())
                )
                .orderBy(book.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // total 쿼리
        Long total = queryFactory
                .select(book.countDistinct())
                .from(book)
                .leftJoin(bookCategory).on(bookCategory.book.eq(book))
                .leftJoin(bookTag).on(bookTag.book.eq(book))
                .where(
                        categoryIdEq(condition.categoryId()),
                        tagIdEq(condition.tagId()),
                        keywordContains(condition.keyword())
                )
                .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0L);
    }

    @Override
    public Slice<BookQueryResponse> findRecentlyAddedBooks(Pageable pageable) {
        QBookImage firstImage = new QBookImage("firstImage");

        List<BookQueryResponse> content = queryFactory
                .select(Projections.constructor(BookQueryResponse.class,
                        book.id,
                        book.title,
                        book.author,
                        book.price,
                        book.salesPrice,
                        JPAExpressions.select(review.rating.avg().coalesce(0.0))
                                .from(review)
                                .join(review.orderedBook, orderedBook)
                                .where(orderedBook.book.id.eq(book.id)),
                        JPAExpressions.select(review.count())
                                .from(review)
                                .join(review.orderedBook, orderedBook)
                                .where(orderedBook.book.id.eq(book.id)),
                        firstImage.imageUrl,
                        book.views
                ))
                .from(book)
                .leftJoin(firstImage)
                .on(firstImage.book.eq(book)
                        .and(firstImage.id.eq(
                                JPAExpressions.select(firstImage.id.min())
                                        .from(firstImage)
                                        .where(firstImage.book.eq(book))
                        ))
                )
                .orderBy(book.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1L)
                .fetch();

        boolean hasNext = false;
        if (content.size() > pageable.getPageSize()) {
            content.remove(pageable.getPageSize());
            hasNext = true;
        }

        return new SliceImpl<>(content, pageable, hasNext);
    }

    @Override
    public Slice<BookQueryResponse> findNewBooks(Pageable pageable) {
        QBookImage firstImage = new QBookImage("firstImage");

        List<BookQueryResponse> content = queryFactory
                .select(Projections.constructor(BookQueryResponse.class,
                        book.id,
                        book.title,
                        book.author,
                        book.price,
                        book.salesPrice,
                        JPAExpressions.select(review.rating.avg().coalesce(0.0))
                                .from(review)
                                .join(review.orderedBook, orderedBook)
                                .where(orderedBook.book.id.eq(book.id)),
                        JPAExpressions.select(review.count())
                                .from(review)
                                .join(review.orderedBook, orderedBook)
                                .where(orderedBook.book.id.eq(book.id)),
                        firstImage.imageUrl,
                        book.views
                ))
                .from(book)
                .leftJoin(firstImage)
                .on(firstImage.book.eq(book)
                        .and(firstImage.id.eq(
                                JPAExpressions.select(firstImage.id.min())
                                        .from(firstImage)
                                        .where(firstImage.book.eq(book))
                        ))
                )
                .orderBy(book.publishedAt.desc()) // Key difference here
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1L)
                .fetch();

        boolean hasNext = false;
        if (content.size() > pageable.getPageSize()) {
            content.remove(pageable.getPageSize());
            hasNext = true;
        }

        return new SliceImpl<>(content, pageable, hasNext);
    }

    @Override
    public Slice<BookQueryResponse> findAllBooks(BookSearchCondition condition, Pageable pageable) {
        QBookImage firstImage = new QBookImage("firstImage");

        List<BookQueryResponse> content = queryFactory
                .select(Projections.constructor(BookQueryResponse.class,
                        book.id,
                        book.title,
                        book.author,
                        book.price,
                        book.salesPrice,
                        JPAExpressions.select(review.rating.avg().coalesce(0.0))
                                .from(review)
                                .join(review.orderedBook, orderedBook)
                                .where(orderedBook.book.id.eq(book.id)),
                        JPAExpressions.select(review.count())
                                .from(review)
                                .join(review.orderedBook, orderedBook)
                                .where(orderedBook.book.id.eq(book.id)),
                        firstImage.imageUrl,
                        book.views
                ))
                .from(book)
                .leftJoin(bookCategory).on(bookCategory.book.eq(book))
                .leftJoin(bookTag).on(bookTag.book.eq(book))
                .leftJoin(firstImage)
                .on(firstImage.book.eq(book)
                        .and(firstImage.id.eq(
                                JPAExpressions.select(firstImage.id.min())
                                        .from(firstImage)
                                        .where(firstImage.book.eq(book))
                        ))
                )
                .where(
                        categoryIdEq(condition.categoryId()),
                        tagIdEq(condition.tagId()),
                        keywordContains(condition.keyword())
                )
                .orderBy(book.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1L)
                .fetch();

        boolean hasNext = false;
        if (content.size() > pageable.getPageSize()) {
            content.remove(pageable.getPageSize());
            hasNext = true;
        }

        return new SliceImpl<>(content, pageable, hasNext);
    }

    @Override
    public Slice<BookQueryResponse> findBestsellers(Pageable pageable) {
        QBookImage firstImage = new QBookImage("firstImage");

        List<BookQueryResponse> content = queryFactory
                .select(Projections.constructor(BookQueryResponse.class,
                        book.id,
                        book.title,
                        book.author,
                        book.price,
                        book.salesPrice,
                        JPAExpressions.select(review.rating.avg().coalesce(0.0))
                                .from(review)
                                .join(review.orderedBook, orderedBook)
                                .where(orderedBook.book.id.eq(book.id)),
                        JPAExpressions.select(review.count())
                                .from(review)
                                .join(review.orderedBook, orderedBook)
                                .where(orderedBook.book.id.eq(book.id)),
                        firstImage.imageUrl,
                        book.views
                ))
                .from(book)
                .leftJoin(firstImage)
                .on(firstImage.book.eq(book)
                        .and(firstImage.id.eq(
                                JPAExpressions.select(firstImage.id.min())
                                        .from(firstImage)
                                        .where(firstImage.book.eq(book))
                        ))
                )
                .orderBy(new OrderSpecifier<>(Order.DESC, JPAExpressions.select(orderedBook.count())
                        .from(orderedBook)
                        .where(orderedBook.book.id.eq(book.id))))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1L)
                .fetch();

        boolean hasNext = false;
        if (content.size() > pageable.getPageSize()) {
            content.remove(pageable.getPageSize());
            hasNext = true;
        }

        return new SliceImpl<>(content, pageable, hasNext);
    }

    @Override
    public Slice<BookQueryResponse> findTopReviewedBooksForLast30Days(Pageable pageable) {
        QBookImage firstImage = new QBookImage("firstImage");
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);

        List<BookQueryResponse> content = queryFactory
                .select(Projections.constructor(BookQueryResponse.class,
                        book.id,
                        book.title,
                        book.author,
                        book.price,
                        book.salesPrice,
                        JPAExpressions.select(review.rating.avg().coalesce(0.0))
                                .from(review)
                                .join(review.orderedBook, orderedBook)
                                .where(orderedBook.book.id.eq(book.id)),
                        JPAExpressions.select(review.count())
                                .from(review)
                                .join(review.orderedBook, orderedBook)
                                .where(orderedBook.book.id.eq(book.id)),
                        firstImage.imageUrl,
                        book.views
                ))
                .from(book)
                .leftJoin(firstImage)
                .on(firstImage.book.eq(book)
                        .and(firstImage.id.eq(
                                JPAExpressions.select(firstImage.id.min())
                                        .from(firstImage)
                                        .where(firstImage.book.eq(book))
                        ))
                )
                .where(JPAExpressions.select(review.count())
                        .from(review)
                        .join(review.orderedBook, orderedBook)
                        .where(orderedBook.book.id.eq(book.id)
                                .and(review.createdAt.after(thirtyDaysAgo)))
                        .gt(0L))
                .orderBy(new OrderSpecifier<>(Order.DESC, JPAExpressions.select(review.count())
                        .from(review)
                        .join(review.orderedBook, orderedBook)
                        .where(orderedBook.book.id.eq(book.id)
                                .and(review.createdAt.after(thirtyDaysAgo)))))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1L)
                .fetch();

        boolean hasNext = false;
        if (content.size() > pageable.getPageSize()) {
            content.remove(pageable.getPageSize());
            hasNext = true;
        }

        return new SliceImpl<>(content, pageable, hasNext);
    }

    @Override
    public Slice<BookQueryResponse> findRandomInStockBooks(Pageable pageable) {
        QBookImage firstImage = new QBookImage("firstImage");

        List<BookQueryResponse> content = queryFactory
                .select(Projections.constructor(BookQueryResponse.class,
                        book.id,
                        book.title,
                        book.author,
                        book.price,
                        book.salesPrice,
                        JPAExpressions.select(review.rating.avg().coalesce(0.0))
                                .from(review)
                                .join(review.orderedBook, orderedBook)
                                .where(orderedBook.book.id.eq(book.id)),
                        JPAExpressions.select(review.count())
                                .from(review)
                                .join(review.orderedBook, orderedBook)
                                .where(orderedBook.book.id.eq(book.id)),
                        firstImage.imageUrl,
                        book.views
                ))
                .from(book)
                .leftJoin(firstImage)
                .on(firstImage.book.eq(book)
                        .and(firstImage.id.eq(
                                JPAExpressions.select(firstImage.id.min())
                                        .from(firstImage)
                                        .where(firstImage.book.eq(book))
                        ))
                )
                .where(book.stock.gt(0))
                .orderBy(Expressions.numberTemplate(Double.class, "function('RAND')").asc())
                .limit(pageable.getPageSize())
                .fetch();

        return new SliceImpl<>(content,Pageable.unpaged(),false);
    }

    @Override
    public Slice<BookQueryResponse> findBooksByViews(Pageable pageable) {
        QBookImage firstImage = new QBookImage("firstImage");

        List<BookQueryResponse> content = queryFactory
                .select(Projections.constructor(BookQueryResponse.class,
                        book.id,
                        book.title,
                        book.author,
                        book.price,
                        book.salesPrice,
                        JPAExpressions.select(review.rating.avg().coalesce(0.0))
                                .from(review)
                                .join(review.orderedBook, orderedBook)
                                .where(orderedBook.book.id.eq(book.id)),
                        JPAExpressions.select(review.count())
                                .from(review)
                                .join(review.orderedBook, orderedBook)
                                .where(orderedBook.book.id.eq(book.id)),
                        firstImage.imageUrl,
                        book.views
                ))
                .from(book)
                .leftJoin(firstImage)
                .on(firstImage.book.eq(book)
                        .and(firstImage.id.eq(
                                JPAExpressions.select(firstImage.id.min())
                                        .from(firstImage)
                                        .where(firstImage.book.eq(book))
                        ))
                )
                .orderBy(book.views.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1L)
                .fetch();

        boolean hasNext = false;
        if (content.size() > pageable.getPageSize()) {
            content.remove(pageable.getPageSize());
            hasNext = true;
        }

        return new SliceImpl<>(content, pageable, hasNext);
    }

    @Override
    public Slice<BookQueryResponse> findEarlyAdopterPicks(Pageable pageable) {
        QBookImage firstImage = new QBookImage("firstImage");
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);

        List<BookQueryResponse> content = queryFactory
                .select(Projections.constructor(BookQueryResponse.class,
                        book.id,
                        book.title,
                        book.author,
                        book.price,
                        book.salesPrice,
                        JPAExpressions.select(review.rating.avg().coalesce(0.0))
                                .from(review)
                                .join(review.orderedBook, orderedBook)
                                .where(orderedBook.book.id.eq(book.id)),
                        JPAExpressions.select(review.count())
                                .from(review)
                                .join(review.orderedBook, orderedBook)
                                .where(orderedBook.book.id.eq(book.id)),
                        firstImage.imageUrl,
                        book.views
                ))
                .from(book)
                .leftJoin(firstImage)
                .on(firstImage.book.eq(book)
                        .and(firstImage.id.eq(
                                JPAExpressions.select(firstImage.id.min())
                                        .from(firstImage)
                                        .where(firstImage.book.eq(book))
                        ))
                )
                .where(JPAExpressions.select(orderedBook.count())
                        .from(orderedBook)
                        .where(orderedBook.book.id.eq(book.id)
                                .and(orderedBook.createdAt.after(thirtyDaysAgo)))
                        .gt(0L))
                .orderBy(new OrderSpecifier<>(Order.DESC, JPAExpressions.select(order.member.id.countDistinct())
                        .from(orderedBook)
                        .join(orderedBook.order, order)
                        .where(orderedBook.book.id.eq(book.id)
                                .and(orderedBook.createdAt.after(thirtyDaysAgo)))))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1L)
                .fetch();

        boolean hasNext = false;
        if (content.size() > pageable.getPageSize()) {
            content.remove(pageable.getPageSize());
            hasNext = true;
        }

        return new SliceImpl<>(content, pageable, hasNext);
    }


    private BooleanExpression categoryIdEq(Long categoryId) {
        return categoryId != null ? bookCategory.category.id.eq(categoryId) : null;
    }

    private BooleanExpression tagIdEq(Long tagId) {
        return tagId != null ? bookTag.tag.id.eq(tagId) : null;
    }

    private BooleanExpression keywordContains(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return null;
        }
        return book.title.containsIgnoreCase(keyword)
                .or(book.author.containsIgnoreCase(keyword))
                .or(book.publisher.containsIgnoreCase(keyword));
    }
}
