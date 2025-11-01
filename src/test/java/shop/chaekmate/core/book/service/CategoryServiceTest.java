package shop.chaekmate.core.book.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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

    @Nested
    @DisplayName("카테고리 생성")
    class CreateCategory {

        @Test
        @DisplayName("실패 - 존재하지 않는 부모 카테고리")
        void createCategory_fail_parent_not_found() {
            CreateCategoryRequest request = new CreateCategoryRequest(999L, "Child");

            when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(ParentCategoryNotFoundException.class, () -> categoryService.createCategory(request));
        }
    }

    @Nested
    @DisplayName("카테고리 삭제")
    class DeleteCategory {

        @Test
        @DisplayName("실패 - 존재하지 않는 카테고리")
        void deleteCategory_fail_not_found() {
            when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

            assertThrows(CategoryNotFoundException.class, () -> categoryService.deleteCategory(1L));
        }

        @Test
        @DisplayName("실패 - 카테고리에 책이 존재")
        void deleteCategory_fail_book_exists() {
            Category category = new Category(null, "test");

            when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
            when(bookCategoryRepository.existsByCategory(category)).thenReturn(true);

            assertThrows(CategoryHasBookException.class, () -> categoryService.deleteCategory(1L));
        }

        @Test
        @DisplayName("실패 - 하위 카테고리 존재")
        void deleteCategory_fail_child_exists() {
            Category category = new Category(null, "test");

            when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
            when(bookCategoryRepository.existsByCategory(category)).thenReturn(false);
            when(categoryRepository.existsByParentCategory(category)).thenReturn(true);

            assertThrows(CategoryHasChildException.class, () -> categoryService.deleteCategory(1L));
        }
    }
}
