package org.tbank.service.observer;

import org.springframework.stereotype.Component;
import org.tbank.models.Location;

import java.util.ArrayList;
import java.util.List;
@Component
public class LocationSubject {

    private final List<LocationObserver> observers = new ArrayList<>();

    public void addObserver(LocationObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(LocationObserver observer) {
        observers.remove(observer);
    }

    public void notifyObservers(Location location, String action) {
        for (LocationObserver observer : observers) {
            observer.update(location, action);
        }
    }
}
