package org.tbank.service.observer;

import org.tbank.models.Location;

public interface LocationObserver {
    void update(Location location, String action);
}
