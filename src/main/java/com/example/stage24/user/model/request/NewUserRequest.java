package com.example.stage24.user.model.request;

import jakarta.validation.constraints.NotBlank;

public class NewUserRequest {

    @NotBlank
    private String username;
}
