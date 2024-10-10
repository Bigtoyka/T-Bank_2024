package org.tbank.cbrapp.DTO;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

// Запрос для POST /currencies/convert
@Data
public class CurrencyConversionRequest {
    @NotNull
    private String fromCurrency;
    @NotNull
    private String toCurrency;
    @Positive(message = "Тольок положительное значение")
    private double amount;
}
