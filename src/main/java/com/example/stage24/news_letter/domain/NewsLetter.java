/*
 * - id: int
 * - title: String
 * - content: String
 * - createdAt: LocalDateTime
 * - senderEmail: String
 */
package com.example.stage24.news_letter.domain;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;

import jakarta.validation.constraints.NotNull;
import jakarta.persistence.*;
import lombok.Data;

@Data
public class NewsLetter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String title;

    @NotNull
    private String content;

    @NotNull
    private String senderEmail;

    @NotNull
    private LocalDateTime createdAt = LocalDateTime.now();

    @NotNull
    private LocalDateTime updatedAt = LocalDateTime.now();

}
