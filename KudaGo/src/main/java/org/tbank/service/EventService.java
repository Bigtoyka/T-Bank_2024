package org.tbank.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.tbank.models.Event;
import org.tbank.models.KudaGoResponse;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class EventService {
    private final RestTemplate restTemplate;

    @Value("${api.url.events}")
    private String KUDAGO_API_URL_EVENT;

    private String fields = "?fields=id,title,price&actual_since=";
    private String actual_until = "&actual_until=";

    public EventService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Mono<List<Event>> getEventsFromKudaGo(LocalDate start, LocalDate end) {
        return Mono.fromCallable(() -> {
            String url = KUDAGO_API_URL_EVENT + fields + start + actual_until + end;
            log.info("KudaGo API URL: {}", url);

            try {
                ResponseEntity<KudaGoResponse> responseEntity = restTemplate.getForEntity(url, KudaGoResponse.class);
                log.info("Статус ответа KudaGo API: {}", responseEntity.getStatusCode());

                if (responseEntity.getBody() != null) {
                    log.info("Ответ от KudaGo API: {}", responseEntity.getBody());
                    return Arrays.asList(responseEntity.getBody().getResults());
                } else {
                    log.warn("Ответ от KudaGo API пуст.");
                    return List.of();
                }

            } catch (HttpClientErrorException e) {
                log.error("Ошибка клиента при запросе к KudaGo: {}", e.getMessage());
                throw e;
            } catch (HttpServerErrorException e) {
                log.error("Ошибка сервера при запросе к KudaGo: {}", e.getMessage());
                throw e;
            } catch (Exception e) {
                log.error("Неизвестная ошибка при запросе к KudaGo: {}", e.getMessage());
                throw e;
            }
        });
    }
}
