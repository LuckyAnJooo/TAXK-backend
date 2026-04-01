package com.example.TAXK.demo.controller;

import com.example.TAXK.demo.dto.PerformanceResponse;
import com.example.TAXK.demo.dto.PortfolioResponse;
import com.example.TAXK.demo.dto.StockHistoryResponse;
import com.example.TAXK.demo.service.PortfolioService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

@RestController
@CrossOrigin
@RequestMapping("/api/portfolio")
public class PortfolioController {

    private final PortfolioService portfolioService;

    public PortfolioController(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
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
}
