package com.murat.tradewave.service;

import com.murat.tradewave.dto.category.request.CategoryRequest;
import com.murat.tradewave.dto.category.response.CategoryResponse;
import com.murat.tradewave.model.Category;
import com.murat.tradewave.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import com.murat.tradewave.helper.Mapper;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final Mapper mapper;

    public List<CategoryRequest> getCategoryById(@PathVariable Long id) {
        return (List<CategoryRequest>) categoryRepository.findById(id).orElseThrow(() -> new RuntimeException("Category not found"));
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

    public CategoryResponse updateCategory(CategoryRequest request) {
        Category category = categoryRepository.findById(request.getId())
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + request.getId()));

        category.setName(request.getName());

        Category updated = categoryRepository.save(category);

        return CategoryResponse.builder()
                .categoryId(updated.getId())
                .categoryName(updated.getName())
                .build();

    }
}
