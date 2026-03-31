// 路径：/Users/zhangjiahao/vscode project/training/project/TAXK-backend/src/main/java/com/example/TAXK/demo/dto/BuyRequest.java
package com.example.TAXK.demo.dto;

public class BuyRequest {
    private String ticker;
    private int quantity;

    // Getters and Setters
    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}