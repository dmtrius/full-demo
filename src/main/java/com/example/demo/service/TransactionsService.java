package com.example.demo.service;

import com.example.demo.entity.Clients;
import com.example.demo.entity.Transactions;
import com.example.demo.repository.TransactionsRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class TransactionsService {
    private final TransactionsRepository repository;

    public TransactionsService(TransactionsRepository repository) {
        this.repository = repository;
    }

    public List<Object> testSql1() {
        return repository.testSql1();
    }

    public List<Map<String, Integer>> testSql2() {
        return repository.testSql2();
    }

    public List<String> testSql3() {
        return repository.testSql3();
    }

    @Cacheable(value = "tx", key = "#id")
    public Optional<Transactions> getTransactionById(Long id) {
        return repository.findById(id);
    }

    public List<Transactions> getTransactions() {
        return repository.findAll();
    }

}
