package com.example.TAXK.demo.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class TradeRequest {
    private String ticker;
    private int quantity;
    private BigDecimal price;
    private LocalDate tradeDate;
    private String notes;

    public String getTicker() { return ticker; }
    public void setTicker(String ticker) { this.ticker = ticker; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public LocalDate getTradeDate() { return tradeDate; }
    public void setTradeDate(LocalDate tradeDate) { this.tradeDate = tradeDate; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
