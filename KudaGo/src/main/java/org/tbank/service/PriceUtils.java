package org.tbank.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PriceUtils {

    public static BigDecimal extractPrice(String priceStr) {
        // Проверяем, не является ли строка пустой или содержит текст
        if (priceStr == null || priceStr.trim().isEmpty() || priceStr.contains("уточняйте на сайте")) {
            return BigDecimal.ZERO; // Возвращаем 0, если цена не указана
        }

        // Ищем числовые значения в строке
        String[] parts = priceStr.split(" ");
        List<BigDecimal> prices = new ArrayList<>();

        for (String part : parts) {
            try {
                prices.add(new BigDecimal(part.replaceAll("[^\\d.]", ""))); // Оставляем только цифры и точку
            } catch (NumberFormatException e) {
                // Игнорируем нечисловые значения
            }
        }

        // Если найдены цены, возвращаем минимальную
        return prices.isEmpty() ? BigDecimal.ZERO : Collections.min(prices);
    }

}
