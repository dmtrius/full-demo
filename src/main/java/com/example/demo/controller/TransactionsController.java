package com.example.demo.controller;

import com.example.demo.entity.Clients;
import com.example.demo.entity.Transactions;
import com.example.demo.service.TransactionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class TransactionsController {

    private final TransactionsService service;

    public TransactionsController(TransactionsService service) {
        this.service = service;
    }

    @GetMapping("/transactions/{id}")
    public Transactions getTransactionById(@PathVariable Long id) {
        return service.getTransactionById(id).orElse(null);
    }

    @GetMapping("/transactions/all")
    public List<Transactions> getTransactionById() {
        return service.getTransactions();
    }

    @GetMapping("/test/sql1")
    public List<Object> testSql1() {
        return service.testSql1();
    }

    @GetMapping("/test/sql2")
    public List<Map<String, Integer>> testSql2() {
        return service.testSql2();
    }

    @GetMapping("/test/sql3")
    public List<String> testSql3() {
        return service.testSql3();
    }
}
