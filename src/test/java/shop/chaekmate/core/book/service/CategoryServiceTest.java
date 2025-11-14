package shop.chaekmate.core.book.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
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
import shop.chaekmate.core.book.dto.request.UpdateCategoryRequest;
import shop.chaekmate.core.book.dto.response.ReadAllCategoriesResponse;
import shop.chaekmate.core.book.dto.response.ReadCategoryResponse;
import shop.chaekmate.core.book.entity.Category;
import shop.chaekmate.core.book.exception.CategoryHasBookException;
import shop.chaekmate.core.book.exception.CategoryHasChildException;
import shop.chaekmate.core.book.exception.category.CategoryNotFoundException;
import shop.chaekmate.core.book.exception.ParentCategoryNotFoundException;
import shop.chaekmate.core.book.repository.BookCategoryRepository;
import shop.chaekmate.core.book.repository.CategoryRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import shop.chaekmate.core.book.dto.response.CategoryHierarchyResponse;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class CategoryServiceTest {

    @InjectMocks
    private CategoryService categoryService;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private BookCategoryRepository bookCategoryRepository;

    @Test
    void 페이지네이션으로_카테고리_조회_성공() {

        // mock 객체 사용
        Category parentCategory = mock(Category.class);
        Category childCategory = mock(Category.class);
        Category grandChildCategory = mock(Category.class);

        when(parentCategory.getId()).thenReturn(1L);
        when(parentCategory.getName()).thenReturn("부모");
        when(parentCategory.getParentCategory()).thenReturn(null);

        when(childCategory.getId()).thenReturn(2L);
        when(childCategory.getName()).thenReturn("자식");
        when(childCategory.getParentCategory()).thenReturn(parentCategory);

        when(grandChildCategory.getId()).thenReturn(3L);
        when(grandChildCategory.getName()).thenReturn("아기");
        when(grandChildCategory.getParentCategory()).thenReturn(childCategory);

        List<Category> allCategories = List.of(parentCategory, childCategory, grandChildCategory);
        Pageable pageable = PageRequest.of(0, 2);
        Page<Category> categoryPage = new PageImpl<>(List.of(parentCategory, childCategory), pageable,
                allCategories.size());

        when(categoryRepository.findAll()).thenReturn(allCategories);
        when(categoryRepository.findAll(pageable)).thenReturn(categoryPage);

        Page<CategoryHierarchyResponse> responsePage = categoryService.readAllCategoriesByPage(pageable);

        assertAll(
                () -> assertNotNull(responsePage),
                () -> assertThat(responsePage.getTotalElements()).isEqualTo(allCategories.size()),
                () -> assertThat(responsePage.getContent()).hasSize(2),
                () -> assertThat(responsePage.getContent().getFirst().hierarchy()).isEqualTo("부모"),
                () -> assertThat(responsePage.getContent().get(1).hierarchy()).isEqualTo("부모 > 자식"));
    }

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
        assertThat(responses.getFirst().getName()).isEqualTo("Test Category");
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
    void 카테고리_삭제_실패_존재하지_않는_부모_카테고리() {
        CreateCategoryRequest request = new CreateCategoryRequest(999L, "Child");

        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ParentCategoryNotFoundException.class, () -> categoryService.createCategory(request));

        verify(categoryRepository, never()).delete(any(Category.class));
    }


    @Test
    void 카테고리_삭제_실패_존재하지_않는_카테고리() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        CategoryNotFoundException exception = assertThrows(CategoryNotFoundException.class, () -> categoryService.deleteCategory(1L));
        assertThat(exception.getMessage()).isEqualTo("해당 카테고리를 찾을 수 없습니다.");

        verify(categoryRepository, never()).delete(any(Category.class));
    }

    @Test
    void 카테고리_삭제_실패_카테고리에_책이_존재() {
        Category category = new Category(null, "test");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(bookCategoryRepository.existsByCategory(category)).thenReturn(true);

        assertThrows(CategoryHasBookException.class, () -> categoryService.deleteCategory(1L));

        verify(categoryRepository, never()).delete(any(Category.class));
    }

    @Test
    void 카테고리_삭제_실패_하위_카테고리_존재() {
        Category category = new Category(null, "test");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(bookCategoryRepository.existsByCategory(category)).thenReturn(false);
        when(categoryRepository.existsByParentCategory(category)).thenReturn(true);

        assertThrows(CategoryHasChildException.class, () -> categoryService.deleteCategory(1L));

        verify(categoryRepository, never()).delete(any(Category.class));
    }

}
