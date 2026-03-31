package com.example.TAXK.demo.controller;

import com.example.TAXK.demo.entity.Holding;
import com.example.TAXK.demo.repo.HoldingRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@CrossOrigin
public class HoldingController {

    @Autowired
    private HoldingRepo holds;

    @GetMapping("get/holdind")
    public ResponseEntity<List<Holding>> getHolding(){
        System.out.println("get holding");
        return null;
    }



}
