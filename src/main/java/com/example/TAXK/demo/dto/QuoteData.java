package com.example.TAXK.demo.dto;

public class QuoteData {
    private double currentPrice;
    private double previousClose;

    public QuoteData() {
    }

    public QuoteData(double currentPrice, double previousClose) {
        this.currentPrice = currentPrice;
        this.previousClose = previousClose;
    }

    public double getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(double currentPrice) {
        this.currentPrice = currentPrice;
    }

    public double getPreviousClose() {
        return previousClose;
    }

    public void setPreviousClose(double previousClose) {
        this.previousClose = previousClose;
    }
}
