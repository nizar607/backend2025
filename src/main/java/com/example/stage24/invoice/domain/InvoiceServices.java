package com.example.stage24.invoice.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import jakarta.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceServices {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String activity;

    private String description;

    private double rate;

    private int hours;

    private double amount;

    public InvoiceServices(String activity, String description, double rate, int hours, double amount) {
        this.activity = activity;
        this.description = description;
        this.rate = rate;
        this.hours = hours;
        this.amount = amount;
    }

}