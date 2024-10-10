package org.tbank.cbrapp;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.junit.jupiter.api.Test;
import org.tbank.cbrapp.DTO.CurrencyConversionRequest;
import org.tbank.cbrapp.DTO.CurrencyConversionResponse;
import org.tbank.cbrapp.exception.ServiceUnavailableException;
import org.tbank.cbrapp.service.CurrencyService;

@SpringBootTest
@AutoConfigureMockMvc
public class CurrencyControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private CurrencyService currencyService;
    //для трушных названий тестов, как было в прошлой домашке нет времени ))) Добавлю потом
    @Test
    public void testConvertCurrencyValidation() throws Exception {
        String invalidRequest = "{\"fromCurrency\": \"\", \"toCurrency\": \"RUB\", \"amount\": -10}";

        mockMvc.perform(post("/currencies/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequest))
                .andExpect(status().isBadRequest());
    }


    @Test
    public void testConvertCurrency() throws Exception {
        CurrencyConversionResponse mockResponse = new CurrencyConversionResponse("USD",
                "RUB",
                7500.0);

        when(currencyService.convertCurrency(any(CurrencyConversionRequest.class)))
                .thenReturn(mockResponse);

        String validRequest = """
            {
                "fromCurrency": "USD",
                "toCurrency": "RUB",
                "amount": 100.0
            }
        """;

        mockMvc.perform(post("/currencies/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fromCurrency").value("USD"))
                .andExpect(jsonPath("$.toCurrency").value("RUB"))
                .andExpect(jsonPath("$.convertedAmount").value(7500.0));
    }
    @Test
    public void testServiceUnavailable() throws Exception {
        when(currencyService.getCurrencyRate(anyString())).thenThrow(new ServiceUnavailableException("Сервис недоступен"));

        mockMvc.perform(get("/currencies/rates/USD"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Internal server error"));
    }
}

