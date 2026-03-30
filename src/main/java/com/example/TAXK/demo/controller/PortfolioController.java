package com.example.TAXK.demo.controller;

import com.example.TAXK.demo.dto.PortfolioResponse;
import com.example.TAXK.demo.dto.TradeRequest;
import com.example.TAXK.demo.service.PortfolioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/portfolio")
public class PortfolioController {

    private final PortfolioService portfolioService;

    public PortfolioController(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    @GetMapping
    public PortfolioResponse getPortfolio() {
        return portfolioService.getPortfolio();
    }

    @PostMapping("/buy")
    public ResponseEntity<?> buy(@RequestBody TradeRequest req) {
        try {
            portfolioService.buy(req);
            return ResponseEntity.ok(portfolioService.getPortfolio());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "INVALID_REQUEST",
                    "message", e.getMessage()
            ));
        }
    }

    @PostMapping("/sell")
    public ResponseEntity<?> sell(@RequestBody TradeRequest req) {
        try {
            portfolioService.sell(req);
            return ResponseEntity.ok(portfolioService.getPortfolio());
        } catch (IllegalArgumentException e) {
            String msg = e.getMessage();
            if (msg != null && msg.startsWith("INSUFFICIENT_HOLDINGS")) {
                return ResponseEntity.badRequest().body(Map.of(
                        "error", "INSUFFICIENT_HOLDINGS",
                        "message", msg.substring("INSUFFICIENT_HOLDINGS: ".length())
                ));
            }
            if (msg != null && msg.startsWith("TICKER_NOT_IN_PORTFOLIO")) {
                return ResponseEntity.status(404).body(Map.of(
                        "error", "TICKER_NOT_IN_PORTFOLIO",
                        "message", msg.substring("TICKER_NOT_IN_PORTFOLIO: ".length())
                ));
            }
            return ResponseEntity.badRequest().body(Map.of("error", "INVALID_REQUEST", "message", msg));
        }
    }

    @DeleteMapping("/{ticker}")
    public ResponseEntity<?> deleteHolding(@PathVariable String ticker) {
        try {
            portfolioService.deleteHolding(ticker);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(Map.of(
                    "error", "NOT_FOUND",
                    "message", e.getMessage()
            ));
        }
    }

    // Stub — Yahoo historical data integration comes in the next sprint
    @GetMapping("/{ticker}/history")
    public ResponseEntity<?> getHistory(@PathVariable String ticker) {
        return ResponseEntity.ok(Map.of(
                "ticker", ticker.toUpperCase(),
                "message", "Historical data endpoint — coming soon"
        ));
    }

    // Stub — multi-stock performance chart data, coming in the next sprint
    @GetMapping("/performance")
    public ResponseEntity<?> getPerformance() {
        return ResponseEntity.ok(Map.of(
                "series", java.util.List.of(),
                "message", "Performance chart data endpoint — coming soon"
        ));
    }
}
