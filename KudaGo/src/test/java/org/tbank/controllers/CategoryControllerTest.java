package org.tbank.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.tbank.models.Category;
import org.tbank.service.CategoryService;
import org.tbank.util.JwtAuthenticationFilter;
import org.tbank.util.JwtService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@SpringBootTest
@AutoConfigureMockMvc
@EnableAutoConfiguration(exclude = SecurityAutoConfiguration.class)
@WithMockUser
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService categoryService;

    private Category category1;

    @BeforeEach
    void setUp() {
        category1 = new Category(1, "slug1", "Category1");
    }

    @Test
    void getCategory_SuccessGetCategory_shouldReturnCategoryById() throws Exception {
        Mockito.when(categoryService.getCategory(anyInt())).thenReturn(category1);

        mockMvc.perform(get("/api/v1/places/categories/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(category1.getId()))
                .andExpect(jsonPath("$.slug").value(category1.getSlug()))
                .andExpect(jsonPath("$.name").value(category1.getName()));
    }

    @Test
    void addCategory_SuccessAddCategory_shouldAddCategory() throws Exception {
        Mockito.doNothing().when(categoryService).addCategory(anyInt(), any(Category.class));
        mockMvc.perform(post("/api/v1/places/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(category1)))
                .andExpect(status().isOk()).andExpect(content().string("Категория добавлена"));
    }

    @Test
    void updateCategory_SuccessUpdateCategory_shouldUpdateCategory() throws Exception {
        Mockito.doNothing().when(categoryService).updateCategory(anyInt(), any(Category.class));
        mockMvc.perform(put("/api/v1/places/categories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(category1)))
                .andExpect(status().isOk())
                .andExpect(content().string("Обновление прошло успешно"));
    }

    @Test
    void deleteCategory_SuccessDeleteCategory_shouldDeleteCategory() throws Exception {
        Mockito.doNothing().when(categoryService).deleteCategory(anyInt());
        mockMvc.perform(delete("/api/v1/places/categories/1"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteCategory_IdNotFound_shouldThrowException() throws Exception {
        Mockito.doThrow(new IllegalArgumentException()).when(categoryService).deleteCategory(1);

        mockMvc.perform(delete("/api/v1/places/categories/1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getCategory_IdNotFound_shouldThrowException() throws Exception {
        Mockito.doThrow(new IllegalArgumentException()).when(categoryService).getCategory(anyInt());

        mockMvc.perform(get("/api/v1/places/categories/1"))
                .andExpect(status().isBadRequest());
    }
}