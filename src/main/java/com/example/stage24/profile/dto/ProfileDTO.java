package com.example.stage24.profile.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileDTO {
    
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String address;
    private String image;
    private String profileUrl;
    private boolean enabled;
    private boolean subscribed;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}