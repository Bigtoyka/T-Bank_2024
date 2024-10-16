package org.tbank.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.tbank.models.CurrencyConversionRequest;
import org.tbank.models.CurrencyConversionResponse;
import org.tbank.models.Event;
import org.tbank.models.KudaGoResponse;
import org.tbank.service.CurrencyService;
import org.tbank.service.PriceUtils;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/events")
public class EventController {
    private final RestTemplate restTemplate;
    private final CurrencyService currencyService;
    private final String kudaGoApiUrl = "https://kudago.com/public-api/v1.4/events";

    @Autowired
    public EventController(RestTemplate restTemplate, CurrencyService currencyService) {
        this.restTemplate = restTemplate;
        this.currencyService = currencyService;
    }

    @GetMapping
    public CompletableFuture<ResponseEntity<List<Event>>> getEvents(
            @RequestParam("budget") BigDecimal budget,
            @RequestParam("currency") String currency,
            @RequestParam(value = "dateFrom", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dateFrom,
            @RequestParam(value = "dateTo", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dateTo) {

        // Получаем текущую неделю, если даты не указаны
        LocalDate start = (dateFrom != null) ? dateFrom : LocalDate.now().with(DayOfWeek.MONDAY);
        LocalDate end = (dateTo != null) ? dateTo : start.plusDays(7);

        // Параллельный запрос событий из KudaGo
        CompletableFuture<List<Event>> eventsFuture = getEventsFromKudaGo(start, end);

        // Параллельный запрос на конвертацию бюджета
        CompletableFuture<Double> convertedBudgetFuture = CompletableFuture.supplyAsync(() -> {
            CurrencyConversionRequest request = new CurrencyConversionRequest(currency, "RUB", budget);
            CurrencyConversionResponse response = currencyService.convertCurrency(request);
            return response.getConvertedAmount();
        });

        // Объединяем результаты с использованием thenCombine
        return eventsFuture.thenCombine(convertedBudgetFuture, (events, convertedBudget) -> {
            List<Event> suitableEvents = events.stream()
                    .filter(event -> {
                        String priceStr = event.getPrice();
                        BigDecimal price = PriceUtils.extractPrice(priceStr);
                        return price.compareTo(BigDecimal.valueOf(convertedBudget)) <= 0;
                    })
                    .collect(Collectors.toList());

            // Возвращаем список подходящих событий
            return ResponseEntity.ok(suitableEvents);
        });

    }

    private CompletableFuture<List<Event>> getEventsFromKudaGo(LocalDate start, LocalDate end) {
        return CompletableFuture.supplyAsync(() -> {
            String url = kudaGoApiUrl + "?fields=id,title,price&actual_since=" + start + "&actual_until=" + end;
            log.info("KudaGo API URL: {}", url);
            try {
                // Получаем ответ от KudaGo API
                ResponseEntity<KudaGoResponse> responseEntity = restTemplate.getForEntity(url, KudaGoResponse.class);

                // Логируем статус ответа и тело ответа
                log.info("Статус ответа KudaGo API: {}", responseEntity.getStatusCode());
                if (responseEntity.getBody() != null) {
                    log.info("Ответ от KudaGo API: {}", responseEntity.getBody());

                } else {
                    log.warn("Ответ от KudaGo API пуст.");
                }

                return List.of(responseEntity.getBody().getResults());

            } catch (HttpClientErrorException e) {
                log.error("Ошибка клиента при запросе к KudaGo: {}", e.getMessage());
                throw e; // повторно выбрасываем исключение для обработки выше
            } catch (HttpServerErrorException e) {
                log.error("Ошибка сервера при запросе к KudaGo: {}", e.getMessage());
                throw e; // повторно выбрасываем исключение для обработки выше
            } catch (Exception e) {
                log.error("Неизвестная ошибка при запросе к KudaGo: {}", e.getMessage());
                throw e; // повторно выбрасываем исключение для обработки выше
            }
        });
    }
}
