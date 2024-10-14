package org.tbank.cbrapp.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CurrencyConversionRequest {
    @NotNull
    private String fromCurrency;
    @NotNull
    private String toCurrency;
    @Positive(message = "Тольок положительное значение")
    private BigDecimal amount;
}
