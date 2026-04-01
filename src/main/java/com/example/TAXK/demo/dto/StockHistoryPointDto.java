package com.example.TAXK.demo.dto;

/**
 * 展开行折线图上的一个点
 * String date;    // "2025-04-01"
 * double close;   // 172.5（那周收盘价，不是收益率，是实际价格）
 */
public class StockHistoryPointDto {
    private String date;
    private double close;

    public StockHistoryPointDto() {
    }

    public StockHistoryPointDto(String date, double close) {
        this.date = date;
        this.close = close;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getClose() {
        return close;
    }

    public void setClose(double close) {
        this.close = close;
    }
}
