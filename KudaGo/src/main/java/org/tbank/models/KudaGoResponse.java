package org.tbank.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class KudaGoResponse {
    @JsonProperty("count")
    private Integer count;

    @JsonProperty("next")
    private String next;

    @JsonProperty("previous")
    private String previous;

    @JsonProperty("results")
    private Event[] results;
}
