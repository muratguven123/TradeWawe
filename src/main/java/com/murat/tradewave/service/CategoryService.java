package com.murat.tradewave.service;

import com.murat.tradewave.dto.category.request.CategoryRequest;
import com.murat.tradewave.dto.category.response.CategoryResponse;
import com.murat.tradewave.model.Category;
import com.murat.tradewave.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryResponse getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        return CategoryResponse.builder()
                .categoryId(category.getId())
                .categoryName(category.getName())
                .build();
    }

    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(category -> CategoryResponse.builder()
                        .categoryId(category.getId())
                        .categoryName(category.getName())
                        .build())
                .collect(Collectors.toList());
    }

    public CategoryResponse createCategory(CategoryRequest categoryRequest) {
        Category category = Category.builder()
                .name(categoryRequest.getName())
                .build();
        Category savedCategory = categoryRepository.save(category);

        return CategoryResponse.builder()
                .categoryId(savedCategory.getId())
                .categoryName(savedCategory.getName())
                .build();
    }

    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));

        category.setName(request.getName());

        Category updated = categoryRepository.save(category);

        return CategoryResponse.builder()
                .categoryId(updated.getId())
                .categoryName(updated.getName())
                .build();

    }
}
