package org.tbank.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CurrencyConversionResponse {
    private String fromCurrency;
    private String toCurrency;
    private double convertedAmount;
}
