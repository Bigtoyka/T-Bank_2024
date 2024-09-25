package org.tbank.dao;

import org.springframework.stereotype.Component;
import org.tbank.models.Location;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class LocationDAO implements DAO<String, Location> {
    private ConcurrentHashMap<String, Location> concurrentHashMap = new ConcurrentHashMap<>();

    @Override
    public void put(String key, Location value) {
        concurrentHashMap.put(key, value);
    }

    @Override
    public Location get(String key) {
        return concurrentHashMap.get(key);
    }

    @Override
    public void remove(String key) {
        concurrentHashMap.remove(key);
    }

    @Override
    public Collection<Location> getAll() {
        return concurrentHashMap.values();
    }

    @Override
    public void update(String key, Location value) {
        concurrentHashMap.replace(key, value);
    }
}
