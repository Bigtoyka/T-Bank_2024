package org.tbank.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PriceUtils {

    public static BigDecimal extractPrice(String priceStr) {
        if (priceStr == null || priceStr.trim().isEmpty() || priceStr.contains("уточняйте на сайте")) {
            return BigDecimal.ZERO;
        }

        String[] parts = priceStr.split(" ");
        List<BigDecimal> prices = new ArrayList<>();

        for (String part : parts) {
            try {
                prices.add(new BigDecimal(part.replaceAll("[^\\d.]", "")));
            } catch (NumberFormatException e) {
            }
        }
        return prices.isEmpty() ? BigDecimal.ZERO : Collections.min(prices);
    }

}
