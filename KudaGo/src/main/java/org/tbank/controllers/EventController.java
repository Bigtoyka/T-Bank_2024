package org.tbank.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.web.client.RestTemplate;
import org.tbank.models.CurrencyConversionRequest;
import org.tbank.models.Event;
import org.tbank.models.EventResponse;
import org.tbank.models.EventSearchResponse;
import org.tbank.models.CurrencyConversionResponse;
import org.tbank.repository.EventRepository;
import org.tbank.repository.LocationRepository;
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
    private EventRepository eventRepository;
    private LocationRepository locationRepository;

    @Autowired
    public EventController(RestTemplate restTemplate, CurrencyService currencyService, EventService eventService, EventRepository eventRepository, LocationRepository locationRepository) {
        this.currencyService = currencyService;
        this.eventService = eventService;
        this.eventRepository = eventRepository;
        this.locationRepository = locationRepository;
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

    @GetMapping("/all")
    public ResponseEntity<List<EventResponse>> getAllEvents() {
        List<Event> events = eventRepository.findAll();
        List<EventResponse> eventResponses = events.stream()
                .map(event -> new EventResponse(
                        event.getId(),
                        event.getTitle(),
                        event.getStartDate(),
                        event.getPrice(),
                        event.getLocation() != null ? event.getLocation().getSlug() : null
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(eventResponses);
    }


    @PostMapping
    public ResponseEntity<EventResponse> addEvent(@RequestBody Event event) {
        if (event.getLocation() == null || !locationRepository.existsById(event.getLocation().getId())) {
            log.error("Location not found or invalid: {}", event.getLocation());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        Event savedEvent = eventRepository.save(event);
        EventResponse response = new EventResponse(savedEvent.getId(), savedEvent.getTitle(), savedEvent.getStartDate(), savedEvent.getPrice(), savedEvent.getLocation().getSlug());
        log.info("Event created successfully: {}", response);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEvent(@PathVariable("id") Long id) {
        if (!eventRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Событие с ID " + id + " не найдено.");
        }
        eventRepository.deleteById(id);
        return ResponseEntity.ok("Событие с ID " + id + " успешно удалено.");
    }

    @GetMapping("/search")
    public ResponseEntity<List<EventSearchResponse>> searchEvents(@RequestParam(required = false) Long locationId) {
        log.info("Searching events with locationId: {}", locationId);
        if (locationId == null) {
            log.warn("locationId is null");
        }
        List<EventSearchResponse> events = eventService.searchEvents(locationId);
        log.info("Found events: {}", events);
        if (events.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(events);
    }

}
