package com.example.TAXK.demo.dto;


// 用在 summary 里的 bestPerformer 和 worstPerformer。
public class PerformerDto {
    private String ticker;
    private double returnPercent;

    public PerformerDto() {
    }

    public PerformerDto(String ticker, double returnPercent) {
        this.ticker = ticker;
        this.returnPercent = returnPercent;
    }

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public double getReturnPercent() {
        return returnPercent;
    }

    public void setReturnPercent(double returnPercent) {
        this.returnPercent = returnPercent;
    }
}
