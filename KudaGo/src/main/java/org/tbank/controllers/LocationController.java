package org.tbank.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.tbank.annotations.TimeExecution;
import org.tbank.models.Location;
import org.tbank.service.LocationService;

import java.util.Collection;

@TimeExecution
@Slf4j
@RestController
@RequestMapping("/api/v1/locations")
public class LocationController {

    private final LocationService locationService;

    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @TimeExecution
    @GetMapping()
    public ResponseEntity<Collection<Location>> getAllLocations() {
        return ResponseEntity.ok(locationService.getAllLocation());
    }

    @TimeExecution
    @GetMapping("/{slug}")
    public Location getLocation(@PathVariable("slug") String slug) {
        return locationService.getLocation(slug);
    }

    @TimeExecution
    @PostMapping()
    public ResponseEntity<String> addLocation(@RequestBody Location location) {
        log.info("Добавить локацию: {}", location.toString());

        try {
            locationService.addLocation(location.getSlug(), location);
            return ResponseEntity.ok("Локация добавлена");
        } catch (IllegalArgumentException e) {
            log.error("Ошибка при добавлении локации: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Ошибка: " + e.getMessage());
        } catch (Exception e) {
            log.error("Неожиданная ошибка: ", e);
            return ResponseEntity.badRequest().body("Неожиданная ошибка: " + e.getMessage());
        }
    }

    @TimeExecution
    @PutMapping("/{slug}")
    public ResponseEntity<String> updateLocation(@PathVariable("slug") String slug, @RequestBody Location location) {
        log.info("Обновлуние локации: {}", location.toString());
        try {
            locationService.updateLocation(slug, location);
            return ResponseEntity.ok("Обновление прошло успешно");
        } catch (IllegalArgumentException e) {
            log.error("Ошибка при обновлении локации: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Ошибка: " + e.getMessage());
        } catch (Exception e) {
            log.error("Неожиданная ошибка: ", e);
            return ResponseEntity.badRequest().body("Неожиданная ошибка" + e.getMessage());
        }
    }

    @TimeExecution
    @DeleteMapping("/{slug}")
    public ResponseEntity<String> deleteLocation(@PathVariable("slug") String slug) {
        log.info("Удаление категории: {}", slug);
        try {
            locationService.deleteCategory(slug);
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
