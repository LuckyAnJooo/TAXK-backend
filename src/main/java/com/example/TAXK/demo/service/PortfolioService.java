package com.example.TAXK.demo.service;

import com.example.TAXK.demo.entity.Holding;
import com.example.TAXK.demo.entity.Transaction;
import com.example.TAXK.demo.repo.HoldingRepo;
import com.example.TAXK.demo.repo.TransactionRepo;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class PortfolioService {
    @Autowired
    public final TransactionRepo transactionRepo;

    @Autowired
    public final HoldingRepo holdingRepo;

    @Autowired
    public final StockPriceService stockPriceService;

    public PortfolioService(TransactionRepo transactionRepo, HoldingRepo holdingRepo, StockPriceService stockPriceService) {
        this.transactionRepo = transactionRepo;
        this.holdingRepo = holdingRepo;
        this.stockPriceService = stockPriceService;
    }


    // buy

    @Transactional
    public boolean buy(String ticker, int quantity, String note) {

        // Step 0: 校验
        Optional<Map<String, Object>> stock = stockPriceService.validate(ticker);

        if(stock.isEmpty()){
            return false;
        }


        // Step 1: 查价
        Optional<Double> currentPriceExist = stockPriceService.getStockPrice(ticker);

        double currentPrice = currentPriceExist.get();
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
            Optional<String> companyName = stockPriceService.getCompanyName(ticker);
            if(companyName.isEmpty()){
                return false;
            }

            Holding newHolding = new Holding();
            newHolding.setTicker(ticker);
            newHolding.setQuantity(quantity);
            newHolding.setAverage_cost(currentPrice);
            newHolding.setFirst_buy_date(LocalDate.now());
            newHolding.setCreated_at(LocalDateTime.now());
            newHolding.setUpdated_at(LocalDateTime.now());
            newHolding.setCompany_name(companyName.get());
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
        return true;
    }

    @Transactional
    public boolean sell(String ticker, int quantity, String note) {

        // Step 0: 查库，没有持仓直接拒绝
        Optional<Holding> existing = holdingRepo.findByTicker(ticker);
        if (existing.isEmpty()) {
            return false;
        }

        Holding holding = existing.get();
        int oldQty = holding.getQuantity();

        // Step 1: 校验数量，卖出不能超过持有
        if (quantity > oldQty) {
            return false;
        }

        // Step 2: 查实时价格
        Optional<Double> currentPriceExist = stockPriceService.getStockPrice(ticker);
        if (currentPriceExist.isEmpty()) {
            return false;
        }
        double currentPrice = currentPriceExist.get();

        // Step 3: 更新 holding
        if (quantity == oldQty) {
            // 全部卖出 → 删除持仓记录
            holdingRepo.delete(holding);
        } else {
            // 部分卖出 → 减少数量，均价不变
            holding.setQuantity(oldQty - quantity);
            holding.setUpdated_at(LocalDateTime.now());
            holdingRepo.save(holding);
        }

        // Step 4: 计算已实现盈亏 = (卖出价 - 均价) × 数量
        double realizedPnl = (currentPrice - holding.getAverage_cost()) * quantity;

        // Step 5: 记录交易
        Transaction tx = new Transaction();
        tx.setTicker(ticker);
        tx.setNotes(note);
        tx.setType(Transaction.TransactionType.SELL);
        tx.setQuantity(quantity);
        tx.setPrice(currentPrice);
        tx.setTotalAmount(quantity * currentPrice);
        tx.setRealizedPnl(realizedPnl);
        tx.setTradeDate(LocalDate.now());
        tx.setCreatedAt(LocalDateTime.now());
        transactionRepo.save(tx);
        return true;
    }


}
