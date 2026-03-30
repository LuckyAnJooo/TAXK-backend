package com.example.TAXK.demo.dto;

import java.math.BigDecimal;

public class StockValidateResponse {
    private boolean valid;
    private String ticker;
    private String name;
    private String exchange;
    private BigDecimal currentPrice;
    private String currency;
    private String message;

    public boolean isValid() { return valid; }
    public void setValid(boolean valid) { this.valid = valid; }

    public String getTicker() { return ticker; }
    public void setTicker(String ticker) { this.ticker = ticker; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getExchange() { return exchange; }
    public void setExchange(String exchange) { this.exchange = exchange; }

    public BigDecimal getCurrentPrice() { return currentPrice; }
    public void setCurrentPrice(BigDecimal currentPrice) { this.currentPrice = currentPrice; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
