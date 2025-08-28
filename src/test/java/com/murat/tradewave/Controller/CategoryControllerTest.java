package com.murat.tradewave.Controller;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.murat.tradewave.controller.CategoryController;
import com.murat.tradewave.dto.category.request.CategoryRequest;
import com.murat.tradewave.dto.category.response.CategoryResponse;
import com.murat.tradewave.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CategoryController.class)
@AutoConfigureMockMvc(addFilters = false)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CategoryService categoryService;

    @Test
    void addCategory_shouldReturnOk() throws Exception {
        CategoryRequest request = CategoryRequest.builder()
                .name("Electronics")
                .build();
        CategoryResponse response = CategoryResponse.builder()
                .categoryId(1L)
                .categoryName("Electronics")
                .build();
        when(categoryService.createCategory(any(CategoryRequest.class))).thenReturn(response);

        mockMvc.perform(post("/category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categoryName").value("Electronics"));

        verify(categoryService).createCategory(any(CategoryRequest.class));
    }

    @Test
    void getAllCategories_shouldReturnOk() throws Exception {
        CategoryResponse response = CategoryResponse.builder()
                .categoryId(1L)
                .categoryName("Electronics")
                .build();
        when(categoryService.getAllCategories()).thenReturn(List.of(response));

        mockMvc.perform(get("/category"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].categoryName").value("Electronics"));

        verify(categoryService).getAllCategories();
    }
}
