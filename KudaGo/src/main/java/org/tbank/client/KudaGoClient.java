package org.tbank.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.tbank.models.Category;
import org.tbank.models.Location;

import java.util.Optional;

@Slf4j
@Component
public class KudaGoClient {

    private final RestTemplate restTemplate;

    @Value("${api.url.categories}")
    private String KUDAGO_API_URL_CATEGORY;
    @Value("${api.url.locations}")
    private String KUDAGO_API_URL_LOCATION;


    public KudaGoClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Optional<Category[]> requestCategories() {
        log.info("Запрос категорий с KudaGo API");
        return Optional.ofNullable(restTemplate.getForObject(KUDAGO_API_URL_CATEGORY, Category[].class));
    }
    public Optional<Location[]> requestLocation() {
        log.info("Запрос локаций с KudaGo API");
        return Optional.ofNullable(restTemplate.getForObject(KUDAGO_API_URL_LOCATION, Location[].class));
    }

}
