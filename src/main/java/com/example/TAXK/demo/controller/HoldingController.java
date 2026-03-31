package com.example.TAXK.demo.controller;

import com.example.TAXK.demo.entity.Holding;
import com.example.TAXK.demo.repo.HoldingRepo;
import com.example.TAXK.demo.service.PortfolioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.TAXK.demo.dto.BuyRequest; // 导入DTO类

import java.util.List;


@RestController
@CrossOrigin
public class HoldingController {

    private final PortfolioService portfolioService;

    public HoldingController(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

//    @GetMapping("api/holding")
//    public ResponseEntity<List<Holding>> getHolding(){
//        List<Holding> holds = holdsRepo.findAll();
//        System.out.println(holds);
//        return ResponseEntity.status(HttpStatus.OK).body(holds);  // 正确返回数据
//    }

    @PostMapping("/api/portfolio")
    public ResponseEntity<Void> addHolding(@RequestBody BuyRequest buyRequest){
        portfolioService.buy(buyRequest.getTicker(), buyRequest.getQuantity());
        System.out.println(buyRequest.getTicker() + " " + buyRequest.getQuantity());
        return ResponseEntity.status(HttpStatus.CREATED).body(null);
    }






}
