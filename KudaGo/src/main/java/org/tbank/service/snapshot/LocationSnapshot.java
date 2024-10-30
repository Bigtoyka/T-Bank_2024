package org.tbank.service.snapshot;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class LocationSnapshot {
    private final Long id;
    private final String slug;
    private final String name;
}
