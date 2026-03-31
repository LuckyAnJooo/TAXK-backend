package com.example.TAXK.demo.service;

import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.HistoricalQuote;
import yahoofinance.histquotes.Interval;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.Map;

@Service
public class StockPriceService {
    // Get the prices of multiple stocks
    public Map<String, Stock> getAllStocks(List<String> tickers) {
        //TODO
        return new HashMap<String, Stock>();
    }

    // Get historical price of one single stock
    public List<HistoricalQuote> getHistoricalPrice(String ticker, LocalDate nowDate, Interval interval) {
        //TODO
        return new LinkedList<HistoricalQuote>();
    }

    // Validate if this ticket exists and return the information

}
