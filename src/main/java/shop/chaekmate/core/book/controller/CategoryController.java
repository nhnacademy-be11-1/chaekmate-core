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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import shop.chaekmate.core.book.dto.CreateCategoryRequest;
import shop.chaekmate.core.book.dto.CreateCategoryResponse;
import shop.chaekmate.core.book.dto.ReadAllCategoriesResponse;
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

    @PostMapping("/admin/categories")
    @ResponseStatus(HttpStatus.CREATED)
    public CreateCategoryResponse createCategory(
            @Valid @RequestBody CreateCategoryRequest createCategoryRequest) {

        return categoryService.createCategory(createCategoryRequest);
    }

    @GetMapping("/categories")
    @ResponseStatus(HttpStatus.OK)
    public List<ReadAllCategoriesResponse> readAllCategories() {
        return categoryService.readAllCategories();
    }

    @GetMapping("/categories/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ReadCategoryResponse readCategory(
            @PathVariable(name = "id") Long categoryId) {

        return categoryService.readCategory(categoryId);
    }

    @PutMapping("/admin/categories/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UpdateCategoryResponse updateCategory(
            @PathVariable(name = "id") Long categoryId,
            @Valid @RequestBody UpdateCategoryRequest updateCategoryRequest) {

        return categoryService.updateCategory(categoryId, updateCategoryRequest);
    }

    @DeleteMapping("/categories/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(
            @PathVariable(name = "id") Long categoryId) {
        categoryService.deleteCategory(categoryId);
    }
}
