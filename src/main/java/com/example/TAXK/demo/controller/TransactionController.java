package com.example.TAXK.demo.controller;

import com.example.TAXK.demo.entity.Holding;
import com.example.TAXK.demo.entity.Transaction;
import com.example.TAXK.demo.repo.HoldingRepo;
import com.example.TAXK.demo.repo.TransactionRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

// sell
@RestController
public class TransactionController {
    @Autowired
    private TransactionRepo transactionRepo;

    @Autowired
    private HoldingRepo holdingRepo;



}