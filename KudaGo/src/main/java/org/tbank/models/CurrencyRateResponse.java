package org.tbank.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CurrencyRateResponse {
    private String currency;
    private double rate;
}
