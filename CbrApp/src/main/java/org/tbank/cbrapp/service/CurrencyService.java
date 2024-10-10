package org.tbank.cbrapp.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.tbank.cbrapp.DTO.CurrencyConversionRequest;
import org.tbank.cbrapp.DTO.CurrencyConversionResponse;
import org.tbank.cbrapp.DTO.CurrencyRateResponse;
import org.tbank.cbrapp.exception.CurrencyNotFoundException;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.StringReader;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class CurrencyService {
    private final RestTemplate restTemplate;
    private final Cache<String, Double> cache;

    @Value("${currency.cbr-url}")
    private String cbrUrl;

    public CurrencyService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.cache = Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofHours(1))
                .build();
    }
    @Cacheable("currencyRates")
    @CircuitBreaker(name = "currencyService", fallbackMethod = "fallbackGetCurrencyRate")
    public CurrencyRateResponse getCurrencyRate(String code) {
        String url = cbrUrl + "?date_req=" + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            String xmlResponse = response.getBody();
            double rate = parseCurrencyRateFromXml(xmlResponse, code);
            cache.put(code.toUpperCase(), rate);
            return new CurrencyRateResponse(code, rate);
        } else {
            throw new RuntimeException("Ошибка получения данных от ЦБ");
        }
    }

    public CurrencyConversionResponse convertCurrency(CurrencyConversionRequest request) {

        double fromRate = getCurrencyRate(request.getFromCurrency()).getRate();
        double toRate = getCurrencyRate(request.getToCurrency()).getRate();
        if (fromRate == 0 || toRate == 0) {
            throw new CurrencyNotFoundException("Валюта не была найдена в списке ЦБ");
        }
        double convertedAmount = (request.getAmount() * fromRate) / toRate;
        return new CurrencyConversionResponse(request.getFromCurrency(), request.getToCurrency(), convertedAmount);
    }

    private double parseCurrencyRateFromXml(String xmlResponse, String currencyCode) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(xmlResponse));
            Document doc = builder.parse(is);
            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xpath = xPathFactory.newXPath();
            XPathExpression expr = xpath.compile(String.format("//Valute[CharCode='%s']/Value", currencyCode));
            if (currencyCode.equals("RUB")) {
                return Double.parseDouble("1");
            }
            String rateStr = (String) expr.evaluate(doc, XPathConstants.STRING);
            if (rateStr == null || rateStr.trim().isEmpty()) {
                throw new IllegalArgumentException("Валюта не была найдена: " + currencyCode);
            }
            return Double.parseDouble(rateStr.replace(",", "."));

        } catch (Exception e) {
            e.printStackTrace();
            throw new CurrencyNotFoundException("Валюта не была найдена в списке ЦБ");
        }
    }
    public double fallbackGetCurrencyRate(String currencyCode, Throwable throwable) {
        System.out.println("Метод сработал из-за: " + throwable.getMessage());
        return 0.0;
    }
}
