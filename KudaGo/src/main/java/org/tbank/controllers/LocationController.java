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
import org.tbank.service.LocationService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/api/v1/locations")
@TimeExecution
public class LocationController {

    @Autowired
    private final LocationService locationService;

    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @GetMapping()
    public ResponseEntity<Collection<Location>> getAllLocations() {
        return ResponseEntity.ok(locationService.getAllLocation());
    }

    @GetMapping("/{slug}")
    public Location getLocation(@PathVariable("slug") String slug) {
        return locationService.getLocation(slug);
    }

    @PostMapping()
    public ResponseEntity<String> addLocation(@RequestBody Location location) {
        log.info("Добавить локацию: {}", location.toString());
        locationService.addLocation(location.getSlug(), location);
        return ResponseEntity.ok("Локация добавлена");
    }

    @PutMapping("/{slug}")
    public ResponseEntity<String> updateLocation(@PathVariable("slug") String slug, @RequestBody Location location) {
        log.info("Обновлуние локации: {}", location.toString());
        locationService.updateLocation(slug, location);
        return ResponseEntity.ok("Обновление прошло успешно");
    }

    @DeleteMapping("/{slug}")
    public ResponseEntity<String> deleteLocation(@PathVariable("slug") String slug) {
        log.info("Удаление категории: {}", slug);
        locationService.deleteCategory(slug);
        return ResponseEntity.ok("Категория удалена");
    }
}
