package org.tbank.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration

public class ExecutorServiceConfig {
    @Value("${app.fixed.threadpool.size}")
    private int fixedThreadPoolSize;

    @Value("${app.scheduled.threadpool.size}")
    private int scheduledThreadPoolSize;

    @Bean(name = "fixedThreadPool")
    public ExecutorService fixedThreadPool() {
        return Executors.newFixedThreadPool(fixedThreadPoolSize, runnable -> {
            Thread thread = Executors.defaultThreadFactory().newThread(runnable);
            return thread;
        });
    }

    @Bean(name = "scheduledThreadPool")
    public ExecutorService scheduledThreadPool() {
        return Executors.newScheduledThreadPool(scheduledThreadPoolSize);
    }

    @Bean
    public Duration initializationSchedule(@Value("${app.initialization.schedule}") Duration duration) {
        return duration;
    }
}
