package org.tbank.service.snapshot;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CategorySnapshot {
    private final int id;
    private final String slug;
    private final String name;
}
