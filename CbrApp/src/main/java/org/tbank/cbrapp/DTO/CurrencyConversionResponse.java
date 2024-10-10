package org.tbank.cbrapp.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

// Ответ на POST /currencies/convert
@Data
@AllArgsConstructor
public class CurrencyConversionResponse {
    private String fromCurrency;
    private String toCurrency;
    private double convertedAmount;
}
