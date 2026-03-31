package com.example.TAXK.demo.service;

import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.HistoricalQuote;
import yahoofinance.histquotes.Interval;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.Map;

@Service
public class StockPriceService {
    // get the price of a stock
    public Optional<Double> getStockPrice(String ticker) {
        Optional<Map<String, Object>> result = validate(ticker);
        if(result.isPresent()) {
            Map<String, Object> data = result.get();
            return Optional.of((double) data.get("currentPrice"));
        }
        return Optional.empty();
    }
    // Get the prices of multiple stocks
    public Map<String, Stock> getAllStocks(List<String> tickers) {
        try {
            String arr[] = tickers.toArray(new String[0]);
            Map<String, Stock> results = YahooFinance.get(arr);
            return results;
        } catch (IOException e){
            return Collections.emptyMap();
        }
    }

    // Get historical price of one single stock
    public List<HistoricalQuote> getHistoricalPrice(String ticker, LocalDate startDate, Interval interval) {
         try {
             Calendar toCal = Calendar.getInstance(); //today
             Calendar fromCal = Calendar.getInstance();
             fromCal.set(startDate.getYear(), startDate.getMonthValue() - 1, startDate.getDayOfMonth());
             Stock stock = YahooFinance.get(ticker, fromCal, toCal, interval);
             return stock.getHistory();
         } catch(IOException e) {
             return Collections.emptyList();
         }
    }

    // Get historical price of all stocks
    public Map<String, List<HistoricalQuote>> getAllHistoricalPrices(List<String> tickers, LocalDate startDate, Interval interval) {
        Map<String, List<HistoricalQuote>> results = new HashMap<>();
        Calendar toCal = Calendar.getInstance(); //today
        Calendar fromCal = Calendar.getInstance();
        fromCal.set(startDate.getYear(), startDate.getMonthValue() - 1, startDate.getDayOfMonth());
        for(String ticker: tickers) {
            try {
                Stock stock = YahooFinance.get(ticker, fromCal, toCal, interval);
                List<HistoricalQuote> history = stock.getHistory();
                results.put(ticker, history);
            } catch(IOException e) {
                results.put(ticker, Collections.emptyList());
            }
        }
        return results;
    }

    // Validate if this ticket exists and return the stock information
    public Optional<Map<String, Object>> validate(String ticker) {
         try {
             Stock stock = YahooFinance.get(ticker.toUpperCase());
             if(stock == null || stock.getQuote().getPrice() == null) {
                 //doesn't exist
                 return Optional.empty();
             }
             Map<String, Object> result = new HashMap<String, Object>();
             result.put("ticker", stock.getSymbol());
             result.put("name", stock.getName());
             result.put("exchange", stock.getStockExchange());
             result.put("currentPrice", stock.getQuote().getPrice());
             result.put("currency", stock.getCurrency());
             return Optional.of(result);
         } catch(IOException e) {
             return Optional.empty();
        }
    }
}
