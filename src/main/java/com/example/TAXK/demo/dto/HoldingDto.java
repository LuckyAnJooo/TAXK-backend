package com.example.TAXK.demo.dto;

/**
 * 表格中的一行（持仓详情）
 */
public class HoldingDto {

    // holding表
    private String ticker;
    private String companyName;
    private int quantity;
    private double averageCost;
    private String firstBuyDate;

    // 调用finnhub计算
    private Double currentPrice;
    private Double marketValue; // 数量 * 当前价格
    private Double pnl; // 市值 - 成本
    private Double returnPercent; // pnl / 成本
    private Double dayChange; // 持有 * （现价 - 闭盘）
    private Double dayChangePercent; // （现价 - 闭盘） / 闭盘
    private Double weight; // 占比，画饼图

    // 数据库计算
    private double costBasis; // 持有 * avgCost

    public HoldingDto() {
    }

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getAverageCost() {
        return averageCost;
    }

    public void setAverageCost(double averageCost) {
        this.averageCost = averageCost;
    }

    public Double getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(Double currentPrice) {
        this.currentPrice = currentPrice;
    }

    public Double getMarketValue() {
        return marketValue;
    }

    public void setMarketValue(Double marketValue) {
        this.marketValue = marketValue;
    }

    public double getCostBasis() {
        return costBasis;
    }

    public void setCostBasis(double costBasis) {
        this.costBasis = costBasis;
    }

    public Double getPnl() {
        return pnl;
    }

    public void setPnl(Double pnl) {
        this.pnl = pnl;
    }

    public Double getReturnPercent() {
        return returnPercent;
    }

    public void setReturnPercent(Double returnPercent) {
        this.returnPercent = returnPercent;
    }

    public Double getDayChange() {
        return dayChange;
    }

    public void setDayChange(Double dayChange) {
        this.dayChange = dayChange;
    }

    public Double getDayChangePercent() {
        return dayChangePercent;
    }

    public void setDayChangePercent(Double dayChangePercent) {
        this.dayChangePercent = dayChangePercent;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public String getFirstBuyDate() {
        return firstBuyDate;
    }

    public void setFirstBuyDate(String firstBuyDate) {
        this.firstBuyDate = firstBuyDate;
    }
}
