package com.example.TAXK.demo.dto;

/** 一条线上的某个点
 * String date;            // "2024-01-22"（哪一周）
 * double returnPercent;   // 3.33（那周的累计收益率） 是收益率，不是价格
 */
public class PerformancePointDto {
    private String date;
    private double returnPercent;

    public PerformancePointDto() {
    }

    public PerformancePointDto(String date, double returnPercent) {
        this.date = date;
        this.returnPercent = returnPercent;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getReturnPercent() {
        return returnPercent;
    }

    public void setReturnPercent(double returnPercent) {
        this.returnPercent = returnPercent;
    }
}
