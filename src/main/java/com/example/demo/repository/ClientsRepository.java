package com.example.demo.repository;

import com.example.demo.entity.Clients;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientsRepository extends JpaRepository<Clients, Long> {
//    @Query("select c from Clients c where c.clientName = :name")
    @Query(value = "select * from clients where client_name = :name", nativeQuery = true)
    Optional<Clients> findByName(String name);

    @Query(value = "select 1", nativeQuery = true)
    Integer testTx();
}
