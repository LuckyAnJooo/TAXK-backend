package com.example.TAXK.demo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.*;

@Service
public class StockPriceService {

    @Value("${finnhub.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    // Get the price of a stock
    public Optional<Double> getStockPrice(String ticker) {
        String url = String.format("https://finnhub.io/api/v1/quote?symbol=%s&token=%s", ticker, apiKey);
        try {
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            if (response != null && response.get("c") != null) {
                return Optional.of(Double.valueOf(response.get("c").toString())); // "c" = current price
            }
        } catch (Exception e) {
            return Optional.empty();
        }
        return Optional.empty();
    }

    // Get the prices of multiple stocks
    public Map<String, Double> getAllStocks(List<String> tickers) {
        Map<String, Double> results = new HashMap<>();
        for (String ticker : tickers) {
            Optional<Double> price = getStockPrice(ticker);
            results.put(ticker, price.orElse(null));
        }
        return results;
    }

    // Get historical price of one single stock
    public List<Map<String, Object>> getHistoricalPrice(String ticker, LocalDate startDate, String resolution) {
        // resolution can be "1", "5", "15", "30", "60", "D", "W", "M"
        long from = startDate.atStartOfDay().toEpochSecond(java.time.ZoneOffset.UTC);
        long to = LocalDate.now().atStartOfDay().toEpochSecond(java.time.ZoneOffset.UTC);

        String url = String.format(
                "https://finnhub.io/api/v1/stock/candle?symbol=%s&resolution=%s&from=%d&to=%d&token=%s",
                ticker, resolution, from, to, apiKey
        );

        try {
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            if (response != null && "ok".equals(response.get("s"))) {
                List<Map<String, Object>> history = new ArrayList<>();
                List<Long> timestamps = (List<Long>) response.get("t");
                List<Double> closes = (List<Double>) response.get("c");
                for (int i = 0; i < timestamps.size(); i++) {
                    Map<String, Object> record = new HashMap<>();
                    record.put("time", timestamps.get(i));
                    record.put("close", closes.get(i));
                    history.add(record);
                }
                return history;
            }
        } catch (Exception e) {
            return Collections.emptyList();
        }
        return Collections.emptyList();
    }

    // Get historical price of all stocks
    public Map<String, List<Map<String, Object>>> getAllHistoricalPrices(List<String> tickers, LocalDate startDate, String resolution) {
        Map<String, List<Map<String, Object>>> results = new HashMap<>();
        for (String ticker : tickers) {
            results.put(ticker, getHistoricalPrice(ticker, startDate, resolution));
        }
        return results;
    }

    // Validate if this ticker exists and return the stock information
    public Optional<Map<String, Object>> validate(String ticker) {
        String url = String.format("https://finnhub.io/api/v1/quote?symbol=%s&token=%s", ticker, apiKey);
        try {
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            if (response != null && response.get("c") != null) {
                Map<String, Object> result = new HashMap<>();
                result.put("ticker", ticker.toUpperCase());
                result.put("currentPrice", response.get("c"));
                result.put("high", response.get("h"));
                result.put("low", response.get("l"));
                result.put("open", response.get("o"));
                result.put("previousClose", response.get("pc"));
                return Optional.of(result);
            }
        } catch (Exception e) {
            return Optional.empty();
        }
        return Optional.empty();
    }

    // Get the company name of a single stock
    public Optional<String> getCompanyName(String ticker) {
        String url = String.format("https://finnhub.io/api/v1/stock/profile2?symbol=%s&token=%s", ticker, apiKey);
        try {
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            if (response != null && response.get("name") != null) {
                return Optional.of(response.get("name").toString());
            }
        } catch (Exception e) {
            return Optional.empty();
        }
        return Optional.empty();
    }

    // Get the company names of multiple stocks
    public Map<String, String> getAllCompanyNames(List<String> tickers) {
        Map<String, String> results = new HashMap<>();
        for (String ticker : tickers) {
            Optional<String> name = getCompanyName(ticker);
            results.put(ticker, name.orElse(null));
        }
        return results;
    }


}
