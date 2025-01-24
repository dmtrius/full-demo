package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalTime;

@Entity(name = "transactions")
@Table(name="transactions")
@Data
@EqualsAndHashCode
public class Transactions {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private Integer clientId;
    @Column
    private String year;
    @Column
    private String month;
    @Column
    private String day;
    @Column
    private LocalTime time;
    @Column
    private Double amount;
    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "client_id")
    @JsonBackReference
    private Clients client;
}
