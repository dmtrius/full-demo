package com.example.demo.service;

import com.example.demo.entity.Clients;
import com.example.demo.repository.ClientsRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

//section for testing client service integration
@SpringBootTest
@ActiveProfiles("test")
class ClientsServiceIntegrationTest {
    @Autowired
    private ClientsService service;
    @MockitoBean
    private ClientsRepository repository;

    // section get by ID
    @Test
    void testGetCustomerById() {
        Clients client = new Clients();
        client.setId(1L);
        client.setClientName("John Snow");
        client.setTransactions(new ArrayList<>());

        repository.save(client);

        Optional<Clients> actualClient = service.getClientById(1L);

        assertTrue(actualClient.isPresent());
        assertEquals(client.getId(), actualClient.get().getId());
        assertEquals(client.getClientName(), actualClient.get().getClientName());
    }
}
