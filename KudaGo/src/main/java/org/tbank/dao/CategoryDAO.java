package org.tbank.dao;

import org.springframework.stereotype.Component;
import org.tbank.models.Category;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class CategoryDAO implements UniversalDAO<Integer, Category> {
    private final ConcurrentHashMap<Integer, Category> concurrentHashMap = new ConcurrentHashMap<>();

    @Override
    public void put(Integer key, Category value) {
        concurrentHashMap.put(key, value);
    }

    @Override
    public Category get(Integer key) {
        return concurrentHashMap.get(key);
    }

    @Override
    public void remove(Integer key) {
        concurrentHashMap.remove(key);
    }

    @Override
    public Collection<Category> getAll() {
        return concurrentHashMap.values();
    }

    @Override
    public void update(Integer key, Category value) {
        concurrentHashMap.replace(key, value);
    }
}
