package com.example.TAXK.demo.repo;

import com.example.TAXK.demo.entity.Holding;
import com.fasterxml.jackson.annotation.JacksonAnnotation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HoldingRepo extends JpaRepository<Holding, Long> {
}
