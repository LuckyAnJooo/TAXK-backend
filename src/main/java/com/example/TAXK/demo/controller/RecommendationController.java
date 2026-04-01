package com.example.TAXK.demo.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.TAXK.demo.dto.StockSummaryDto;
import com.example.TAXK.demo.service.StockPriceService;

@RestController
@CrossOrigin
@RequestMapping("/api/recommendation")
public class RecommendationController {
    @Autowired
    private final StockPriceService stockPriceService;

    public RecommendationController(StockPriceService stockPriceService) {
        this.stockPriceService = stockPriceService;
    }
    
    @GetMapping("/information")
    public List<StockSummaryDto> getRecommendationInformation(
            @RequestParam(name = "tickers") String tickers) {
        List<String> tickerList = Arrays.asList(tickers.split(","));
        return stockPriceService.getStockSummaries(tickerList);
    }
}
