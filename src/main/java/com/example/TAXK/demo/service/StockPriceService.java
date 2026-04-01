package com.example.TAXK.demo.service;

import com.example.TAXK.demo.dto.NewsDto;
import com.example.TAXK.demo.dto.QuoteData;
import com.example.TAXK.demo.dto.StockSummaryDto;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
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
    public Map<String, Optional<Double>> getAllStocks(List<String> tickers) {
        Map<String, Optional<Double>> results = new HashMap<>();
        for (String ticker : tickers) {
            results.put(ticker, getStockPrice(ticker));
        }
        return results;
    }

    // Get historical price of one single stock (via Yahoo Finance)
    public List<Map<String, Object>> getHistoricalPrice(String ticker, LocalDate startDate, String resolution) {
        // Map Finnhub resolution codes to Yahoo Finance interval strings
        String interval = switch (resolution) {
            case "W" -> "1wk";
            case "M" -> "1mo";
            case "D" -> "1d";
            default  -> "1d";
        };

        long from = startDate.atStartOfDay().toEpochSecond(java.time.ZoneOffset.UTC);
        long to   = LocalDate.now().atStartOfDay().toEpochSecond(java.time.ZoneOffset.UTC);

        String url = String.format(
                "https://query1.finance.yahoo.com/v8/finance/chart/%s?interval=%s&period1=%d&period2=%d",
                ticker, interval, from, to
        );

        // Yahoo Finance blocks requests without a browser-like User-Agent
        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<Map> responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
            Map<String, Object> body = responseEntity.getBody();
            if (body == null) return Collections.emptyList();

            Map<String, Object> chart = (Map<String, Object>) body.get("chart");
            List<Map<String, Object>> results = (List<Map<String, Object>>) chart.get("result");
            if (results == null || results.isEmpty()) return Collections.emptyList();

            Map<String, Object> first = results.get(0);
            List<Number> timestamps = (List<Number>) first.get("timestamp");

            Map<String, Object> indicators = (Map<String, Object>) first.get("indicators");
            List<Map<String, Object>> quoteList = (List<Map<String, Object>>) indicators.get("quote");
            List<Number> closes = (List<Number>) quoteList.get(0).get("close");

            if (timestamps == null || closes == null) return Collections.emptyList();

            List<Map<String, Object>> history = new ArrayList<>();
            for (int i = 0; i < timestamps.size(); i++) {
                Number closeVal = closes.get(i);
                if (closeVal == null) continue; // skip gaps (holidays, etc.)
                Map<String, Object> record = new HashMap<>();
                record.put("time", timestamps.get(i).longValue());
                record.put("close", closeVal.doubleValue());
                history.add(record);
            }
            return history;
        } catch (Exception e) {
            return Collections.emptyList();
        }
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
    public Map<String, Optional<String>> getAllCompanyNames(List<String> tickers) {
        Map<String, Optional<String>> results = new HashMap<>();
        for (String ticker : tickers) {
            results.put(ticker, getCompanyName(ticker));
        }
        return results;
    }

    // Get full quote (current price + previous close) for a single stock
    public Optional<QuoteData> getQuote(String ticker) {
        String url = String.format("https://finnhub.io/api/v1/quote?symbol=%s&token=%s", ticker, apiKey);
        try {
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            if (response != null && response.get("c") != null && response.get("pc") != null) {
                double currentPrice = Double.parseDouble(response.get("c").toString());
                double previousClose = Double.parseDouble(response.get("pc").toString());
                return Optional.of(new QuoteData(currentPrice, previousClose));
            }
        } catch (Exception e) {
            return Optional.empty();
        }
        return Optional.empty();
    }

    // Get full quotes for multiple stocks
    public Map<String, Optional<QuoteData>> getAllQuotes(List<String> tickers) {
        Map<String, Optional<QuoteData>> results = new HashMap<>();
        for (String ticker : tickers) {
            results.put(ticker, getQuote(ticker));
        }
        return results;
    }

    // Get latest general market news
    public List<NewsDto> getNews() {
        String url = String.format("https://finnhub.io/api/v1/news?category=general&token=%s", apiKey);
        try {
            ResponseEntity<List<NewsDto>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<NewsDto>>() {}
            );
            return response.getBody();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    // Get company-specific news
    public List<NewsDto> getCompanyNewsLatest(String ticker) {
        String url = String.format("https://finnhub.io/api/v1/company-news?symbol=%s&token=%s", ticker, apiKey);
        try {
            ResponseEntity<List<NewsDto>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<NewsDto>>() {}
            );
            return response.getBody();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public List<StockSummaryDto> getStockSummaries(List<String> tickers) {
        List<StockSummaryDto> summaries = new ArrayList<>();
        for (String ticker : tickers) {
            Optional<QuoteData> quoteOpt = getQuote(ticker);
            Optional<String> nameOpt = getCompanyName(ticker);

            if (quoteOpt.isPresent()) {
                QuoteData quote = quoteOpt.get();
                String companyName = nameOpt.orElse("Unknown Company");
                summaries.add(new StockSummaryDto(
                        ticker,
                        companyName,
                        quote.getCurrentPrice(),
                        quote.getPreviousClose()
                ));
            }
        }
        return summaries;
    }


}
