package com.example.stage24.payment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentConfirmRequest {
    
    @NotBlank(message = "Payment Intent ID is required")
    private String paymentIntentId;
}