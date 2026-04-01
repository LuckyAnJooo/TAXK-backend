package com.example.TAXK.demo.service;

import com.example.TAXK.demo.dto.*;
import com.example.TAXK.demo.entity.Holding;
import com.example.TAXK.demo.entity.Transaction;
import com.example.TAXK.demo.repo.HoldingRepo;
import com.example.TAXK.demo.repo.TransactionRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.*;

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

    // portfolio预览
    public PortfolioResponse getPortfolioOverview() {
        List<Holding> holdings = holdingRepo.findAll();

        if (holdings.isEmpty()) {
            PortfolioSummaryDto summary = new PortfolioSummaryDto();
            return new PortfolioResponse(summary, Collections.emptyList());
        }

        // 批量获取实时报价（当前价 + 前收盘价）
        List<String> tickers = holdings.stream().map(Holding::getTicker).toList();
        Map<String, Optional<QuoteData>> quotes = stockPriceService.getAllQuotes(tickers);

        // 计算每个 holding 的基础数据
        List<HoldingDto> holdingDtos = new ArrayList<>();
        double totalValue = 0;
        double totalCost = 0;
        double todayChange = 0;

        for (Holding h : holdings) {
            HoldingDto dto = new HoldingDto();
            dto.setTicker(h.getTicker());
            dto.setCompanyName(h.getCompany_name());
            dto.setQuantity(h.getQuantity());
            dto.setAverageCost(h.getAverage_cost());
            dto.setCostBasis(h.getQuantity() * h.getAverage_cost());
            dto.setFirstBuyDate(h.getFirst_buy_date() != null ? h.getFirst_buy_date().toString() : null);

            Optional<QuoteData> quote = quotes.getOrDefault(h.getTicker(), Optional.empty());
            if (quote.isPresent()) {
                double currentPrice = quote.get().getCurrentPrice();
                double previousClose = quote.get().getPreviousClose();
                double marketValue = h.getQuantity() * currentPrice;
                double pnl = marketValue - dto.getCostBasis();

                dto.setCurrentPrice(currentPrice);
                dto.setMarketValue(marketValue);
                dto.setPnl(pnl);
                dto.setReturnPercent(dto.getCostBasis() != 0 ? (pnl / dto.getCostBasis()) * 100 : 0);
                dto.setDayChange(h.getQuantity() * (currentPrice - previousClose));
                dto.setDayChangePercent(previousClose != 0 ? ((currentPrice - previousClose) / previousClose) * 100 : 0);

                totalValue += marketValue;
                todayChange += dto.getDayChange();
            } else {
                // API 失败：价格相关字段为 null
                dto.setCurrentPrice(null);
                dto.setMarketValue(null);
                dto.setPnl(null);
                dto.setReturnPercent(null);
                dto.setDayChange(null);
                dto.setDayChangePercent(null);
            }

            totalCost += dto.getCostBasis();
            holdingDtos.add(dto);
        }

        // 计算 weight
        for (HoldingDto dto : holdingDtos) {
            if (dto.getMarketValue() != null && totalValue > 0) {
                dto.setWeight((dto.getMarketValue() / totalValue) * 100);
            }
        }

        // 汇总
        PortfolioSummaryDto summary = new PortfolioSummaryDto();
        summary.setTotalValue(totalValue);
        summary.setTotalCost(totalCost);
        summary.setTotalPnl(totalValue - totalCost);
        summary.setTotalReturnPercent(totalCost != 0 ? ((totalValue - totalCost) / totalCost) * 100 : 0);
        summary.setTodayChange(todayChange);
        double yesterdayValue = totalValue - todayChange;
        summary.setTodayChangePercent(yesterdayValue != 0 ? (todayChange / yesterdayValue) * 100 : 0);

        // best / worst performer
        holdingDtos.stream()
                .filter(d -> d.getReturnPercent() != null)
                .max(Comparator.comparingDouble(HoldingDto::getReturnPercent))
                .ifPresent(d -> summary.setBestPerformer(new PerformerDto(d.getTicker(), d.getReturnPercent())));

        holdingDtos.stream()
                .filter(d -> d.getReturnPercent() != null)
                .min(Comparator.comparingDouble(HoldingDto::getReturnPercent))
                .ifPresent(d -> summary.setWorstPerformer(new PerformerDto(d.getTicker(), d.getReturnPercent())));

        return new PortfolioResponse(summary, holdingDtos);
    }

    // 折线图数据
    public PerformanceResponse getPerformanceData() {
        List<Holding> holdings = holdingRepo.findAll();

        if (holdings.isEmpty()) {
            return new PerformanceResponse(Collections.emptyList());
        }

        List<PerformanceSeriesDto> series = new ArrayList<>();

        for (Holding h : holdings) {
            LocalDate startDate = h.getFirst_buy_date() != null ? h.getFirst_buy_date() : LocalDate.now().minusYears(1);
            List<Map<String, Object>> history = stockPriceService.getHistoricalPrice(h.getTicker(), startDate, "W");

            List<PerformancePointDto> points = new ArrayList<>();
            double averageCost = h.getAverage_cost();

            for (Map<String, Object> record : history) {
                long timestamp = ((Number) record.get("time")).longValue();
                double close = ((Number) record.get("close")).doubleValue();
                String date = Instant.ofEpochSecond(timestamp).atZone(ZoneOffset.UTC).toLocalDate().toString();
                double returnPercent = averageCost != 0 ? ((close - averageCost) / averageCost) * 100 : 0;
                points.add(new PerformancePointDto(date, returnPercent));
            }

            series.add(new PerformanceSeriesDto(h.getTicker(), points));
        }

        return new PerformanceResponse(series);
    }

    // 历史动向
    public StockHistoryResponse getStockHistory(String ticker) {
        Optional<Holding> existing = holdingRepo.findByTicker(ticker);
        if (existing.isEmpty()) {
            return null;
        }

        Holding holding = existing.get();
        LocalDate startDate = LocalDate.now().minusYears(1);
        List<Map<String, Object>> history = stockPriceService.getHistoricalPrice(ticker, startDate, "W");

        List<StockHistoryPointDto> points = new ArrayList<>();
        for (Map<String, Object> record : history) {
            long timestamp = ((Number) record.get("time")).longValue();
            double close = ((Number) record.get("close")).doubleValue();
            String date = Instant.ofEpochSecond(timestamp).atZone(ZoneOffset.UTC).toLocalDate().toString();
            points.add(new StockHistoryPointDto(date, close));
        }

        return new StockHistoryResponse(ticker, holding.getAverage_cost(), points);
    }

    // 卖

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
