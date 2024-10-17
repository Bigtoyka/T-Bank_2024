package org.tbank.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Event {
    private Long id;
    private String title;
    @JsonProperty("price")
    private String price;
}
