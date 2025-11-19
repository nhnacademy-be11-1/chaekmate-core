package shop.chaekmate.core.book.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import shop.chaekmate.core.book.entity.BookImage;

import java.util.List;

import static shop.chaekmate.core.book.entity.QBookImage.bookImage;

@RequiredArgsConstructor
public class BookImageRepositoryImpl implements BookImageRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public List<BookImage> findAllByBookIdOrderByCreatedAtAsc(Long bookId) {
        return queryFactory
                .selectFrom(bookImage)
                .where(bookImage.book.id.eq(bookId))
                .orderBy(bookImage.createdAt.asc())
                .fetch();
    }
}
