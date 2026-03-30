package com.example.TAXK.demo.repository;

import com.example.TAXK.demo.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByTickerOrderByTradeDateDesc(String ticker);
}
