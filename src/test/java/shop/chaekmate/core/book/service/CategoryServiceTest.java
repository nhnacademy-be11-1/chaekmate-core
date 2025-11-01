package shop.chaekmate.core.book.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import shop.chaekmate.core.book.dto.request.CreateCategoryRequest;
import shop.chaekmate.core.book.entity.Category;
import shop.chaekmate.core.book.exception.CategoryHasBookException;
import shop.chaekmate.core.book.exception.CategoryHasChildException;
import shop.chaekmate.core.book.exception.CategoryNotFoundException;
import shop.chaekmate.core.book.exception.ParentCategoryNotFoundException;
import shop.chaekmate.core.book.repository.BookCategoryRepository;
import shop.chaekmate.core.book.repository.CategoryRepository;

@ActiveProfiles("test")
@SuppressWarnings("NonAsciiCharacters")
@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @InjectMocks
    private CategoryService categoryService;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private BookCategoryRepository bookCategoryRepository;


    @Test
    void 카테고리_삭제_실패_존재하지_않는_부모_카테고리() {
        CreateCategoryRequest request = new CreateCategoryRequest(999L, "Child");

        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ParentCategoryNotFoundException.class, () -> categoryService.createCategory(request));
    }


    @Test
    void 카테고리_삭제_실패_존재하지_않는_카테고리() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CategoryNotFoundException.class, () -> categoryService.deleteCategory(1L));
    }

    @Test
    void 카테고리_삭제_실패_카테고리에_책이_존재() {
        Category category = new Category(null, "test");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(bookCategoryRepository.existsByCategory(category)).thenReturn(true);

        assertThrows(CategoryHasBookException.class, () -> categoryService.deleteCategory(1L));
    }

    @Test
    void 카테고리_삭제_실패_하위_카테고리_존재() {
        Category category = new Category(null, "test");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(bookCategoryRepository.existsByCategory(category)).thenReturn(false);
        when(categoryRepository.existsByParentCategory(category)).thenReturn(true);

        assertThrows(CategoryHasChildException.class, () -> categoryService.deleteCategory(1L));
    }

}
