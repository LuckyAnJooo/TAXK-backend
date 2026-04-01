package com.example.TAXK.demo.repo;

import com.example.TAXK.demo.entity.Transaction;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepo extends JpaRepository<Transaction, Long> {
    List<Transaction> findByTickerOrderByTradeDateDesc(String ticker);
}
