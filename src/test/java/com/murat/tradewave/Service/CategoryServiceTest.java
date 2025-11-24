package com.murat.tradewave.Service;
import com.murat.tradewave.dto.category.request.CategoryRequest;
import com.murat.tradewave.dto.category.response.CategoryResponse;
import com.murat.tradewave.helper.Mapper;
import com.murat.tradewave.model.Category;
import com.murat.tradewave.repository.CategoryRepository;
import com.murat.tradewave.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private Mapper mapper;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    void createCategory_saves_and_returns_response() {
        CategoryRequest request = new CategoryRequest();
        request.setName("Electronics");

        Category saved = Category.builder().id(1L).name("Electronics").build();
        when(categoryRepository.save(any(Category.class))).thenReturn(saved);

        CategoryResponse response = categoryService.createCategory(request);

        assertThat(response.getCategoryId()).isEqualTo(1L);
        assertThat(response.getCategoryName()).isEqualTo("Electronics");
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void getAllCategories_maps_entities_to_responses() {
        List<Category> categories = Arrays.asList(
                Category.builder().id(1L).name("A").build(),
                Category.builder().id(2L).name("B").build()
        );
        when(categoryRepository.findAll()).thenReturn(categories);

        List<CategoryResponse> responses = categoryService.getAllCategories();

        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).getCategoryName()).isEqualTo("A");
        verify(categoryRepository).findAll();
    }

    @Test
    void updateCategory_updates_existing_entity() {
        CategoryRequest request = new CategoryRequest();
        request.setId(1L);
        request.setName("Updated");

        Category existing = Category.builder().id(1L).name("Old").build();
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(categoryRepository.save(existing)).thenReturn(Category.builder().id(1L).name("Updated").build());

        CategoryResponse response = categoryService.updateCategory(request);

        assertThat(response.getCategoryName()).isEqualTo("Updated");
        verify(categoryRepository).findById(1L);
        verify(categoryRepository).save(existing);
    }
}
