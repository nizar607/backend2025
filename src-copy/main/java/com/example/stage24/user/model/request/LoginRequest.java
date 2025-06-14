package com.example.stage24.user.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LoginRequest {
	@NotBlank(message = "email  is required")
	private String email;
	@NotBlank
	private String password;


}
