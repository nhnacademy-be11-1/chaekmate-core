package shop.chaekmate.core.book.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.chaekmate.core.book.dto.request.CreateCategoryRequest;
import shop.chaekmate.core.book.dto.request.UpdateCategoryRequest;
import shop.chaekmate.core.book.dto.response.*;
import shop.chaekmate.core.book.entity.Category;
import shop.chaekmate.core.book.exception.CategoryHasBookException;
import shop.chaekmate.core.book.exception.CategoryHasChildException;
import shop.chaekmate.core.book.exception.ParentCategoryNotFoundException;
import shop.chaekmate.core.book.exception.category.CategoryNotFoundException;
import shop.chaekmate.core.book.repository.BookCategoryRepository;
import shop.chaekmate.core.book.repository.CategoryRepository;

import java.util.*;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final BookCategoryRepository bookCategoryRepository;

    @CacheEvict(value = "categories", allEntries = true)
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

    @Transactional(readOnly = true)
    public ReadCategoryResponse readCategory(Long targetCategoryId) {

        Category targetCategory = categoryRepository.findById(targetCategoryId)
                .orElseThrow(CategoryNotFoundException::new);

        Long parentCategoryId = null;
        if (targetCategory.getParentCategory() != null) {
            parentCategoryId = targetCategory.getParentCategory().getId();
        }

        return new ReadCategoryResponse(targetCategory.getId(), parentCategoryId, targetCategory.getName());
    }

    @CacheEvict(value = "categories", allEntries = true)
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

    @CacheEvict(value = "categories", allEntries = true)
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

        categoryRepository.delete(targetCategory); // 실제론 deleted_at 이 바뀜
    }

    @Cacheable("categories")
    @Transactional(readOnly = true)
    public List<ReadAllCategoriesResponse> readAllCategories() {
        List<Category> allCategories = categoryRepository.findAll();
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

    @Transactional(readOnly = true)
    public Page<CategoryHierarchyResponse> readAllCategoriesByPage(Pageable pageable) {
        Page<Category> categoriesPage = categoryRepository.findAll(pageable);
        List<Category> allCategories = categoryRepository.findAll();
        Map<Long, String> categoryHierarchyMap = new HashMap<>();

        for (Category category : allCategories) {
            categoryHierarchyMap.put(category.getId(), buildCategoryHierarchy(category, allCategories));
        }

        List<CategoryHierarchyResponse> responses = categoriesPage.getContent().stream()
                .map(category -> new CategoryHierarchyResponse(category.getId(),
                        categoryHierarchyMap.get(category.getId())))
                .toList();

        return new PageImpl<>(responses, pageable, categoriesPage.getTotalElements());
    }

    @Transactional(readOnly = true)
    public List<List<CategoryPathResponse>> getCategoriesWithParents(List<Long> categoryIds) {
        List<Category> categories = categoryRepository.findAllById(categoryIds);

        if (categories.size() != categoryIds.size()) {
            throw new CategoryNotFoundException();
        }

        Set<Long> allCategoryIds = new HashSet<>(categoryIds);
        for (Category category : categories) {
            collectParentIds(category, allCategoryIds);
        }

        List<Category> allCategories = categoryRepository.findAllById(allCategoryIds);

        Map<Long, Category> categoryMap = new HashMap<>();
        for (Category category : allCategories) {
            categoryMap.put(category.getId(), category);
        }

        List<List<CategoryPathResponse>> result = new ArrayList<>();
        for (Long categoryId : categoryIds) {
            List<CategoryPathResponse> path = tracePathList(categoryId, categoryMap);

            result.add(path);
        }

        return result;
    }

    private List<CategoryPathResponse> tracePathList(Long categoryId, Map<Long, Category> categoryMap) {
        List<CategoryPathResponse> path = new ArrayList<>();
        Category current = categoryMap.get(categoryId);

        if (current == null) {
            throw new CategoryNotFoundException();
        }

        while (current != null) {
            int depth = calculateDepth(current);

            path.add(new CategoryPathResponse(current.getId(), current.getName(), depth));

            current = current.getParentCategory();
        }

        return path;
    }

    private int calculateDepth(Category category) {
        int depth = 0;
        Category current = category;

        while (current.getParentCategory() != null) {
            depth++;

            current = current.getParentCategory();
        }

        return depth;
    }

    private void collectParentIds(Category category, Set<Long> allCategoryIds) {
        Category currentCategory = category.getParentCategory();

        while (currentCategory != null) {
            allCategoryIds.add(currentCategory.getId());
            currentCategory = currentCategory.getParentCategory();
        }
    }

    private String buildCategoryHierarchy(Category category, List<Category> allCategories) {
        StringBuilder hierarchy = new StringBuilder(category.getName());
        Category current = category;
        while (Objects.requireNonNull(current).getParentCategory() != null) {
            Long parentId = current.getParentCategory().getId();
            current = allCategories.stream().filter(c -> c.getId().equals(parentId)).findFirst().orElse(null);
            if (current != null) {
                hierarchy.insert(0, current.getName() + " > ");
            }
        }
        return hierarchy.toString();
    }
}
