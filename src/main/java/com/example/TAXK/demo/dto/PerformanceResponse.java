package com.example.TAXK.demo.dto;

import java.util.List;

/** 多股走势的返回格式。
 * List<PerformanceSeriesDto> series;  // 所有线（每只股票一条）
 */
public class PerformanceResponse {
    private List<PerformanceSeriesDto> series;

    public PerformanceResponse() {
    }

    public PerformanceResponse(List<PerformanceSeriesDto> series) {
        this.series = series;
    }

    public List<PerformanceSeriesDto> getSeries() {
        return series;
    }

    public void setSeries(List<PerformanceSeriesDto> series) {
        this.series = series;
    }
}
