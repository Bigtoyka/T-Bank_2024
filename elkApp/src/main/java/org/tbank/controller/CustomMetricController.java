package org.tbank.controller;

import io.micrometer.core.annotation.Counted;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tbank.service.CreateUUID;
@Slf4j
@RestController
public class CustomMetricController {
    private Counter counter;
    CreateUUID uuid = new CreateUUID();

    public CustomMetricController(MeterRegistry meterRegistry) {
        this.counter = meterRegistry.counter("my_custom_requests", "name", "custom_metric");;
    }

    @GetMapping("/custom-metric")
    public String customMetric() {
        String result = uuid.createId();
        try (var ignore = MDC.putCloseable("requestId", result + " My request")) {
            log.info("Structure log");
        }
        counter.increment();
        return "My custom_metric";
    }
}
