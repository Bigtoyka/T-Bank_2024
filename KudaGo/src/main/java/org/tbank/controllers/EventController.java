package org.tbank.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.tbank.models.CurrencyConversionRequest;
import org.tbank.models.CurrencyConversionResponse;
import org.tbank.models.Event;
import org.tbank.service.CurrencyService;
import org.tbank.service.EventService;
import org.tbank.service.PriceUtils;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/events")
public class EventController {
    private final CurrencyService currencyService;
    private final EventService eventService;

    @Autowired
    public EventController(RestTemplate restTemplate, CurrencyService currencyService, EventService eventService) {
        this.currencyService = currencyService;
        this.eventService = eventService;
    }

    @GetMapping
    public Mono<ResponseEntity<List<Event>>> getEvents(
            @RequestParam("budget") BigDecimal budget,
            @RequestParam("currency") String currency,
            @RequestParam(value = "dateFrom", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dateFrom,
            @RequestParam(value = "dateTo", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dateTo) {

        LocalDate start = (dateFrom != null) ? dateFrom : LocalDate.now().with(DayOfWeek.MONDAY);
        LocalDate end = (dateTo != null) ? dateTo : start.plusDays(7);

        Mono<List<Event>> eventsMono = eventService.getEventsFromKudaGo(start, end);

        Mono<Double> convertedBudgetMono = currencyService.convertCurrency(new CurrencyConversionRequest(currency, "RUB", budget))
                .map(CurrencyConversionResponse::getConvertedAmount);

        return Mono.zip(eventsMono, convertedBudgetMono)
                .flatMap(tuple -> {
                    List<Event> events = tuple.getT1();
                    Double convertedBudget = tuple.getT2();
                    List<Event> suitableEvents = events.stream()
                            .filter(event -> {
                                String priceStr = event.getPrice();
                                BigDecimal price = PriceUtils.extractPrice(priceStr);
                                return price.compareTo(BigDecimal.valueOf(convertedBudget)) <= 0;
                            })
                            .collect(Collectors.toList());
                    return Mono.just(ResponseEntity.ok(suitableEvents));
                });


    }


}
