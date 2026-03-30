package com.example.TAXK.demo.service;

import com.example.TAXK.demo.dto.StockValidateResponse;
import org.springframework.stereotype.Service;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;

@Service
public class StockPriceService {

    /**
     * Validates that a ticker exists on Yahoo Finance and returns its info.
     */
    public StockValidateResponse validateTicker(String ticker) {
        StockValidateResponse response = new StockValidateResponse();
        try {
            Stock stock = YahooFinance.get(ticker.toUpperCase());
            if (stock == null || stock.getName() == null || stock.getName().isBlank()) {
                response.setValid(false);
                response.setMessage("Ticker '" + ticker + "' not found on Yahoo Finance.");
                return response;
            }
            response.setValid(true);
            response.setTicker(stock.getSymbol());
            response.setName(stock.getName());
            response.setExchange(stock.getStockExchange());
            response.setCurrency(stock.getCurrency());
            if (stock.getQuote() != null) {
                response.setCurrentPrice(stock.getQuote().getPrice());
            }
        } catch (IOException e) {
            response.setValid(false);
            response.setMessage("Failed to reach Yahoo Finance: " + e.getMessage());
        }
        return response;
    }

    /**
     * Returns the current price for a single ticker.
     */
    public BigDecimal getCurrentPrice(String ticker) {
        try {
            Stock stock = YahooFinance.get(ticker.toUpperCase());
            if (stock != null && stock.getQuote() != null) {
                return stock.getQuote().getPrice();
            }
        } catch (IOException e) {
            // Return null; caller decides how to handle missing price
        }
        return null;
    }

    /**
     * Batch-fetches quotes for multiple tickers in a single API call.
     */
    public Map<String, Stock> getBatchQuotes(String[] tickers) {
        try {
            return YahooFinance.get(tickers);
        } catch (IOException e) {
            return Map.of();
        }
    }
}
