package com.example.demo.service;

import com.example.demo.entity.Clients;
import com.example.demo.repository.ClientsRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientsServiceTest {
    @Mock
    private ClientsRepository repository;

    @InjectMocks
    private ClientsService service;

    @Test
    void testGetClientById() {
        Long id = 1L;
        String name = "John Doe";

        // given
        Clients client = new Clients();
        client.setId(id);
        client.setClientName(name);

        when(repository.findById(id)).thenReturn(Optional.of(client));

        // when
        Optional<Clients> actualClient = service.getClientById(id);

        // then
        assertTrue(actualClient.isPresent());
        assertEquals(client.getId(), actualClient.get().getId());
        assertEquals(client.getClientName(), actualClient.get().getClientName());

        // verify
        verify(repository, times(1)).findById(anyLong());
    }
}
