package shop.chaekmate.core.book.service;

import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import shop.chaekmate.core.book.dto.CreateCategoryRequest;
import shop.chaekmate.core.book.dto.CreateCategoryResponse;
import shop.chaekmate.core.book.dto.ReadAllCategoriesResponse;
import shop.chaekmate.core.book.dto.ReadCategoryResponse;
import shop.chaekmate.core.book.dto.UpdateCategoryRequest;
import shop.chaekmate.core.book.dto.UpdateCategoryResponse;
import shop.chaekmate.core.book.entity.Category;
import shop.chaekmate.core.book.repository.CategoryRepository;

@Service
@RequiredArgsConstructor
public class CategoryService {
    
    private final CategoryRepository categoryRepository;

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
    public ReadCategoryResponse readCategory(Long targetCategoryId) {

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
    public UpdateCategoryResponse updateCategory(Long targetId, UpdateCategoryRequest request) {
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

        categoryRepository.save(targetCategory);

        Long responseParentId = (targetCategory.getParentCategory() != null) ? targetCategory.getParentCategory().getId() : null;

        return new UpdateCategoryResponse(targetCategory.getId(), responseParentId,
                targetCategory.getName());
    }

    @Transactional
    public void deleteCategory(Long targetCategoryId) {
        if (categoryRepository.findById(targetCategoryId).isEmpty()) {
            throw new RuntimeException("해당하는 Id의 카테고리를 찾을 수 없습니다.");
        }
        Category targetCategory = categoryRepository.findById(targetCategoryId).get();
        categoryRepository.delete(targetCategory); // 실제론 deleted_at 이 바뀜
    }

    @Transactional
    public List<ReadAllCategoriesResponse> readAllCategories() {
        List<Category> allCategories = categoryRepository.findAll(); // 또는 부모 없는 것만
        Map<Long, ReadAllCategoriesResponse> dtoMap = new HashMap<>();

        // 모든 카테고리를 DTO로 변환
        for (Category c : allCategories) {
            dtoMap.put(c.getId(), new ReadAllCategoriesResponse(c.getId(), c.getName()));
        }

        // 부모-자식 연결
        List<ReadAllCategoriesResponse> roots = new ArrayList<>();
        for (Category c : allCategories) {
            ReadAllCategoriesResponse dto = dtoMap.get(c.getId());
            if (c.getParentCategory() != null) {
                dtoMap.get(c.getParentCategory().getId()).addChild(dto);
            } else {
                roots.add(dto);
            }
        }

        return roots;
    }
}
