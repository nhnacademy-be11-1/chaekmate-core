package shop.chaekmate.core.book.service;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import shop.chaekmate.core.book.dto.CreateCategoryRequest;
import shop.chaekmate.core.book.dto.CreateCategoryResponse;
import shop.chaekmate.core.book.dto.DeleteCategoryRequest;
import shop.chaekmate.core.book.dto.DeleteCategoryResponse;
import shop.chaekmate.core.book.dto.ReadCategoryRequest;
import shop.chaekmate.core.book.dto.ReadCategoryResponse;
import shop.chaekmate.core.book.dto.UpdateCategoryRequest;
import shop.chaekmate.core.book.dto.UpdateCategoryResponse;
import shop.chaekmate.core.book.entity.Category;
import shop.chaekmate.core.book.repository.CategoryRepository;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }


    @Transactional
    public CreateCategoryResponse createCategory(CreateCategoryRequest request) {

        if (request.parentCategoryId() != null
                && categoryRepository.findById(request.parentCategoryId()).isEmpty()) {
            throw new RuntimeException("해당하는 Id의 Parent Category를 찾을 수 없음");
        }

        if (request.name() == null) {
            throw new RuntimeException("Category Name 이 Null 입니다.");
        }

        Category parentCategory = null; // request.parentCategoryId 가 null 일때

        if (request.parentCategoryId() != null) {
            parentCategory = categoryRepository.findById(request.parentCategoryId()).get();
        }

        Category category = new Category(parentCategory, request.name());

        categoryRepository.save(category);

        // Make response
        Long parentCategoryId = null;
        if (parentCategory != null) {
            parentCategoryId = parentCategory.getId();
        }

        return new CreateCategoryResponse(category.getId(), parentCategoryId,
                category.getName());
    }

    @Transactional
    public ReadCategoryResponse readCategory(ReadCategoryRequest request) {

        Long targetCategoryId = request.id();
        if (categoryRepository.findById(targetCategoryId).isEmpty()) {
            throw new RuntimeException("해당하는 Id 의 Category를 찾을 수 없습니다.");
        }

        Category targetCategory = categoryRepository.findById(targetCategoryId).get();

        String parentCategoryName = "null";
        if (targetCategory.getParentCategory() != null) {
            parentCategoryName = targetCategory.getParentCategory().getName();
        }

        return new ReadCategoryResponse(targetCategory.getId(), parentCategoryName, targetCategory.getName());
    }

    @Transactional
    public UpdateCategoryResponse updateCategory(UpdateCategoryRequest request) {

        Long targetId = request.id();
        Long parentCategoryId = request.parentCategoryId();

        Category targetCategory = categoryRepository.findById(targetId)
                .orElseThrow(() -> new RuntimeException("해당 ID의 카테고리를 찾을 수 없습니다"));

        Category parentCategory = null;
        if (parentCategoryId != null) {
            parentCategory = categoryRepository.findById(parentCategoryId)
                    .orElseThrow(() -> new RuntimeException("해당 ID의 부모 카테고리를 찾을 수 없습니다"));
        }

        targetCategory.setName(request.name());
        targetCategory.setParentCategory(parentCategory);

        return new UpdateCategoryResponse(targetCategory.getId(), targetCategory.getParentCategory().getId(),
                targetCategory.getName());
    }

    @Transactional
    public DeleteCategoryResponse deleteCategory(DeleteCategoryRequest request) {
        Long targetCategoryId = request.id();
        if (categoryRepository.findById(targetCategoryId).isEmpty()) {
            throw new RuntimeException("해당하는 Id의 카테고리를 찾을 수 없습니다.");
        }
        Category targetCategory = categoryRepository.findById(targetCategoryId).get();
        categoryRepository.delete(targetCategory); // 실제론 deleted_at 이 바뀜

        return new DeleteCategoryResponse();
    }
}
