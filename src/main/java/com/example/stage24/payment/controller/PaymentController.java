package com.example.stage24.payment.controller;

import com.example.stage24.order.domain.Order;
import com.example.stage24.order.repository.OrderRepository;
import com.example.stage24.payment.domain.Payment;
import com.example.stage24.payment.dto.CashPaymentRequest;
import com.example.stage24.payment.dto.PaymentConfirmRequest;
import com.example.stage24.payment.dto.PaymentIntentRequest;
import com.example.stage24.payment.service.StripePaymentService;
import com.stripe.exception.StripeException;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments")
public class PaymentController {

    private StripePaymentService stripePaymentService;

    private OrderRepository orderRepository;

    /**
     * Create Stripe payment intent
     */
    @PostMapping("/stripe/create-intent")
    public ResponseEntity<?> createStripePaymentIntent(@Valid @RequestBody PaymentIntentRequest request) {
        try {
            Long orderId = request.getOrderId();
            String customerEmail = request.getCustomerEmail();

            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new RuntimeException("Order not found"));

            Map<String, Object> response = stripePaymentService.createPaymentIntent(order, customerEmail);
            
            return ResponseEntity.ok(response);
        } catch (StripeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to create payment intent: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Internal server error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Confirm Stripe payment
     */
    @PostMapping("/stripe/confirm")
    public ResponseEntity<?> confirmStripePayment(@Valid @RequestBody PaymentConfirmRequest request) {
        try {
            String paymentIntentId = request.getPaymentIntentId();
            
            boolean success = stripePaymentService.confirmPayment(paymentIntentId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", success);
            response.put("message", success ? "Payment confirmed successfully" : "Payment confirmation failed");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to confirm payment: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Process cash payment (creates pending payment)
     */
    @PostMapping("/cash/create")
    public ResponseEntity<?> createCashPayment(@Valid @RequestBody CashPaymentRequest request) {
        try {
            Long orderId = request.getOrderId();

            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new RuntimeException("Order not found"));

            Payment payment = stripePaymentService.processCashPayment(order);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Cash payment created. Awaiting admin confirmation.");
            response.put("paymentId", payment.getId());
            response.put("paymentReference", payment.getPaymentReference());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to create cash payment: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Admin confirms cash payment
     */
    @PostMapping("/cash/confirm/{paymentId}")
    public ResponseEntity<?> confirmCashPayment(@PathVariable Long paymentId) {
        try {
            boolean success = stripePaymentService.confirmCashPayment(paymentId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", success);
            response.put("message", success ? "Cash payment confirmed successfully" : "Failed to confirm cash payment");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to confirm cash payment: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Get test card numbers for development
     */
    @GetMapping("/test-cards")
    public ResponseEntity<?> getTestCards() {
        System.out.println("working here");
        Map<String, String> testCards = stripePaymentService.getTestCards();
        
        Map<String, Object> response = new HashMap<>();
        response.put("testCards", testCards);
        response.put("instructions", Map.of(
            "expiry", "12/25",
            "cvc", "123",
            "zip", "12345"
        ));
        
        return ResponseEntity.ok(response);
    }

    //hello world get
    @GetMapping("/hello")
    public ResponseEntity<?> getHello() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "hello world");
        return ResponseEntity.ok(response);
    }

    /**
     * Webhook endpoint for Stripe events (optional)
     */
    @PostMapping("/stripe/webhook")
    public ResponseEntity<String> handleStripeWebhook(@RequestBody String payload, 
                                                     @RequestHeader("Stripe-Signature") String sigHeader) {
        // Handle Stripe webhook events here
        // This is useful for handling events like payment_intent.succeeded
        // You would verify the webhook signature and process the event
        
        return ResponseEntity.ok("Webhook received");
    }
}