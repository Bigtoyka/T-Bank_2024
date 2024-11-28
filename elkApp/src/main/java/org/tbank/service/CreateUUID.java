package org.tbank.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
public class CreateUUID {
    public String createId() {
        log.info("Generate UUID");
        String id = UUID.randomUUID().toString();
        log.info("UUID generate finished");
        return id;
    }
}
