package com.example.TAXK.demo.repository;

import com.example.TAXK.demo.entity.Holding;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HoldingRepository extends JpaRepository<Holding, Long> {
    Optional<Holding> findByTicker(String ticker);
    boolean existsByTicker(String ticker);
}
