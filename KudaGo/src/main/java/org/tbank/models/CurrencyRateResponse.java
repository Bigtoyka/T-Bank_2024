package org.tbank.models;

import lombok.AllArgsConstructor;
import lombok.Data;

// Ответ на GET /currencies/rates/{code}
@Data
@AllArgsConstructor
public class CurrencyRateResponse {
    private String currency;
    private double rate;
}
