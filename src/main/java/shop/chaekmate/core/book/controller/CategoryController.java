package shop.chaekmate.core.book.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shop.chaekmate.core.book.controller.docs.CategoryControllerDocs;
import shop.chaekmate.core.book.dto.request.CreateCategoryRequest;
import shop.chaekmate.core.book.dto.request.UpdateCategoryRequest;
import shop.chaekmate.core.book.dto.response.*;
import shop.chaekmate.core.book.service.CategoryService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CategoryController implements CategoryControllerDocs {

    private final CategoryService categoryService;

    @PostMapping("/admin/categories")
    public ResponseEntity<CreateCategoryResponse> createCategory(
            @Valid @RequestBody CreateCategoryRequest createCategoryRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.createCategory(createCategoryRequest));
    }

    @GetMapping("/categories")
    public ResponseEntity<List<ReadAllCategoriesResponse>> readAllCategories() {
        return ResponseEntity.ok(categoryService.readAllCategories());
    }

    @GetMapping("/categories/paged")
    public ResponseEntity<PageResponse<CategoryHierarchyResponse>> readAllCategoriesByPage(@PageableDefault Pageable pageable) {
        return ResponseEntity.ok(PageResponse.from(categoryService.readAllCategoriesByPage(pageable)));
    }

    @GetMapping("/categories/{id}")
    public ResponseEntity<ReadCategoryResponse> readCategory(
            @PathVariable(name = "id") Long categoryId) {
        return ResponseEntity.ok(categoryService.readCategory(categoryId));
    }

    @PutMapping("/admin/categories/{id}")
    public ResponseEntity<UpdateCategoryResponse> updateCategory(
            @PathVariable(name = "id") Long categoryId,
            @Valid @RequestBody UpdateCategoryRequest updateCategoryRequest) {
        return ResponseEntity.ok(categoryService.updateCategory(categoryId, updateCategoryRequest));
    }

    @DeleteMapping("/admin/categories/{id}")
    public ResponseEntity<Void> deleteCategory(
            @PathVariable(name = "id") Long categoryId) {
        categoryService.deleteCategory(categoryId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/categories/bulk")
    public ResponseEntity<List<List<CategoryPathResponse>>> getCategoriesWithParents(
            @RequestParam List<Long> categoryIds) {
        List<List<CategoryPathResponse>> result = categoryService.getCategoriesWithParents(categoryIds);

        return ResponseEntity.ok(result);
    }
}
