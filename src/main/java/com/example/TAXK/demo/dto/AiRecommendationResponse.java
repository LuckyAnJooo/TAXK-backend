package com.example.TAXK.demo.dto;

import java.util.List;
import java.util.Map;

/** Response returned to the frontend for POST /api/ai/recommend. */
public class AiRecommendationResponse {
    private List<StockSummaryDto> stocks;
    private Map<String, String> reasons;

    public AiRecommendationResponse() {}

    public AiRecommendationResponse(List<StockSummaryDto> stocks, Map<String, String> reasons) {
        this.stocks = stocks;
        this.reasons = reasons;
    }

    public List<StockSummaryDto> getStocks() { return stocks; }
    public void setStocks(List<StockSummaryDto> stocks) { this.stocks = stocks; }

    public Map<String, String> getReasons() { return reasons; }
    public void setReasons(Map<String, String> reasons) { this.reasons = reasons; }
}
