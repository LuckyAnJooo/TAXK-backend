package com.example.TAXK.demo.controller;

import com.example.TAXK.demo.entity.Transaction;
import com.example.TAXK.demo.service.PortfolioService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/transactions")
public class TransactionController {

    private final PortfolioService portfolioService;

    public TransactionController(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    @GetMapping
    public List<Transaction> getAllTransactions() {
        return portfolioService.getAllTransactions();
    }
}
