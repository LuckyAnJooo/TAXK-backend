package com.example.TAXK.demo.service;

import com.example.TAXK.demo.entity.Holding;
import com.example.TAXK.demo.entity.Transaction;
import com.example.TAXK.demo.repo.HoldingRepo;
import com.example.TAXK.demo.repo.TransactionRepo;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class PortfolioService {
    public final TransactionRepo transactionRepo;
    public final HoldingRepo holdingRepo;
    public final StockPriceService stockPriceService;

    public PortfolioService(TransactionRepo transactionRepo, HoldingRepo holdingRepo, StockPriceService stockPriceService) {
        this.transactionRepo = transactionRepo;
        this.holdingRepo = holdingRepo;
        this.stockPriceService = stockPriceService;
    }


    // buy

    public void buy(String ticker, int quantity, String note) {
        // Step 1: 查价
        Map<String, Double> stocks = stockPriceService.getAllStocks(List.of(ticker));
        double currentPrice = stocks.get(ticker.toUpperCase());

        // Step 2: 查库
        Optional<Holding> existing = holdingRepo.findByTicker(ticker);

        // 跟新两个表

        // a.存在 -> 更新，b.不存在 -> 新纪录
        if (existing.isPresent()) {
            // Step 3a: 存在
            Holding holding = existing.get();
            double oldQty = holding.getQuantity();
            double oldAvgCost = holding.getAverage_cost();

            double newQty = oldQty + quantity;
            double newAvgCost = (oldQty * oldAvgCost + quantity * currentPrice) / newQty;

            holding.setQuantity((int) newQty);
            holding.setAverage_cost(newAvgCost);
            holding.setUpdated_at(LocalDateTime.now());
            holdingRepo.save(holding);
        } else {
            // Step 3b: 不存在
            Holding newHolding = new Holding();
            newHolding.setTicker(ticker);
            newHolding.setQuantity(quantity);
            newHolding.setAverage_cost(currentPrice);
            newHolding.setFirst_buy_date(LocalDate.now());
            newHolding.setCreated_at(LocalDateTime.now());
            newHolding.setUpdated_at(LocalDateTime.now());
            holdingRepo.save(newHolding);
        }

        // Step 4: 跟新交易表
        Transaction tx = new Transaction();
        tx.setTicker(ticker);
        tx.setNotes(note);
        tx.setType(Transaction.TransactionType.BUY);
        tx.setQuantity(quantity);
        tx.setPrice(currentPrice);
        tx.setTotalAmount(quantity * currentPrice);
        tx.setRealizedPnl(0);
        tx.setTradeDate(LocalDate.now());
        tx.setCreatedAt(LocalDateTime.now());
        transactionRepo.save(tx);
    }

}
