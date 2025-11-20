package shop.chaekmate.core.book.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
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
import shop.chaekmate.core.book.entity.QBookImage;

import static shop.chaekmate.core.book.entity.QBook.book;
import static shop.chaekmate.core.book.entity.QBookCategory.bookCategory;
import static shop.chaekmate.core.book.entity.QBookTag.bookTag;

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
