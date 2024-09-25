package org.tbank.service;

import org.springframework.stereotype.Service;
import org.tbank.dao.DAO;
import org.tbank.models.Category;

import java.util.Collection;
import java.util.Optional;

@Service
public class CategoryService {

    public final DAO<Integer, Category> concurrentHashMap;

    public CategoryService(DAO<Integer, Category> concurrentHashMap) {
        this.concurrentHashMap = concurrentHashMap;
    }

    public Collection<Category> getAllCategories() {
        return concurrentHashMap.getAll();
    }

    public Category getCategory(int id) {
        Optional<Category> category = Optional.ofNullable(concurrentHashMap.get(id));
        return category.orElseThrow(() -> new IllegalArgumentException(String.valueOf(id)));

    }

    public void addCategory(int id, Category category) {
        Optional<Category> existingCategory = Optional.ofNullable(concurrentHashMap.get(id));
        existingCategory.ifPresent(n -> {
            throw new IllegalArgumentException("Location с таким slug уже существует: " + id);
        });
        concurrentHashMap.put(id, category);
    }

    public void updateCategory(int id, Category category) {
        concurrentHashMap.update(id, category);
    }
    public void deleteCategory(int id) {
        Optional<Category> category = Optional.ofNullable(concurrentHashMap.get(id));
        category.orElseThrow(() -> new IllegalArgumentException(String.valueOf(id)));
        concurrentHashMap.remove(id);
    }
}
