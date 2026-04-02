package com.example.TAXK.demo.dto;

import java.util.List;
import java.util.Map;

/** Deserializes the raw JSON returned by the Python /api/recommend endpoint. */
public class AiRecommendRaw {
    private List<String> tickers;
    private Map<String, String> reasons;

    public AiRecommendRaw() {}

    public List<String> getTickers() { return tickers; }
    public void setTickers(List<String> tickers) { this.tickers = tickers; }

    public Map<String, String> getReasons() { return reasons; }
    public void setReasons(Map<String, String> reasons) { this.reasons = reasons; }
}
