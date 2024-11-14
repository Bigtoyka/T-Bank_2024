package org.tbank.service.observer;

import lombok.extern.slf4j.Slf4j;
import org.tbank.models.Location;

@Slf4j
public class LoggingLocationObserver implements LocationObserver {
    @Override
    public void update(Location location, String action) {
        log.info("Location {} был {}", location.getSlug(), action);
    }
}
