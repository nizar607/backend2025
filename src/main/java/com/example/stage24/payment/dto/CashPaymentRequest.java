package com.example.stage24.payment.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CashPaymentRequest {
    
    @NotNull(message = "Order ID is required")
    private Long orderId;
}