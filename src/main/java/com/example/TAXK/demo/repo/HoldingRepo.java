package com.example.TAXK.demo.repo;

import com.example.TAXK.demo.entity.Holding;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HoldingRepo extends JpaRepository<Holding, Long> {
    Optional<Holding> findByTicker(String ticker);
}
