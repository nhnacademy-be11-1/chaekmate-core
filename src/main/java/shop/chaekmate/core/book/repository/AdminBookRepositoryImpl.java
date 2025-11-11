package shop.chaekmate.core.book.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import shop.chaekmate.core.book.entity.Book;
import shop.chaekmate.core.book.entity.QBook;

import java.util.List;

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
}
