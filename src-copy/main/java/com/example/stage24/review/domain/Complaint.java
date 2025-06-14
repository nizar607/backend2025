/*
* -id: int
* -comment: String
* -createdAt: Date
* -userID: int
* -articleID: String
* -status: ReviewStatus
*/
package com.example.stage24.review.domain;

import com.example.stage24.user.domain.User;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

@Data
public class Complaint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String content;

    @ManyToOne(fetch = FetchType.EAGER)
    private User user;

    @NotNull
    private LocalDateTime createdAt = LocalDateTime.now();
}
