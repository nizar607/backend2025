package com.example.stage24.profile.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileDTO {
    
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String address;
    private String password; // Optional - only update if provided
    private String image; // Optional - only update if provided
}