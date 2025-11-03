package shop.chaekmate.core.book.service;

import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import shop.chaekmate.core.book.dto.request.CreateCategoryRequest;
import shop.chaekmate.core.book.dto.response.CreateCategoryResponse;
import shop.chaekmate.core.book.dto.response.ReadAllCategoriesResponse;
import shop.chaekmate.core.book.dto.response.ReadCategoryResponse;
import shop.chaekmate.core.book.dto.request.UpdateCategoryRequest;
import shop.chaekmate.core.book.dto.response.UpdateCategoryResponse;
import shop.chaekmate.core.book.entity.Category;
import shop.chaekmate.core.book.exception.CategoryHasBookException;
import shop.chaekmate.core.book.exception.CategoryHasChildException;
import shop.chaekmate.core.book.exception.CategoryNotFoundException;
import shop.chaekmate.core.book.exception.ParentCategoryNotFoundException;
import shop.chaekmate.core.book.repository.BookCategoryRepository;
import shop.chaekmate.core.book.repository.CategoryRepository;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final BookCategoryRepository bookCategoryRepository;

    @Transactional
    public CreateCategoryResponse createCategory(CreateCategoryRequest request) {

        Category parentCategory = null; // request.parentCategoryId 가 null 일때

        if (request.parentCategoryId() != null) {
            parentCategory = categoryRepository.findById(request.parentCategoryId())
                    .orElseThrow(ParentCategoryNotFoundException::new);
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

        Category targetCategory = categoryRepository.findById(targetCategoryId)
                .orElseThrow(CategoryNotFoundException::new);

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
                .orElseThrow(CategoryNotFoundException::new);

        Category parentCategory = null;
        if (parentCategoryId != null) {
            parentCategory = categoryRepository.findById(parentCategoryId)
                    .orElseThrow(ParentCategoryNotFoundException::new);
        }

        targetCategory.updateCategory(parentCategory, request.name());

        categoryRepository.save(targetCategory);

        Long responseParentId =
                (targetCategory.getParentCategory() != null) ? targetCategory.getParentCategory().getId() : null;

        return new UpdateCategoryResponse(targetCategory.getId(), responseParentId,
                targetCategory.getName());
    }

    @Transactional
    public void deleteCategory(Long targetCategoryId) {
        Category targetCategory = categoryRepository.findById(targetCategoryId)
                .orElseThrow(CategoryNotFoundException::new);

        if (bookCategoryRepository.existsByCategory(targetCategory)) {
            throw new CategoryHasBookException();
        }

        if (categoryRepository.existsByParentCategory(targetCategory)) {
            throw new CategoryHasChildException();
        }

        // TODO: 해당 카테고리에 해당하는 쿠폰 정책이 있을때 삭제불가

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
