package org.tbank.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.tbank.dao.UniversalDAO;
import org.tbank.models.Category;
import org.tbank.service.snapshot.CategorySnapshot;
import org.tbank.service.snapshot.SnapshotManager;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

class CategoryServiceTest {

    @Mock
    private UniversalDAO<Integer, Category> categoryDao;

    @InjectMocks
    private CategoryService categoryService;

    @Mock
    private SnapshotManager snapshotManager;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllCategories_SuccessReturnAllCategories_shouldReturnAllCategories() {
        Category category1 = new Category(1, "slug1", "Category1");
        Category category2 = new Category(2, "slug2", "Category2");
        List<Category> categories = Arrays.asList(category1, category2);

        when(categoryDao.getAll()).thenReturn(categories);
        Collection<Category> result = categoryService.getAllCategories();

        assertEquals(categories, result);
    }

    @Test
    void getCategory_SuccessReturnCategory_shouldReturnCategory() {
        Category category = new Category(1, "slug1", "Category1");
        when(categoryDao.get(1)).thenReturn(category);

        Category result = categoryService.getCategory(1);

        assertEquals(category, result);
    }

    @Test
    public void getCategory_NotFound_shouldThrowException() {
        when(categoryDao.get(1)).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> categoryService.getCategory(1));
    }

    @Test
    void addCategory_SuccessAddCategory_shouldAddCategory() {
        Category category = new Category(2, "slug2", "Category2");

        categoryService.addCategory(2, category);

        verify(categoryDao, times(1)).put(2, category);

    }

    @Test
    public void addCategory_AlreadyExists_shouldThrowException() {
        Category category = new Category(2, "slug2", "Category2");
        when(categoryDao.get(2)).thenReturn(category);

        assertThrows(IllegalArgumentException.class, () -> categoryService.addCategory(2, category));
    }

    @Test
    void updateCategory_successUpdateCategory_shouldUpdateCategory() {
        Category category = new Category(2, "slug1", "Category1");
        when(categoryDao.get(2)).thenReturn(category);
        categoryService.updateCategory(category.getId(), category);

        verify(categoryDao).update(category.getId(), category);
        verify(snapshotManager, times(1)).saveCategorySnapshot(any(CategorySnapshot.class));

    }


    @Test
    void deleteCategory_SuccessDeleteCategory_shouldDeleteCategory() {
        Category category = new Category(2, "slug2", "Category2");

        when(categoryDao.get(2)).thenReturn(category);
        categoryService.deleteCategory(2);

        verify(categoryDao, times(1)).remove(2);
        verify(snapshotManager, times(1)).saveCategorySnapshot(any(CategorySnapshot.class));

    }

    @Test
    public void deleteCategory_NotFoundCategory_shouldThrowException() {
        when(categoryDao.get(2)).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> categoryService.deleteCategory(2));
    }
}