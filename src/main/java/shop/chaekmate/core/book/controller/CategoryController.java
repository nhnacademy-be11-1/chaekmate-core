package shop.chaekmate.core.book.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import shop.chaekmate.core.book.dto.CreateCategoryRequest;
import shop.chaekmate.core.book.dto.CreateCategoryResponse;
import shop.chaekmate.core.book.dto.DeleteCategoryRequest;
import shop.chaekmate.core.book.dto.DeleteCategoryResponse;
import shop.chaekmate.core.book.dto.ReadCategoryRequest;
import shop.chaekmate.core.book.dto.ReadCategoryResponse;
import shop.chaekmate.core.book.dto.UpdateCategoryRequest;
import shop.chaekmate.core.book.dto.UpdateCategoryResponse;
import shop.chaekmate.core.book.service.CategoryService;

@RestController
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping("/category")
    public ResponseEntity<CreateCategoryResponse> createCategory(
            @Valid @RequestBody CreateCategoryRequest createCategoryRequest) {
        CreateCategoryResponse createCategoryResponse = categoryService.createCategory(createCategoryRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(createCategoryResponse);
    }

    @GetMapping("/category")
    public ResponseEntity<ReadCategoryResponse> readCategory(
            @Valid @RequestBody ReadCategoryRequest readCategoryRequest) {
        ReadCategoryResponse readCategoryResponse = categoryService.readCategory(readCategoryRequest);

        return ResponseEntity.status(HttpStatus.OK).body(readCategoryResponse);
    }

    @PutMapping("/category")
    public ResponseEntity<UpdateCategoryResponse> updateCategory(
            @Valid @RequestBody UpdateCategoryRequest updateCategoryRequest) {
        UpdateCategoryResponse updateCategoryResponse = categoryService.updateCategory(updateCategoryRequest);

        return ResponseEntity.status(HttpStatus.OK).body(updateCategoryResponse);
    }

    @DeleteMapping("/category")
    public ResponseEntity<DeleteCategoryResponse> deleteCategory(
            @Valid @RequestBody DeleteCategoryRequest deleteCategoryRequest) {
        DeleteCategoryResponse deleteCategoryResponse = categoryService.deleteCategory(deleteCategoryRequest);

        return ResponseEntity.status(HttpStatus.OK).body(deleteCategoryResponse);
    }
}
