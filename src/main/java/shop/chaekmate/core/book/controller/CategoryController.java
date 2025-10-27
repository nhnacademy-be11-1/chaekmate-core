package shop.chaekmate.core.book.controller;

import jakarta.validation.Valid;
import java.util.List;
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
import shop.chaekmate.core.book.dto.CreateCategoryRequest;
import shop.chaekmate.core.book.dto.CreateCategoryResponse;
import shop.chaekmate.core.book.dto.ReadAllCategoriesResponse;
import shop.chaekmate.core.book.dto.ReadCategoryResponse;
import shop.chaekmate.core.book.dto.UpdateCategoryRequest;
import shop.chaekmate.core.book.dto.UpdateCategoryResponse;
import shop.chaekmate.core.book.service.CategoryService;

@RestController
public class CategoryController implements CategoryControllerDocs {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping("/admin/categories")
    public ResponseEntity<CreateCategoryResponse> createCategory(
            @Valid @RequestBody CreateCategoryRequest createCategoryRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.createCategory(createCategoryRequest));
    }

    @GetMapping("/categories")
    public ResponseEntity<List<ReadAllCategoriesResponse>> readAllCategories() {
        return ResponseEntity.ok(categoryService.readAllCategories());
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
