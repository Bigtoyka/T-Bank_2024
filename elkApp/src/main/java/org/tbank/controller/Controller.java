package org.tbank.controller;


import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.tbank.service.CreateUUID;

@Slf4j
@RestController
@RequestMapping("/api")
public class Controller {
    private CreateUUID uuid = new CreateUUID();

    public Controller(CreateUUID uuid) {
        this.uuid = uuid;
    }

    @GetMapping("/start")
    public String generateLog() {
        log.info("Request start");
        String result = uuid.createId();
        MDC.put("UUID: ", result);
        log.info("Request finished");
        MDC.clear();
        return result;
    }
}
