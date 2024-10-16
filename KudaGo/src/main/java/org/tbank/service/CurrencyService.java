package org.tbank.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.tbank.models.CurrencyConversionRequest;
import org.tbank.models.CurrencyConversionResponse;
import org.tbank.models.CurrencyRateResponse;
import org.tbank.exception.CurrencyNotFoundException;
import org.tbank.exception.ServiceUnavailableException;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.StringReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
public class CurrencyService {
    private final RestTemplate restTemplate;
    private final String dateReq ="?date_req=";
    private final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private String cbrUrl = "http://www.cbr.ru/scripts/XML_daily.asp";

    public CurrencyService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    @Cacheable("currencyRates")
    public CurrencyRateResponse getCurrencyRate(String code) {
        String url = cbrUrl + dateReq + LocalDate.now().format(dateFormat);
        log.info("Запрос курса валюты для кода: {}", code);
        ResponseEntity<String> response;
        try {
            response = restTemplate.getForEntity(url, String.class);
            log.info("Ответ от ЦБ для валюты {}: {}", code, response.getBody());
        } catch (HttpClientErrorException e) {
            log.error("Неверный запрос к ЦБ для валюты {}: {}", code, e.getMessage());
            throw new BadRequestException("Неверный запрос к ЦБ: " + e.getMessage());
        } catch (HttpServerErrorException e) {
            log.error("API ЦБ недоступно {}: {}", code, e.getMessage());
            throw new ServiceUnavailableException("API ЦБ недоступно: " + e.getMessage());
        } catch (Exception e) {
            log.error("Неизвестная ошибка при запросе валюты {}: {}", code, e.getMessage());
            throw new RuntimeException("Неизвестная ошибка: " + e.getMessage());
        }

        if (response.getStatusCode().is2xxSuccessful()) {
            String xmlResponse = response.getBody();
            double rate = parseCurrencyRateFromXml(xmlResponse, code);
            log.info("Курс валюты {}: {}", code, rate);
            return new CurrencyRateResponse(code, rate);
        } else {
            log.error("Ошибка получения данных от ЦБ для валюты {}", code);
            throw new BadRequestException("Ошибка получения данных от ЦБ");
        }
    }

    public CurrencyConversionResponse convertCurrency(CurrencyConversionRequest request) {
        log.info("Конвертация валюты {} в {}", request.getFromCurrency(), request.getToCurrency());
        BigDecimal fromRate = BigDecimal.valueOf(getCurrencyRate(request.getFromCurrency()).getRate());
        BigDecimal toRate = BigDecimal.valueOf(getCurrencyRate(request.getToCurrency()).getRate());

        if (fromRate.compareTo(BigDecimal.ZERO) == 0 || toRate.compareTo(BigDecimal.ZERO) == 0) {
            log.error("Валюта не найдена для конвертации: {} или {}", request.getFromCurrency(), request.getToCurrency());
            throw new CurrencyNotFoundException("Валюта не была найдена в списке ЦБ");
        }
        BigDecimal convertedAmount = request.getAmount().multiply(fromRate).divide(toRate, 2, RoundingMode.HALF_UP);
        log.info("Конвертированная сумма: {}", convertedAmount);
        return new CurrencyConversionResponse(request.getFromCurrency(), request.getToCurrency(), convertedAmount.doubleValue());
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
}
