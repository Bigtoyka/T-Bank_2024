package org.tbank.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.tbank.annotations.TimeExecution;
import org.tbank.client.KudaGoClient;
import org.tbank.dao.UniversalDAO;
import org.tbank.models.Category;
import org.tbank.models.Location;

import java.util.Optional;

@Slf4j
@Configuration
@AllArgsConstructor
public class DataInitConfig {
    private final UniversalDAO<Integer, Category> categoryDAO;
    private final UniversalDAO<String, Location> locationDAO;
    private final KudaGoClient kudaGoClient;


    @Bean
    @TimeExecution
    public CommandLineRunner doInit(RestTemplate restTemplate) {
        return args -> {
            log.info("Старт инициализации данных");
            try {
                log.info("Инициализация категорий");
                Optional<Category[]> categories = kudaGoClient.requestCategories();
                if (categories.isPresent()) {
                    for (Category category : categories.get()) {
                        categoryDAO.put(category.getId(), category);
                        log.info("Инициализированные категории: {}", category.getId());
                    }
                    log.info("Категории успешно инициализированы");
                } else {
                    log.warn("Категории не найдены");
                }
            } catch (Exception e) {
                log.error("Ошибка инициализации категорий: ", e);
            }

            try {
                log.info("Инициализация локаций.");
                Optional<Location[]> locations = kudaGoClient.requestLocation();
                if (locations.isPresent()) {
                    for (Location location : locations.get()) {
                        locationDAO.put(location.getSlug(), location);
                        log.info("Инициализированные локации: {}", location.getSlug());
                    }
                    log.info("Локации успешно инициализированы");
                } else {
                    log.warn("Локации не найдены");
                }
            } catch (Exception e) {
                log.error("Ошибка инициализации локаций: ", e);
            }
            log.info("Инициализация данных завершена");
        };
    }
}