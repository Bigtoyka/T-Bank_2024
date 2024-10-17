package org.tbank.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.tbank.annotations.TimeExecution;
import org.tbank.client.KudaGoClient;
import org.tbank.dao.UniversalDAO;
import org.tbank.models.Category;
import org.tbank.models.Location;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ExecutionException;


@Slf4j
@Configuration
public class DataInitConfig {
    private final UniversalDAO<Integer, Category> categoryDAO;
    private final UniversalDAO<String, Location> locationDAO;
    private final KudaGoClient kudaGoClient;
    private final ExecutorService fixedThreadPool;
    private final ExecutorService scheduledThreadPool;
    private final Duration initializationSchedule;
    private final RestTemplate restTemplate;


    public DataInitConfig(UniversalDAO<Integer, Category> categoryDAO,
                          UniversalDAO<String, Location> locationDAO,
                          KudaGoClient kudaGoClient,
                          @Qualifier("fixedThreadPool") ExecutorService fixedThreadPool,
                          @Qualifier("scheduledThreadPool") ExecutorService scheduledThreadPool,
                          Duration initializationSchedule, RestTemplate restTemplate) {
        this.categoryDAO = categoryDAO;
        this.locationDAO = locationDAO;
        this.kudaGoClient = kudaGoClient;
        this.fixedThreadPool = fixedThreadPool;
        this.scheduledThreadPool = scheduledThreadPool;
        this.initializationSchedule = initializationSchedule;
        this.restTemplate = restTemplate;
    }

    @Bean
    @TimeExecution
    public CommandLineRunner doInit(RestTemplate restTemplate) {
        return args -> {
            log.info("Старт инициализации данных");
            initializeData();
        };
    }

    private void initializeData() {
        if (scheduledThreadPool instanceof ScheduledExecutorService) {
            ScheduledExecutorService scheduler = (ScheduledExecutorService) scheduledThreadPool;
            scheduler.schedule(this::doInitialization, initializationSchedule.toMillis(), TimeUnit.MILLISECONDS);
        } else {
            throw new IllegalStateException("scheduledThreadPool должен быть ScheduledExecutorService");
        }
    }

    private void doInitialization() {
        long startTime = System.currentTimeMillis();
        List<Future<Void>> futures = new ArrayList<>();
        futures.add(fixedThreadPool.submit(this::initializeCategories));
        futures.add(fixedThreadPool.submit(this::initializeLocations));
        for (Future<Void> future : futures) {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                log.error("Ошибка при инициализации данных: ", e);
            }
        }
        long endTime = System.currentTimeMillis();
        log.info("Инициализация данных завершена за {} миллисекунд.", (endTime - startTime));
    }

    private Void initializeCategories() {
        try {
            log.info("Инициализация категорий");
            Optional<Category[]> categories = kudaGoClient.requestCategories();
            categories.ifPresent(cat -> {
                for (Category category : cat) {
                    categoryDAO.put(category.getId(), category);
                    log.info("Инициализированные категории: {}", category.getId());
                }
                log.info("Категории успешно инициализированы");
            });
        } catch (Exception e) {
            log.error("Ошибка инициализации категорий: ", e);
        }
        return null;
    }

    private Void initializeLocations() {
        try {
            log.info("Инициализация локаций.");
            Optional<Location[]> locations = kudaGoClient.requestLocation();
            locations.ifPresent(loc -> {
                for (Location location : loc) {
                    locationDAO.put(location.getSlug(), location);
                    log.info("Инициализированные локации: {}", location.getSlug());
                }
                log.info("Локации успешно инициализированы");
            });
        } catch (Exception e) {
            log.error("Ошибка инициализации локаций: ", e);
        }
        return null;
    }
}