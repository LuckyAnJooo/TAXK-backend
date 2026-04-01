package com.example.TAXK.demo.dto;

import java.util.List;

/** GET /api/portfolio — 组合总览 返回格式
 * PortfolioSummaryDto summary;   // header
 * List<HoldingDto> holdings;     // 表格数据 + 饼图数据
 */
public class PortfolioResponse {
    private PortfolioSummaryDto summary;
    private List<HoldingDto> holdings;

    public PortfolioResponse() {
    }

    public PortfolioResponse(PortfolioSummaryDto summary, List<HoldingDto> holdings) {
        this.summary = summary;
        this.holdings = holdings;
    }

    public PortfolioSummaryDto getSummary() {
        return summary;
    }

    public void setSummary(PortfolioSummaryDto summary) {
        this.summary = summary;
    }

    public List<HoldingDto> getHoldings() {
        return holdings;
    }

    public void setHoldings(List<HoldingDto> holdings) {
        this.holdings = holdings;
    }
}
