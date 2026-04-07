// Done

package com.example.TAXK.demo.controller;

import com.example.TAXK.demo.service.PortfolioService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.TAXK.demo.dto.TransactionRequest; // 导入DTO类


@RestController
@CrossOrigin
public class HoldingController {
    
    @Autowired
    private final PortfolioService portfolioService;

    public HoldingController(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }


//    @PostMapping("/api/portfolio/buy")
//    public ResponseEntity<Void> addHolding(@RequestBody TransactionRequest transactionRequest){
//        portfolioService.buy(transactionRequest.getTicker(), transactionRequest.getQuantity(), transactionRequest.getNote());
//        System.out.println(transactionRequest.getTicker() + " " + transactionRequest.getQuantity());
//        return ResponseEntity.status(HttpStatus.CREATED).body(null);
//    }
    // practice Kara
    @PostMapping("/api/portfolio/buy")
    public ResponseEntity<Void> addholdings (@RequestBody TransactionRequest transactionRequest){
        portfolioService.buy(transactionRequest.getTicker(),transactionRequest.getQuantity(),transactionRequest.getNote());
        return ResponseEntity.status(HttpStatus.CREATED).body(null);
    }

}
