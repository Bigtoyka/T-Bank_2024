package org.tbank.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.tbank.service.observer.LocationSubject;
import org.tbank.service.observer.LoggingLocationObserver;

@Configuration
public class ObserverConfig {

    @Bean
    public LocationSubject locationSubject() {
        LocationSubject subject = new LocationSubject();
        subject.addObserver(new LoggingLocationObserver());
        return subject;
    }
}
