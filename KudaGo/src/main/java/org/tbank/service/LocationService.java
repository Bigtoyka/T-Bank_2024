package org.tbank.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.tbank.dao.UniversalDAO;
import org.tbank.models.Location;
import org.tbank.service.snapshot.LocationSnapshot;
import org.tbank.service.snapshot.SnapshotManager;
import org.tbank.service.observer.LocationSubject;

import java.util.Collection;
import java.util.Optional;

@Slf4j
@Service
public class LocationService {

    public final UniversalDAO<String, Location> concurrentHashMap;
    private final LocationSubject locationSubject;
    private final SnapshotManager snapshotManager;


    public LocationService(UniversalDAO<String, Location> concurrentHashMap, LocationSubject locationSubject, SnapshotManager snapshotManager) {
        this.concurrentHashMap = concurrentHashMap;
        this.locationSubject = locationSubject;
        this.snapshotManager = snapshotManager;
    }

    public Collection<Location> getAllLocation() {
        log.info("Получение всех локаций");
        return concurrentHashMap.getAll();
    }

    public Location getLocation(String slug) {
        log.info("Получение всех локаций по slug: {}", slug);
        Optional<Location> location = Optional.ofNullable(concurrentHashMap.get(slug));
        return location.orElseThrow(() -> new IllegalArgumentException(String.valueOf(slug)));

    }

    public void addLocation(String slug, Location location) {
        log.info("Добавление локации по slug: {}", slug);
        Optional<Location> existingLocation = Optional.ofNullable(concurrentHashMap.get(slug));
        existingLocation.ifPresent(n -> {
            throw new IllegalArgumentException("Location с таким slug уже существует: " + slug);
        });
        concurrentHashMap.put(slug, location);
        locationSubject.notifyObservers(location, "ADD");
        log.info("Локация добавлена");
    }

    public void updateLocation(String slug, Location location) {
        Location currentLocation = getLocation(slug);
        snapshotManager.saveLocationSnapshot(new LocationSnapshot(currentLocation.getId(), currentLocation.getSlug(), currentLocation.getName()));
        log.info("Обновление локации по slug: {}", slug);
        concurrentHashMap.update(slug, location);
        locationSubject.notifyObservers(location, "UPDATE");
        log.info("Локация обновлена");
    }

    public void deleteCategory(String slug) {
        Location currentLocation = getLocation(slug);
        snapshotManager.saveLocationSnapshot(new LocationSnapshot(currentLocation.getId(), currentLocation.getSlug(), currentLocation.getName()));
        log.info("Удаление локации по slug: {}", slug);
        Optional<Location> location = Optional.ofNullable(concurrentHashMap.get(slug));
        location.orElseThrow(() -> new IllegalArgumentException(String.valueOf(slug)));
        concurrentHashMap.remove(slug);
        locationSubject.notifyObservers(location.get(), "DELETE");
        log.info("Локация удалена");
    }

}
