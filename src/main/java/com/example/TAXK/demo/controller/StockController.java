package com.example.TAXK.demo.controller;

import com.example.TAXK.demo.dto.StockValidateResponse;
import com.example.TAXK.demo.service.StockPriceService;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/stocks")
public class StockController {

    private final StockPriceService stockPriceService;

    public StockController(StockPriceService stockPriceService) {
        this.stockPriceService = stockPriceService;
    }

    @GetMapping("/validate")
    public StockValidateResponse validate(@RequestParam String ticker) {
        return stockPriceService.validateTicker(ticker);
    }
}
