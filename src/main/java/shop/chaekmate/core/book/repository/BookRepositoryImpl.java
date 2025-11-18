package shop.chaekmate.core.book.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import shop.chaekmate.core.book.dto.request.BookSearchCondition;
import shop.chaekmate.core.book.dto.response.BookListResponse;
import shop.chaekmate.core.book.dto.response.QBookListResponse;

import java.util.List;

import static shop.chaekmate.core.book.entity.QBook.book;
import static shop.chaekmate.core.book.entity.QBookCategory.bookCategory;
import static shop.chaekmate.core.book.entity.QBookImage.bookImage;
import static shop.chaekmate.core.book.entity.QBookTag.bookTag;

@Repository
@RequiredArgsConstructor
public class BookRepositoryImpl implements BookRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<BookListResponse> searchBooks(BookSearchCondition condition, Pageable pageable) {

        // 서브쿼리: 각 책의 첫 번째 이미지 URL (섬네일)
        JPQLQuery<String> imageUrlSubQuery = JPAExpressions
                .select(bookImage.imageUrl)
                .from(bookImage)
                .where(bookImage.book.id.eq(book.id))
                .orderBy(bookImage.id.asc())
                .limit(1);

        List<BookListResponse> content = queryFactory
                .select(new QBookListResponse(
                        book.id,
                        book.title,
                        book.author,
                        book.publisher,
                        book.price,
                        book.salesPrice,
                        imageUrlSubQuery
                ))
                .from(book)
                .leftJoin(bookCategory).on(bookCategory.book.eq(book))
                .leftJoin(bookTag).on(bookTag.book.eq(book))
                // bookImage 조인 제거
                .where(
                        categoryIdEq(condition.categoryId()),
                        tagIdEq(condition.tagId()),
                        keywordContains(condition.keyword())
                )
                .groupBy(book.id)
                .orderBy(book.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

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
