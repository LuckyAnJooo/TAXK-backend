package com.example.TAXK.demo.controller;

import com.example.TAXK.demo.dto.TransactionRequest;
import com.example.TAXK.demo.entity.Holding;
import com.example.TAXK.demo.entity.Transaction;
import com.example.TAXK.demo.repo.HoldingRepo;
import com.example.TAXK.demo.repo.TransactionRepo;
import com.example.TAXK.demo.service.PortfolioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

// sell controller
@RestController
@CrossOrigin
public class TransactionController {

    private final PortfolioService portfolioService;

    public TransactionController (PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

//    @GetMapping("api/holding")
//    public ResponseEntity<List<Holding>> getHolding(){
//        List<Holding> holds = holdsRepo.findAll();
//        System.out.println(holds);
//        return ResponseEntity.status(HttpStatus.OK).body(holds);  // 正确返回数据
//    }

    @PostMapping("/api/portfolio/sell")
    public ResponseEntity<Void> addHolding(@RequestBody TransactionRequest transactionRequest){
        portfolioService.sell(transactionRequest.getTicker(), transactionRequest.getQuantity(), transactionRequest.getNote());
        System.out.println(transactionRequest.getTicker() + " " + transactionRequest.getQuantity());
        return ResponseEntity.status(HttpStatus.CREATED).body(null);
    }



}