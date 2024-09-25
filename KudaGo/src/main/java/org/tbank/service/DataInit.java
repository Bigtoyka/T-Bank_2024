package org.tbank.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.tbank.annotations.TimeExecution;
import org.tbank.dao.DAO;
import org.tbank.models.Category;
import org.tbank.models.Location;

import java.util.Optional;

@Slf4j
@Configuration
public class DataInit {
    private final DAO<Integer, Category> categoryDAO;
    private final DAO<String, Location> locationDAO;
    private final String KUDAGOAPIURLCATEGORY = "https://kudago.com/public-api/v1.4/place-categories";
    private final String KUDAGOAPIURLLOCATION = "https://kudago.com/public-api/v1.4/locations";


    public DataInit(DAO<Integer, Category> categoryDAO, DAO<String, Location> locationDAO) {
        this.categoryDAO = categoryDAO;
        this.locationDAO = locationDAO;
    }

    @Bean
    @TimeExecution
    public CommandLineRunner doInit(RestTemplate restTemplate) {
        return args -> {
            log.info("Старт инициализации данных");
            try {
                log.info("Инициализация категорий");
                Optional<Category[]> categories = Optional
                        .ofNullable(restTemplate.getForObject(KUDAGOAPIURLCATEGORY, Category[].class));
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
                Optional<Location[]> locations = Optional
                        .ofNullable(restTemplate.getForObject(KUDAGOAPIURLLOCATION, Location[].class));
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
