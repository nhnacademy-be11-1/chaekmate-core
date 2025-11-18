package shop.chaekmate.core.book.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import shop.chaekmate.core.book.entity.QBookTag;
import shop.chaekmate.core.book.entity.QTag;

@RequiredArgsConstructor
public class BookTagRepositoryImpl implements BookTagRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<String> findTagNamesByBookId(Long bookId){
        QBookTag bookTag = QBookTag.bookTag;
        QTag tag = QTag.tag;
        return queryFactory
                .select(tag.name)
                .from(bookTag)
                .join(bookTag.tag,tag)
                .where(bookTag.book.id.eq(bookId))
                .fetch();
    }
}
