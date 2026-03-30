package com.example.TAXK.demo.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class HoldingDto {
    private String ticker;
    private String companyName;
    private int quantity;
    private BigDecimal averageCost;
    private BigDecimal currentPrice;
    private BigDecimal marketValue;
    private BigDecimal costBasis;
    private BigDecimal pnl;
    private BigDecimal returnPercent;
    private BigDecimal dayChange;
    private BigDecimal dayChangePercent;
    private LocalDate firstBuyDate;

    public String getTicker() { return ticker; }
    public void setTicker(String ticker) { this.ticker = ticker; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public BigDecimal getAverageCost() { return averageCost; }
    public void setAverageCost(BigDecimal averageCost) { this.averageCost = averageCost; }

    public BigDecimal getCurrentPrice() { return currentPrice; }
    public void setCurrentPrice(BigDecimal currentPrice) { this.currentPrice = currentPrice; }

    public BigDecimal getMarketValue() { return marketValue; }
    public void setMarketValue(BigDecimal marketValue) { this.marketValue = marketValue; }

    public BigDecimal getCostBasis() { return costBasis; }
    public void setCostBasis(BigDecimal costBasis) { this.costBasis = costBasis; }

    public BigDecimal getPnl() { return pnl; }
    public void setPnl(BigDecimal pnl) { this.pnl = pnl; }

    public BigDecimal getReturnPercent() { return returnPercent; }
    public void setReturnPercent(BigDecimal returnPercent) { this.returnPercent = returnPercent; }

    public BigDecimal getDayChange() { return dayChange; }
    public void setDayChange(BigDecimal dayChange) { this.dayChange = dayChange; }

    public BigDecimal getDayChangePercent() { return dayChangePercent; }
    public void setDayChangePercent(BigDecimal dayChangePercent) { this.dayChangePercent = dayChangePercent; }

    public LocalDate getFirstBuyDate() { return firstBuyDate; }
    public void setFirstBuyDate(LocalDate firstBuyDate) { this.firstBuyDate = firstBuyDate; }
}
