package org.tbank.cbrapp.ontroller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.tbank.cbrapp.DTO.CurrencyConversionRequest;
import org.tbank.cbrapp.DTO.CurrencyConversionResponse;
import org.tbank.cbrapp.DTO.CurrencyRateResponse;
import org.tbank.cbrapp.service.CurrencyService;

@RestController
@RequestMapping("/currencies")
public class CurrencyController {
    private final CurrencyService currencyService;

    public CurrencyController(CurrencyService currencyService) {
        this.currencyService = currencyService;
    }


    @GetMapping("/rates/{code}")
    public ResponseEntity<CurrencyRateResponse> getCurrencyRate(@PathVariable("code") String code) {
        CurrencyRateResponse response = currencyService.getCurrencyRate(code);
        return ResponseEntity.ok(response);
    }


    @PostMapping("/convert")
    public ResponseEntity<CurrencyConversionResponse> convertCurrency(@Valid @RequestBody CurrencyConversionRequest request) {
        CurrencyConversionResponse response = currencyService.convertCurrency(request);
        return ResponseEntity.ok(response);
    }
}
