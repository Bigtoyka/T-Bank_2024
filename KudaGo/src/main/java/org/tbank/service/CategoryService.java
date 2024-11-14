package org.tbank.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.tbank.dao.UniversalDAO;
import org.tbank.models.Category;
import org.tbank.service.snapshot.CategorySnapshot;
import org.tbank.service.snapshot.SnapshotManager;

import java.util.Collection;
import java.util.Optional;

@Slf4j
@Service
public class CategoryService {
    private final SnapshotManager snapshotManager;
    public final UniversalDAO<Integer, Category> concurrentHashMap;

    public CategoryService(SnapshotManager snapshotManager, UniversalDAO<Integer, Category> concurrentHashMap) {
        this.snapshotManager = snapshotManager;
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
        Category currentCategory = getCategory(id);
        snapshotManager.saveCategorySnapshot(new CategorySnapshot(currentCategory.getId(), currentCategory.getSlug(), currentCategory.getName()));
        log.info("Обновление категории по id: {}", id);
        concurrentHashMap.update(id, category);
        log.info("Категория обновлена");

    }
    public void deleteCategory(int id) {
        Category currentCategory = getCategory(id);
        snapshotManager.saveCategorySnapshot(new CategorySnapshot(currentCategory.getId(), currentCategory.getSlug(), currentCategory.getName()));
        log.info("Удаление категории по id: {}", id);
        Optional<Category> category = Optional.ofNullable(concurrentHashMap.get(id));
        category.orElseThrow(() -> new IllegalArgumentException(String.valueOf(id)));
        concurrentHashMap.remove(id);
        log.info("Категория удалена");
    }

}
