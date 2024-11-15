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
import org.tbank.models.Event;
import org.tbank.models.Location;
import org.tbank.repository.EventRepository;
import org.tbank.repository.LocationRepository;
import org.tbank.service.command.CategoryInitializationCommand;
import org.tbank.service.command.LocationInitializationCommand;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
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
    private LocationRepository locationRepository;
    private EventService eventService;
    private EventRepository eventRepository;


    public DataInitConfig(UniversalDAO<Integer, Category> categoryDAO,
                          UniversalDAO<String, Location> locationDAO,
                          KudaGoClient kudaGoClient,
                          @Qualifier("fixedThreadPool") ExecutorService fixedThreadPool,
                          @Qualifier("scheduledThreadPool") ExecutorService scheduledThreadPool,
                          Duration initializationSchedule, RestTemplate restTemplate, LocationRepository locationRepository, EventService eventService, EventRepository eventRepository) {
        this.categoryDAO = categoryDAO;
        this.locationDAO = locationDAO;
        this.kudaGoClient = kudaGoClient;
        this.fixedThreadPool = fixedThreadPool;
        this.scheduledThreadPool = scheduledThreadPool;
        this.initializationSchedule = initializationSchedule;
        this.restTemplate = restTemplate;
        this.locationRepository = locationRepository;
        this.eventService = eventService;
        this.eventRepository = eventRepository;
    }

//    @Bean
//    @TimeExecution
//    public CommandLineRunner doInit(RestTemplate restTemplate) {
//        return args -> {
//            log.info("Старт инициализации данных");
//            initializeData();
//        };
//    }

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
        CategoryInitializationCommand categoryCommand = new CategoryInitializationCommand(categoryDAO, kudaGoClient);
        LocationInitializationCommand locationCommand = new LocationInitializationCommand(locationRepository, kudaGoClient);

        futures.add(fixedThreadPool.submit(categoryCommand::execute));
        futures.add(fixedThreadPool.submit(locationCommand::execute));
        futures.add(fixedThreadPool.submit(this::initializeEvents));
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
                    locationRepository.save(location);
                    log.info("Инициализированные локации: {}", location.getSlug());
                }
                log.info("Локации успешно инициализированы");
            });
        } catch (Exception e) {
            log.error("Ошибка инициализации локаций: ", e);
        }
        return null;
    }

    private Void initializeEvents() {
        try {
            log.info("Инициализация событий");
            LocalDate start = LocalDate.now().with(DayOfWeek.MONDAY);
            LocalDate end = start.plusDays(7);
            eventService.getEventsFromKudaGo(start, end)
                    .doOnNext(events -> {
                        for (Event event : events) {
                            Location location = locationRepository.findBySlug(event.getLocation().getSlug());
                            if (location != null) {
                                event.setLocation(location);
                                eventRepository.save(event);
                                log.info("Инициализированные события: {}", event.getTitle());
                            } else {
                                log.warn("Локация для события {} не найдена!", event.getTitle());
                            }
                        }
                        log.info("События успешно инициализированы");
                    })
                    .doOnError(e -> log.error("Ошибка инициализации событий: ", e))
                    .subscribe();
        } catch (Exception e) {
            log.error("Ошибка инициализации событий: ", e);
        }
        return null;
    }
}