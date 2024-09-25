package org.tbank.service;

import org.springframework.stereotype.Service;
import org.tbank.dao.DAO;
import org.tbank.models.Location;

import java.util.Collection;
import java.util.Optional;

@Service
public class LocationService {

    public final DAO<String, Location> concurrentHashMap;

    public LocationService(DAO<String, Location> concurrentHashMap) {
        this.concurrentHashMap = concurrentHashMap;
    }

    public Collection<Location> getAllLocation() {
        return concurrentHashMap.getAll();
    }

    public Location getLocation(String slug) {
        Optional<Location> location = Optional.ofNullable(concurrentHashMap.get(slug));
        return location.orElseThrow(() -> new IllegalArgumentException(String.valueOf(slug)));

    }

    public void addLocation(String slug, Location location) {
        Optional<Location> existingLocation = Optional.ofNullable(concurrentHashMap.get(slug));
        existingLocation.ifPresent(n -> {
            throw new IllegalArgumentException("Location с таким slug уже существует: " + slug);
        });
        concurrentHashMap.put(slug, location);
    }

    public void updateLocation(String slug, Location location) {
        concurrentHashMap.update(slug, location);
    }
    public void deleteCategory(String slug) {
        Optional<Location> location = Optional.ofNullable(concurrentHashMap.get(slug));
        location.orElseThrow(() -> new IllegalArgumentException(String.valueOf(slug)));
        concurrentHashMap.remove(slug);
    }
}
