package com.example.TAXK.demo.dto;

import java.util.List;


/** 展开的折线图的响应格式
 * String ticker;                       // "AAPL"
 * double averageCost;                  // 150.0（你的成本价，前端画水平参考线用）
 * List<StockHistoryPointDto> history;  // 近 1 年的周线数据
 */
public class StockHistoryResponse {
    private String ticker;
    private double averageCost;
    private List<StockHistoryPointDto> history;

    public StockHistoryResponse() {
    }

    public StockHistoryResponse(String ticker, double averageCost, List<StockHistoryPointDto> history) {
        this.ticker = ticker;
        this.averageCost = averageCost;
        this.history = history;
    }

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public double getAverageCost() {
        return averageCost;
    }

    public void setAverageCost(double averageCost) {
        this.averageCost = averageCost;
    }

    public List<StockHistoryPointDto> getHistory() {
        return history;
    }

    public void setHistory(List<StockHistoryPointDto> history) {
        this.history = history;
    }
}
