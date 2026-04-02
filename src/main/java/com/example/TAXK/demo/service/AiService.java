package com.example.TAXK.demo.service;

import com.example.TAXK.demo.dto.AiRecommendRaw;
import com.example.TAXK.demo.dto.AiRecommendationResponse;
import com.example.TAXK.demo.dto.ChatRequest;
import com.example.TAXK.demo.dto.ChatResponse;
import com.example.TAXK.demo.dto.StockSummaryDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class AiService {

    @Value("${ai.service.base-url}")
    private String aiBaseUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    private final StockPriceService stockPriceService;

    public AiService(StockPriceService stockPriceService) {
        this.stockPriceService = stockPriceService;
    }

    /**
     * Forward a chat message to the Python AI service.
     * The Python agent will call get_portfolio / get_stock_history tools as needed,
     * then return a portfolio analysis with buy/sell/hold advice.
     */
    public String chat(String message) {
        String url = aiBaseUrl + "/api/chat";
        ChatResponse res = restTemplate.postForObject(url, new ChatRequest(message), ChatResponse.class);
        if (res == null) throw new RuntimeException("No response from AI service");
        return res.getResponse();
    }

    /**
     * Ask the Python AI to recommend stocks.
     * The agent gathers portfolio, history, and news via tools, then returns tickers + reasons.
     * This method then enriches each ticker with live stock data and returns the combined result.
     */
    public AiRecommendationResponse getRecommendation() {
        // 1. Python AI runs its agentic loop and returns {tickers, reasons}
        AiRecommendRaw raw = restTemplate.postForObject(
                aiBaseUrl + "/api/recommend",
                null,
                AiRecommendRaw.class
        );
        if (raw == null || raw.getTickers() == null) {
            throw new RuntimeException("No response from AI service");
        }

        // 2. Fetch live stock details for the recommended tickers
        List<StockSummaryDto> stocks = stockPriceService.getStockSummaries(raw.getTickers());

        // 3. Combine stock details + AI reasons
        return new AiRecommendationResponse(stocks, raw.getReasons());
    }
}
