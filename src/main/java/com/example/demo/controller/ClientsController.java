package com.example.demo.controller;

import com.example.demo.entity.Clients;
import com.example.demo.service.ClientsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ClientsController {

    private final ClientsService service;

    public ClientsController(ClientsService service) {
        this.service = service;
    }

    @GetMapping("/clients/all")
    public List<Clients> getAllClients() {
        return service.getAllClients();
    }

    @GetMapping("/clients/{id}")
    public Clients getClientById(@PathVariable Long id) {
        return service.getClientById(id).orElse(null);
    }

    @GetMapping("/clients/name/{name}")
    public Clients getClientByName(@PathVariable String name) {
        return service.getClientByName(name).orElse(null);
    }

    @GetMapping("/testTx")
    public void testTx() {
        service.testTx();
    }
}
