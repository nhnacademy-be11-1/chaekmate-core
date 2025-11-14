package shop.chaekmate.core.book.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import shop.chaekmate.core.book.entity.QBookCategory;
import shop.chaekmate.core.book.entity.QCategory;

@RequiredArgsConstructor
public class BookCategoryRepositoryImpl implements BookCategoryRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<String> findCategoryNamesByBookId(Long bookId){
        QBookCategory bookCategory = QBookCategory.bookCategory;
        QCategory category = QCategory.category;

        return queryFactory
                .select(category.name)
                .from(bookCategory)
                .join(bookCategory.category, category)
                .where(bookCategory.book.id.eq(bookId))
                .fetch();
    }
}
