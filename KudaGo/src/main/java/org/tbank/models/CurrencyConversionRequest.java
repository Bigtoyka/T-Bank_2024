package org.tbank.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CurrencyConversionRequest {

    private String fromCurrency;
    private String toCurrency;
    private BigDecimal amount;
}
