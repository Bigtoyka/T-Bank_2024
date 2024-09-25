package org.tbank.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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

    @TimeExecution
    @GetMapping()
    public ResponseEntity<Collection<Category>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @TimeExecution
    @GetMapping("/{id}")
    public Category getCategory(@PathVariable("id") int id) {
        try {
            return categoryService.getCategory(id);
        } catch (Exception e) {
            log.error("Ошибка при нахождении категории: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @TimeExecution
    @PostMapping()
    public ResponseEntity<String> addCategory(@RequestBody Category category) {
        log.info("Добавить категорию: {}", category.toString());
        try {
            categoryService.addCategory(category.getId(), category);
            return ResponseEntity.ok("Категория добавлена");
        } catch (IllegalArgumentException e) {
            log.error("Ошибка при добавлении категории: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Ошибка: " + e.getMessage());
        } catch (Exception e) {
            log.error("Неожиданная ошибка: ", e);
            return ResponseEntity.badRequest().body("Неожиданная ошибка: " + e.getMessage());
        }
    }

    @TimeExecution
    @PutMapping("/{id}")
    public ResponseEntity<String> updateCategory(@PathVariable("id") int id, @RequestBody Category category) {
        log.info("Обновлуние категории: {}", category.toString());
        try {
            categoryService.updateCategory(id, category);
            return ResponseEntity.ok("Обновление прошло успешно");
        } catch (IllegalArgumentException e) {
            log.error("Ошибка при обновлении категории: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Ошибка: " + e.getMessage());
        } catch (Exception e) {
            log.error("Неожиданная ошибка: ", e);
            return ResponseEntity.badRequest().body("Неожиданная ошибка" + e.getMessage());
        }
    }

    @TimeExecution
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable("id") int id) {
        log.info("Удаление категории: {}", id);
        try {
            categoryService.deleteCategory(id);
            return ResponseEntity.ok("Категория удалена");
        } catch (IllegalArgumentException e) {
            log.error("Ошибка при удалении категории: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Ошибка: " + e.getMessage());
        } catch (Exception e) {
            log.error("Неожиданная ошибка: ", e);
            return ResponseEntity.badRequest().body("Ошибка: " + e.getMessage());
        }
    }
}
