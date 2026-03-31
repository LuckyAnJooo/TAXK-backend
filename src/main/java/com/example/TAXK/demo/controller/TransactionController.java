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


@RestController
public class TransactionController {
    @Autowired
    private TransactionRepo transactionRepo;

    @Autowired
    private HoldingRepo holdingRepo;

    @GetMapping ("/api/transaction")
    public ResponseEntity<List<Transaction>> getTransaction(){
        List<Transaction> transactionList = transactionRepo.findAll();
        System.out.println(transactionList);
        return ResponseEntity.status(HttpStatus.OK).body(transactionList);
    }

    @PostMapping("/api/portfolio/sell")
    public ResponseEntity<?> sell(@RequestBody SellRequest request) {
        String ticker = request.getTicker() == null ? null : request.getTicker().trim().toUpperCase();
        Optional<Holding> holdingOptional = holdingRepo.findByTicker(ticker);

        if (holdingOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    Map.of(
                            "error", "TICKER_NOT_IN_PORTFOLIO",
                            "message", ticker + " is not in your portfolio"
                    )
            );
        }

        Holding holding = holdingOptional.get();
        if (request.getQuantity() > holding.getQuantity()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    Map.of(
                            "error", "INSUFFICIENT_HOLDINGS",
                            "message", "Cannot sell " + request.getQuantity() + " shares, only holding " + holding.getQuantity()
                    )
            );
        }

        int remainingQuantity = holding.getQuantity() - request.getQuantity();
        if (remainingQuantity == 0) {
            holdingRepo.delete(holding);
        } else {
            holding.setQuantity(remainingQuantity);
            holding.setUpdated_at(LocalDateTime.now());
            holdingRepo.save(holding);
        }

        Transaction transaction = new Transaction();
        transaction.setTicker(ticker);
        transaction.setType(Transaction.TransactionType.SELL);
        transaction.setQuantity(request.getQuantity());
        transaction.setPrice(request.getPrice());
        transaction.setTotalAmount(request.getQuantity() * request.getPrice());
        transaction.setRealizedPnl((request.getPrice() - holding.getAverage_cost()) * request.getQuantity());
        transaction.setTradeDate(request.getTradeDate());
        transaction.setCreatedAt(LocalDateTime.now());
        transactionRepo.save(transaction);

        return ResponseEntity.status(HttpStatus.CREATED).body(transaction);
    }

    public static class SellRequest {
        private String ticker;
        private int quantity;
        private double price;
        private LocalDate tradeDate;

        public String getTicker() {
            return ticker;
        }

        public void setTicker(String ticker) {
            this.ticker = ticker;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }

        public LocalDate getTradeDate() {
            return tradeDate;
        }

        public void setTradeDate(LocalDate tradeDate) {
            this.tradeDate = tradeDate;
        }
    }
}
