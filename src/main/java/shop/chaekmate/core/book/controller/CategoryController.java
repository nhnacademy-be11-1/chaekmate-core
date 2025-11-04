package shop.chaekmate.core.book.controller;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import shop.chaekmate.core.book.controller.docs.CategoryControllerDocs;
import shop.chaekmate.core.book.dto.request.CreateCategoryRequest;
import shop.chaekmate.core.book.dto.response.CategoryHierarchyResponse;
import shop.chaekmate.core.book.dto.response.CreateCategoryResponse;
import shop.chaekmate.core.book.dto.response.PageResponse;
import shop.chaekmate.core.book.dto.response.ReadAllCategoriesResponse;
import shop.chaekmate.core.book.dto.response.ReadCategoryResponse;
import shop.chaekmate.core.book.dto.request.UpdateCategoryRequest;
import shop.chaekmate.core.book.dto.response.UpdateCategoryResponse;
import shop.chaekmate.core.book.service.CategoryService;

import lombok.RequiredArgsConstructor;

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

    @GetMapping(value = "/categories", params = {"page", "size"})
    public ResponseEntity<PageResponse<CategoryHierarchyResponse>> readAllCategoriesByPage(Pageable pageable) {
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
}
