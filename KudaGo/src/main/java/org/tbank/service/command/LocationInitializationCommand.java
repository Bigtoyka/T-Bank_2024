package org.tbank.service.command;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.tbank.client.KudaGoClient;
import org.tbank.models.Location;
import org.tbank.repository.LocationRepository;

import java.util.Optional;

@Slf4j
@Component
public class LocationInitializationCommand implements InitializationCommand{
    private final LocationRepository locationRepository;
    private final KudaGoClient kudaGoClient;

    public LocationInitializationCommand(LocationRepository locationRepository, KudaGoClient kudaGoClient) {
        this.locationRepository = locationRepository;
        this.kudaGoClient = kudaGoClient;
    }

    @Override
    public Void execute() {
        log.info("Инициализация локаций...");
        Optional<Location[]> locations = kudaGoClient.requestLocation();
        locations.ifPresent(loc -> {
            for (Location location : loc) {
                locationRepository.save(location);
                log.info("Инициализированные локации: {}", location.getSlug());
            }
            log.info("Локации успешно инициализированы.");
        });
        return null;
    }
}
