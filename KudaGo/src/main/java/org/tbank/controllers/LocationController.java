package org.tbank.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.tbank.annotations.TimeExecution;
import org.tbank.models.Location;
import org.tbank.repository.LocationRepository;
import org.tbank.service.LocationService;
import org.tbank.service.ResourceNotFoundException;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/api/v1/locations")
@TimeExecution
public class LocationController {

    @Autowired
    private final LocationService locationService;

    @Autowired
    private final LocationRepository locationRepository;

    public LocationController(LocationService locationService, LocationRepository locationRepository) {
        this.locationService = locationService;
        this.locationRepository = locationRepository;
    }

    @GetMapping()
    public ResponseEntity<Collection<Location>> getAllLocations() {
        return ResponseEntity.ok(locationRepository.findAll());
    }

    @GetMapping("/{slug}")
    public Location getLocation(@PathVariable("slug") String slug) {
        return locationRepository.findBySlug(slug);
    }

    @PostMapping()
    public ResponseEntity<String> addLocation(@RequestBody Location location) {
        log.info("Добавить локацию: {}", location.toString());
        locationRepository.save(location);
        return ResponseEntity.ok("Локация добавлена");
    }

    @PutMapping("/{slug}")
    public ResponseEntity<String> updateLocation(@PathVariable("slug") String slug, @RequestBody Location location) {
        log.info("Обновлуние локации: {}", location.toString());
        Location existingLocation = locationRepository.findBySlug(slug);
        if (existingLocation == null) {
            throw new ResourceNotFoundException("Локация с данным slug не найдена: " + slug);
        }

        existingLocation.setName(location.getName());
        existingLocation.setSlug(location.getSlug());
        locationRepository.save(existingLocation);

        return ResponseEntity.ok("Обновление прошло успешно");
    }

    @DeleteMapping("/{slug}")
    public ResponseEntity<String> deleteLocation(@PathVariable("slug") String slug) {
        log.info("Удаление категории: {}", slug);
        Location location = locationRepository.findBySlug(slug);
        locationRepository.delete(location);
        return ResponseEntity.ok("Категория удалена");
    }
}
