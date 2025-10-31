package shop.chaekmate.core.book.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import shop.chaekmate.core.book.dto.request.CreateCategoryRequest;
import shop.chaekmate.core.book.dto.response.ReadAllCategoriesResponse;
import shop.chaekmate.core.book.dto.response.ReadCategoryResponse;
import shop.chaekmate.core.book.dto.request.UpdateCategoryRequest;
import shop.chaekmate.core.book.entity.Category;
import shop.chaekmate.core.book.repository.CategoryRepository;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    void 최상위_카테고리_생성_성공() {
        // given
        CreateCategoryRequest request = new CreateCategoryRequest(null, "New Category");
        Category category = new Category(null, "New Category");

        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        // when
        var response = categoryService.createCategory(request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.name()).isEqualTo("New Category");
    }

    @Test
    void 하위_카테고리_생성_성공() {
        // given
        Long parentId = 1L;
        Category parentCategory = new Category(null, "Parent");
        CreateCategoryRequest request = new CreateCategoryRequest(parentId, "Child");
        Category category = new Category(parentCategory, "Child");

        when(categoryRepository.findById(parentId)).thenReturn(Optional.of(parentCategory));
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        // when
        var response = categoryService.createCategory(request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.name()).isEqualTo("Child");
    }

    @Test
    void ID로_카테고리_조회_성공() {
        // given
        Long categoryId = 1L;
        Category category = new Category(null, "Test Category");

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        // when
        ReadCategoryResponse response = categoryService.readCategory(categoryId);

        // then
        assertThat(response).isNotNull();
        assertThat(response.name()).isEqualTo("Test Category");
    }

    @Test
    void 모든_카테고리_조회_성공() {
        // given
        Category category = new Category(null, "Test Category");
        when(categoryRepository.findAll()).thenReturn(Collections.singletonList(category));

        // when
        List<ReadAllCategoriesResponse> responses = categoryService.readAllCategories();

        // then
        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getName()).isEqualTo("Test Category");
    }

    @Test
    void 카테고리_수정_성공() {
        // given
        Long categoryId = 1L;
        UpdateCategoryRequest request = new UpdateCategoryRequest(null, "Updated Category");
        Category category = new Category(null, "Old Category");

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        // when
        var response = categoryService.updateCategory(categoryId, request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.name()).isEqualTo("Updated Category");
    }

    @Test
    void ID로_카테고리_삭제_성공() {
        // given
        Long categoryId = 1L;
        Category category = new Category(null, "Test Category");

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        doNothing().when(categoryRepository).delete(category);

        // when
        categoryService.deleteCategory(categoryId);

        // then
        verify(categoryRepository, times(1)).delete(category);
    }

    @Test
    void 존재하지_않는_부모ID로_카테고리_생성_실패() {
        // given
        CreateCategoryRequest request = new CreateCategoryRequest(999L, "Child");
        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        // when & then
        assertThrows(RuntimeException.class, () -> categoryService.createCategory(request));
    }
}
