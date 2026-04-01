package com.example.TAXK.demo.dto;

import java.util.List;

/** 折线图上的一条线
 * String ticker;                      // "AAPL"
 * List<PerformancePointDto> data;     // 这条线上的所有点
 */
public class PerformanceSeriesDto {
    private String ticker;
    private List<PerformancePointDto> data;

    public PerformanceSeriesDto() {
    }

    public PerformanceSeriesDto(String ticker, List<PerformancePointDto> data) {
        this.ticker = ticker;
        this.data = data;
    }

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public List<PerformancePointDto> getData() {
        return data;
    }

    public void setData(List<PerformancePointDto> data) {
        this.data = data;
    }
}
