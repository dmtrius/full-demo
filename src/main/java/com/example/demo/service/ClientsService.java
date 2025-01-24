package com.example.demo.service;

import com.example.demo.entity.Clients;
import com.example.demo.repository.ClientsRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Log4j2
public class ClientsService {
    private final ClientsRepository repository;

    public ClientsService(ClientsRepository repository) {
        this.repository = repository;
    }

    public List<Clients> getAllClients() {
        return repository.findAll();
    }

    @Cacheable(value = "client", key = "#id")
    public Optional<Clients> getClientById(Long id) {
        return repository.findById(id);
    }

    @Cacheable(value = "client", key = "#name")
    public Optional<Clients> getClientByName(String name) {
        return repository.findByName(name);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void testTx() {
        log.info("TX1 -- enter");
        repository.testTx();
        testTx2();
        repository.testTx();
        log.info("TX1 -- exit");
    }
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void testTx2() {
        log.info("TX2 -- enter");
        repository.testTx();
        log.info("TX2 -- exit");
    }
}
