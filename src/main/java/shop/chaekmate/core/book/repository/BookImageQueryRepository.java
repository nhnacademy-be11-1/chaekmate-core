package shop.chaekmate.core.book.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import shop.chaekmate.core.book.entity.BookImage;

import java.util.List;

import static shop.chaekmate.core.book.entity.QBookImage.bookImage;

@Repository
@RequiredArgsConstructor
public class BookImageQueryRepository {

    private final JPAQueryFactory queryFactory;

    public List<BookImage> findAllByBookIdOrderByCreatedAtAsc(Long bookId) {
        return queryFactory
                .selectFrom(bookImage)
                .where(bookImage.book.id.eq(bookId))
                .orderBy(bookImage.createdAt.asc())
                .fetch();
    }
}
