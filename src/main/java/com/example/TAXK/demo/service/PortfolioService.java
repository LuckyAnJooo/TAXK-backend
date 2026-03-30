package com.example.TAXK.demo.service;

import com.example.TAXK.demo.dto.HoldingDto;
import com.example.TAXK.demo.dto.PortfolioResponse;
import com.example.TAXK.demo.dto.TradeRequest;
import com.example.TAXK.demo.entity.Holding;
import com.example.TAXK.demo.entity.Transaction;
import com.example.TAXK.demo.repository.HoldingRepository;
import com.example.TAXK.demo.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yahoofinance.Stock;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class PortfolioService {

    private final HoldingRepository holdingRepository;
    private final TransactionRepository transactionRepository;
    private final StockPriceService stockPriceService;

    public PortfolioService(HoldingRepository holdingRepository,
                            TransactionRepository transactionRepository,
                            StockPriceService stockPriceService) {
        this.holdingRepository = holdingRepository;
        this.transactionRepository = transactionRepository;
        this.stockPriceService = stockPriceService;
    }

    /**
     * Returns full portfolio with live prices and calculated P&L.
     */
    public PortfolioResponse getPortfolio() {
        List<Holding> holdings = holdingRepository.findAll();

        // Batch-fetch live prices for all tickers at once
        Map<String, Stock> quotes = Map.of();
        if (!holdings.isEmpty()) {
            String[] tickers = holdings.stream().map(Holding::getTicker).toArray(String[]::new);
            quotes = stockPriceService.getBatchQuotes(tickers);
        }

        BigDecimal totalValue = BigDecimal.ZERO;
        BigDecimal totalCostBasis = BigDecimal.ZERO;
        BigDecimal totalDayChange = BigDecimal.ZERO;

        List<HoldingDto> holdingDtos = new ArrayList<>();
        for (Holding h : holdings) {
            HoldingDto dto = new HoldingDto();
            dto.setTicker(h.getTicker());
            dto.setCompanyName(h.getCompanyName());
            dto.setQuantity(h.getQuantity());
            dto.setAverageCost(h.getAverageCost());
            dto.setFirstBuyDate(h.getFirstBuyDate());

            BigDecimal qty = BigDecimal.valueOf(h.getQuantity());
            BigDecimal costBasis = h.getAverageCost().multiply(qty);
            dto.setCostBasis(costBasis);

            Stock stock = quotes.get(h.getTicker());
            BigDecimal currentPrice = (stock != null && stock.getQuote() != null)
                    ? stock.getQuote().getPrice()
                    : h.getAverageCost(); // fallback to cost if price unavailable

            dto.setCurrentPrice(currentPrice);

            BigDecimal marketValue = currentPrice.multiply(qty);
            dto.setMarketValue(marketValue);

            BigDecimal pnl = marketValue.subtract(costBasis);
            dto.setPnl(pnl);

            BigDecimal returnPct = costBasis.compareTo(BigDecimal.ZERO) != 0
                    ? pnl.divide(costBasis, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100))
                    : BigDecimal.ZERO;
            dto.setReturnPercent(returnPct.setScale(2, RoundingMode.HALF_UP));

            BigDecimal dayChangePriceUnit = (stock != null && stock.getQuote() != null && stock.getQuote().getChange() != null)
                    ? stock.getQuote().getChange()
                    : BigDecimal.ZERO;
            BigDecimal dayChange = dayChangePriceUnit.multiply(qty);
            dto.setDayChange(dayChange);

            BigDecimal dayChangePct = (stock != null && stock.getQuote() != null && stock.getQuote().getChangeInPercent() != null)
                    ? stock.getQuote().getChangeInPercent()
                    : BigDecimal.ZERO;
            dto.setDayChangePercent(dayChangePct);

            totalValue = totalValue.add(marketValue);
            totalCostBasis = totalCostBasis.add(costBasis);
            totalDayChange = totalDayChange.add(dayChange);

            holdingDtos.add(dto);
        }

        PortfolioResponse.Summary summary = new PortfolioResponse.Summary();
        summary.setTotalValue(totalValue.setScale(2, RoundingMode.HALF_UP));
        BigDecimal totalPnl = totalValue.subtract(totalCostBasis);
        summary.setTotalPnl(totalPnl.setScale(2, RoundingMode.HALF_UP));
        BigDecimal totalReturn = totalCostBasis.compareTo(BigDecimal.ZERO) != 0
                ? totalPnl.divide(totalCostBasis, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100))
                : BigDecimal.ZERO;
        summary.setTotalReturnPercent(totalReturn.setScale(2, RoundingMode.HALF_UP));
        summary.setTodayChange(totalDayChange.setScale(2, RoundingMode.HALF_UP));
        BigDecimal todayChangePct = totalValue.subtract(totalDayChange).compareTo(BigDecimal.ZERO) != 0
                ? totalDayChange.divide(totalValue.subtract(totalDayChange), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100))
                : BigDecimal.ZERO;
        summary.setTodayChangePercent(todayChangePct.setScale(2, RoundingMode.HALF_UP));

        PortfolioResponse response = new PortfolioResponse();
        response.setSummary(summary);
        response.setHoldings(holdingDtos);
        return response;
    }

    /**
     * Records a buy transaction and updates (or creates) the holding.
     * Uses weighted average cost method per new plan.md §4.2.
     */
    @Transactional
    public void buy(TradeRequest req) {
        if (req.getQuantity() <= 0) {
            throw new IllegalArgumentException("Quantity must be a positive integer");
        }

        BigDecimal buyQty = BigDecimal.valueOf(req.getQuantity());
        BigDecimal buyPrice = req.getPrice();
        BigDecimal totalAmount = buyPrice.multiply(buyQty);

        // Write transaction record
        Transaction tx = new Transaction();
        tx.setTicker(req.getTicker().toUpperCase());
        tx.setType(Transaction.TransactionType.BUY);
        tx.setQuantity(req.getQuantity());
        tx.setPrice(buyPrice);
        tx.setTotalAmount(totalAmount);
        tx.setTradeDate(req.getTradeDate());
        tx.setNotes(req.getNotes());
        transactionRepository.save(tx);

        // Update or create holding
        Optional<Holding> existingOpt = holdingRepository.findByTicker(req.getTicker().toUpperCase());
        if (existingOpt.isPresent()) {
            Holding existing = existingOpt.get();
            BigDecimal oldQty = BigDecimal.valueOf(existing.getQuantity());
            BigDecimal newAvgCost = existing.getAverageCost().multiply(oldQty)
                    .add(buyPrice.multiply(buyQty))
                    .divide(oldQty.add(buyQty), 4, RoundingMode.HALF_UP);
            existing.setAverageCost(newAvgCost);
            existing.setQuantity(existing.getQuantity() + req.getQuantity());
            holdingRepository.save(existing);
        } else {
            Holding holding = new Holding();
            holding.setTicker(req.getTicker().toUpperCase());
            holding.setQuantity(req.getQuantity());
            holding.setAverageCost(buyPrice.setScale(4, RoundingMode.HALF_UP));
            holding.setFirstBuyDate(req.getTradeDate());
            // Company name will be populated via Yahoo Finance validate flow; set empty for now
            holdingRepository.save(holding);
        }
    }

    /**
     * Records a sell transaction and decrements (or removes) the holding.
     */
    @Transactional
    public void sell(TradeRequest req) {
        Holding holding = holdingRepository.findByTicker(req.getTicker().toUpperCase())
                .orElseThrow(() -> new IllegalArgumentException("TICKER_NOT_IN_PORTFOLIO: " + req.getTicker() + " is not in your portfolio"));

        if (req.getQuantity() > holding.getQuantity()) {
            throw new IllegalArgumentException("INSUFFICIENT_HOLDINGS: Cannot sell " + req.getQuantity()
                    + " shares, only holding " + holding.getQuantity());
        }

        BigDecimal sellQty = BigDecimal.valueOf(req.getQuantity());
        BigDecimal sellPrice = req.getPrice();
        BigDecimal totalAmount = sellPrice.multiply(sellQty);
        BigDecimal realizedPnl = sellPrice.subtract(holding.getAverageCost()).multiply(sellQty);

        // Write transaction record
        Transaction tx = new Transaction();
        tx.setTicker(req.getTicker().toUpperCase());
        tx.setType(Transaction.TransactionType.SELL);
        tx.setQuantity(req.getQuantity());
        tx.setPrice(sellPrice);
        tx.setTotalAmount(totalAmount);
        tx.setRealizedPnl(realizedPnl.setScale(4, RoundingMode.HALF_UP));
        tx.setTradeDate(req.getTradeDate());
        tx.setNotes(req.getNotes());
        transactionRepository.save(tx);

        // Update or delete holding
        int newQty = holding.getQuantity() - req.getQuantity();
        if (newQty == 0) {
            holdingRepository.delete(holding);
        } else {
            holding.setQuantity(newQty);
            holdingRepository.save(holding);
        }
    }

    /**
     * Force-deletes a holding and its associated transactions (correction use).
     */
    @Transactional
    public void deleteHolding(String ticker) {
        Holding holding = holdingRepository.findByTicker(ticker.toUpperCase())
                .orElseThrow(() -> new IllegalArgumentException("Holding not found: " + ticker));
        holdingRepository.delete(holding);
    }

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }
}
