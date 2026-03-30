package com.example.TAXK.demo.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Holding {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String ticker;

    private String company_name;

    private int quantity;

    private double average_cost;

    private LocalDate first_buy_date;

    private LocalDateTime created_at;

    private LocalDateTime updated_at;

    public Holding() {
    }

    public Holding(Long id, String ticker, String company_name, int quantity, LocalDate first_buy_date, double average_cost, LocalDateTime created_at, LocalDateTime updated_at) {
        this.id = id;
        this.ticker = ticker;
        this.company_name = company_name;
        this.quantity = quantity;
        this.first_buy_date = first_buy_date;
        this.average_cost = average_cost;
        this.created_at = created_at;
        this.updated_at = updated_at;
    }

    public String getCompany_name() {
        return company_name;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getAverage_cost() {
        return average_cost;
    }

    public void setAverage_cost(double average_cost) {
        this.average_cost = average_cost;
    }

    public LocalDate getFirst_buy_date() {
        return first_buy_date;
    }

    public void setFirst_buy_date(LocalDate first_buy_date) {
        this.first_buy_date = first_buy_date;
    }

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public LocalDateTime getCreated_at() {
        return created_at;
    }

    public void setCreated_at(LocalDateTime created_at) {
        this.created_at = created_at;
    }

    public LocalDateTime getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(LocalDateTime updated_at) {
        this.updated_at = updated_at;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
