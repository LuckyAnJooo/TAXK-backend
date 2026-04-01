package com.example.TAXK.demo.dto;

/** header部分显示的数据
 * double totalValue;          // 所有持仓的市值之和
 * double totalCost;           // 所有持仓的成本之和
 * double totalPnl;            // = totalValue - totalCost
 * double totalReturnPercent;  // = totalPnl / totalCost × 100
 * double todayChange;         // 所有持仓的 dayChange 之和
 * double todayChangePercent;  // = todayChange / 昨天总市值 × 100
 * PerformerDto bestPerformer; // 收益率最高的股票
 * PerformerDto worstPerformer;// 收益率最低的股票
 */
public class PortfolioSummaryDto {
    private double totalValue;
    private double totalCost;
    private double totalPnl;
    private double totalReturnPercent;
    private double todayChange;
    private double todayChangePercent;
    private PerformerDto bestPerformer;
    private PerformerDto worstPerformer;

    public PortfolioSummaryDto() {
    }

    public double getTotalValue() {
        return totalValue;
    }

    public void setTotalValue(double totalValue) {
        this.totalValue = totalValue;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }

    public double getTotalPnl() {
        return totalPnl;
    }

    public void setTotalPnl(double totalPnl) {
        this.totalPnl = totalPnl;
    }

    public double getTotalReturnPercent() {
        return totalReturnPercent;
    }

    public void setTotalReturnPercent(double totalReturnPercent) {
        this.totalReturnPercent = totalReturnPercent;
    }

    public double getTodayChange() {
        return todayChange;
    }

    public void setTodayChange(double todayChange) {
        this.todayChange = todayChange;
    }

    public double getTodayChangePercent() {
        return todayChangePercent;
    }

    public void setTodayChangePercent(double todayChangePercent) {
        this.todayChangePercent = todayChangePercent;
    }

    public PerformerDto getBestPerformer() {
        return bestPerformer;
    }

    public void setBestPerformer(PerformerDto bestPerformer) {
        this.bestPerformer = bestPerformer;
    }

    public PerformerDto getWorstPerformer() {
        return worstPerformer;
    }

    public void setWorstPerformer(PerformerDto worstPerformer) {
        this.worstPerformer = worstPerformer;
    }
}
