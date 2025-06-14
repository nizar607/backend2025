package com.example.stage24.payment.controller;

import com.example.stage24.order.domain.Order;
import com.example.stage24.order.repository.OrderRepository;
import com.example.stage24.payment.domain.Payment;
import com.example.stage24.payment.service.StripePaymentService;
import com.stripe.exception.StripeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "*")
public class PaymentController {

    @Autowired
    private StripePaymentService stripePaymentService;

    @Autowired
    private OrderRepository orderRepository;

    /**
     * Create Stripe payment intent
     */
    @PostMapping("/stripe/create-intent")
    public ResponseEntity<?> createStripePaymentIntent(@RequestBody Map<String, Object> request) {
        try {
            Long orderId = Long.valueOf(request.get("orderId").toString());
            String customerEmail = request.get("customerEmail").toString();

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
    public ResponseEntity<?> confirmStripePayment(@RequestBody Map<String, String> request) {
        try {
            String paymentIntentId = request.get("paymentIntentId");
            
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
    public ResponseEntity<?> createCashPayment(@RequestBody Map<String, Object> request) {
        try {
            Long orderId = Long.valueOf(request.get("orderId").toString());

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
        Map<String, String> testCards = stripePaymentService.getTestCards();
        
        Map<String, Object> response = new HashMap<>();
        response.put("testCards", testCards);
        response.put("instructions", Map.of(
            "expiry", "Use any future date (e.g., 12/25)",
            "cvc", "Use any 3-digit number (e.g., 123)",
            "zip", "Use any 5-digit number (e.g., 12345)"
        ));
        
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