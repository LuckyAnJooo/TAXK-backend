package com.example.TAXK.demo.controller;

import com.example.TAXK.demo.dto.PerformanceResponse;
import com.example.TAXK.demo.dto.PortfolioResponse;
import com.example.TAXK.demo.dto.StockHistoryResponse;
import com.example.TAXK.demo.dto.NewsDto;
import com.example.TAXK.demo.service.PortfolioService;
import com.example.TAXK.demo.service.StockPriceService;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import org.springframework.http.ResponseEntity;

@RestController
@CrossOrigin
@RequestMapping("/api/portfolio")
public class PortfolioController {

    @Autowired
    private final PortfolioService portfolioService;

    @Autowired
    private final StockPriceService stockPriceService;

    public PortfolioController(PortfolioService portfolioService, StockPriceService stockPriceService) {
        this.portfolioService = portfolioService;
        this.stockPriceService = stockPriceService;
    }

    @GetMapping
    public ResponseEntity<PortfolioResponse> getPortfolio() {
        PortfolioResponse response = portfolioService.getPortfolioOverview();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/performance")
    public ResponseEntity<PerformanceResponse> getPerformance() {
        PerformanceResponse response = portfolioService.getPerformanceData();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{ticker}/history")
    public ResponseEntity<StockHistoryResponse> getStockHistory(@PathVariable String ticker) {
        StockHistoryResponse response = portfolioService.getStockHistory(ticker.toUpperCase());
        if (response == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/news")
    public ResponseEntity<List<NewsDto>> getGeneralNews() {
        List<NewsDto> news = stockPriceService.getNews();
        return ResponseEntity.ok(news);
    }

    @GetMapping("/{ticker}/news")
    public ResponseEntity<List<NewsDto>> getCompanyNews(@PathVariable String ticker) {
        List<NewsDto> news = stockPriceService.getCompanyNewsLatest(ticker.toUpperCase());
        return ResponseEntity.ok(news);
    }
    
}
