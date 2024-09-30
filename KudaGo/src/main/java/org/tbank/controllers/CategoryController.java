package org.tbank.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.tbank.annotations.TimeExecution;
import org.tbank.models.Category;
import org.tbank.service.CategoryService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/api/v1/places/categories")
@TimeExecution
public class CategoryController {

    @Autowired
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }


    @GetMapping()
    public ResponseEntity<Collection<Category>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }


    @GetMapping("/{id}")
    public Category getCategory(@PathVariable("id") int id) {
        return categoryService.getCategory(id);
    }


    @PostMapping()
    public ResponseEntity<String> addCategory(@RequestBody Category category) {
        log.info("Добавить категорию: {}", category.toString());
        categoryService.addCategory(category.getId(), category);
        return ResponseEntity.ok("Категория добавлена");
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateCategory(@PathVariable("id") int id, @RequestBody Category category) {
        log.info("Обновлуние категории: {}", category.toString());
        categoryService.updateCategory(id, category);
        return ResponseEntity.ok("Обновление прошло успешно");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable("id") int id) {
        log.info("Удаление категории: {}", id);
        categoryService.deleteCategory(id);
        return ResponseEntity.ok("Категория удалена");
    }
}
