package com.example.stage24.calendar.domain;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.data.annotation.Id;


import java.time.LocalDateTime;

import jakarta.persistence.*;

@Data
public class Calendar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String title;
    @NotBlank
    private LocalDateTime start;
    @NotBlank
    private LocalDateTime end;
    @NotBlank
    private String description;

}
