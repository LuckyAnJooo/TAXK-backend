package com.example.TAXK.demo.dto;

import java.math.BigDecimal;
import java.util.List;

public class PortfolioResponse {

    public static class Summary {
        private BigDecimal totalValue;
        private BigDecimal totalPnl;
        private BigDecimal totalReturnPercent;
        private BigDecimal todayChange;
        private BigDecimal todayChangePercent;

        public BigDecimal getTotalValue() { return totalValue; }
        public void setTotalValue(BigDecimal totalValue) { this.totalValue = totalValue; }

        public BigDecimal getTotalPnl() { return totalPnl; }
        public void setTotalPnl(BigDecimal totalPnl) { this.totalPnl = totalPnl; }

        public BigDecimal getTotalReturnPercent() { return totalReturnPercent; }
        public void setTotalReturnPercent(BigDecimal totalReturnPercent) { this.totalReturnPercent = totalReturnPercent; }

        public BigDecimal getTodayChange() { return todayChange; }
        public void setTodayChange(BigDecimal todayChange) { this.todayChange = todayChange; }

        public BigDecimal getTodayChangePercent() { return todayChangePercent; }
        public void setTodayChangePercent(BigDecimal todayChangePercent) { this.todayChangePercent = todayChangePercent; }
    }

    private Summary summary;
    private List<HoldingDto> holdings;

    public Summary getSummary() { return summary; }
    public void setSummary(Summary summary) { this.summary = summary; }

    public List<HoldingDto> getHoldings() { return holdings; }
    public void setHoldings(List<HoldingDto> holdings) { this.holdings = holdings; }
}
