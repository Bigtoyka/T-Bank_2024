package org.tbank.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.tbank.dao.UniversalDAO;
import org.tbank.models.Category;

import java.util.Collection;
import java.util.Optional;

@Slf4j
@Service
public class CategoryService {

    public final UniversalDAO<Integer, Category> concurrentHashMap;

    public CategoryService(UniversalDAO<Integer, Category> concurrentHashMap) {
        this.concurrentHashMap = concurrentHashMap;
    }

    public Collection<Category> getAllCategories() {
        log.info("Получение всех категорий");
        return concurrentHashMap.getAll();

    }

    public Category getCategory(int id) {
        log.info("Получение категории по id: {}", id);
        Optional<Category> category = Optional.ofNullable(concurrentHashMap.get(id));
        return category.orElseThrow(() -> new IllegalArgumentException(String.valueOf(id)));
    }

    public void addCategory(int id, Category category) {
        log.info("Добавление категории по id: {}", id);
        Optional<Category> existingCategory = Optional.ofNullable(concurrentHashMap.get(id));
        existingCategory.ifPresent(n -> {
            throw new IllegalArgumentException("Location с таким slug уже существует: " + id);
        });
        concurrentHashMap.put(id, category);
        log.info("Категория добавлена");
    }

    public void updateCategory(int id, Category category) {
        log.info("Обновление категории по id: {}", id);
        concurrentHashMap.update(id, category);
        log.info("Категория обновлена");

    }
    public void deleteCategory(int id) {
        log.info("Удаление категории по id: {}", id);
        Optional<Category> category = Optional.ofNullable(concurrentHashMap.get(id));
        category.orElseThrow(() -> new IllegalArgumentException(String.valueOf(id)));
        concurrentHashMap.remove(id);
        log.info("Категория удалена");
    }
}
