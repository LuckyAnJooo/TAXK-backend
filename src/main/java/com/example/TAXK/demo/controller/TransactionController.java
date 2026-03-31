package com.example.TAXK.demo.controller;

import com.example.TAXK.demo.entity.Transaction;
import com.example.TAXK.demo.repo.TransactionRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
public class TransactionController {
    @Autowired
    private TransactionRepo transactionRepo;

    @GetMapping ("/api/transaction")
    public ResponseEntity<List<Transaction>> getTransaction(){
        List<Transaction> transactionList = transactionRepo.findAll();
        System.out.println(transactionList);
        return ResponseEntity.status(HttpStatus.OK).body(transactionList);
    }

}
