package com.example.TAXK.demo.controller;

import com.example.TAXK.demo.entity.Holding;
import com.example.TAXK.demo.repo.HoldingRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@CrossOrigin
public class HoldingController {

    private final PortfolioService portfolioService;

//    @GetMapping("api/holding")
//    public ResponseEntity<List<Holding>> getHolding(){
//        List<Holding> holds = holdsRepo.findAll();
//        System.out.println(holds);
//        return ResponseEntity.status(HttpStatus.OK).body(holds);  // 正确返回数据
//    }

    @PostMapping("api/portfolio")
    public ResponseEntity<> addHolding(@RequestBody Holding holding){
        Holding savedHolding = portfolioService.buy(holding);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedHolding);
    }






}
