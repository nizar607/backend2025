package com.example.stage24.payment.service;

import com.example.stage24.order.domain.Order;
import com.example.stage24.order.repository.OrderRepository;
import com.example.stage24.payment.domain.Payment;
import com.example.stage24.payment.repository.PaymentRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class StripePaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    public StripePaymentService() {
        // Initialize Stripe with your secret key
        // In production, this should be set via environment variable
    }

    /**
     * Create a Stripe PaymentIntent for the order
     */
    @Transactional
    public Map<String, Object> createPaymentIntent(Order order, String customerEmail) throws StripeException {
        // Set the Stripe API key
        Stripe.apiKey = stripeSecretKey;

        // Convert amount to cents (Stripe uses smallest currency unit)
        long amountInCents = Math.round(order.getTotalAmount() * 100);

        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(amountInCents)
                .setCurrency("usd")
                .putMetadata("order_id", order.getId().toString())
                .putMetadata("order_number", order.getOrderNumber())
                .setReceiptEmail(customerEmail)
                .setDescription("Payment for Order #" + order.getOrderNumber())
                .build();

        PaymentIntent paymentIntent = PaymentIntent.create(params);

        // Create a Payment record in our database
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setUser(order.getUser());
        payment.setPaymentReference("stripe_" + paymentIntent.getId());
        payment.setTransactionId(paymentIntent.getId());
        payment.setPaymentMethod(Payment.PaymentMethod.CREDIT_CARD);
        payment.setStatus(Payment.PaymentStatus.PENDING);
        payment.setAmount(order.getTotalAmount());
        payment.setCurrency("USD");
        payment.setGatewayName("Stripe");
        payment.setGatewayTransactionId(paymentIntent.getId());
        payment.setBillingEmail(customerEmail);
        
        paymentRepository.save(payment);

        // Return client secret for frontend
        Map<String, Object> response = new HashMap<>();
        response.put("clientSecret", paymentIntent.getClientSecret());
        response.put("paymentIntentId", paymentIntent.getId());
        response.put("amount", order.getTotalAmount());
        response.put("currency", "USD");
        
        return response;
    }

    /**
     * Confirm payment and update order status
     */
    @Transactional
    public boolean confirmPayment(String paymentIntentId) {
        try {
            Stripe.apiKey = stripeSecretKey;
            
            PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
            
            // Find our payment record
            Payment payment = paymentRepository.findByTransactionId(paymentIntentId)
                    .orElseThrow(() -> new RuntimeException("Payment not found"));
            
            if ("succeeded".equals(paymentIntent.getStatus())) {
                // Update payment status
                payment.setStatus(Payment.PaymentStatus.COMPLETED);
                payment.setProcessedAt(LocalDateTime.now());
                
                // Card details will be updated separately if needed
                // For now, we'll just mark the payment as completed
                
                // Update order status
                Order order = payment.getOrder();
                order.setPaymentStatus(Order.PaymentStatus.PAID);
                order.setOrderStatus(Order.OrderStatus.CONFIRMED);
                
                paymentRepository.save(payment);
                orderRepository.save(order);
                
                return true;
            } else {
                // Payment failed
                payment.setStatus(Payment.PaymentStatus.FAILED);
                payment.setFailedAt(LocalDateTime.now());
                payment.setFailureReason(paymentIntent.getLastPaymentError() != null ? 
                    paymentIntent.getLastPaymentError().getMessage() : "Payment failed");
                
                Order order = payment.getOrder();
                order.setPaymentStatus(Order.PaymentStatus.FAILED);
                
                paymentRepository.save(payment);
                orderRepository.save(order);
                
                return false;
            }
        } catch (StripeException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Process cash payment (admin confirms manually)
     */
    @Transactional
    public Payment processCashPayment(Order order) {
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setUser(order.getUser());
        payment.setPaymentReference("cash_" + System.currentTimeMillis());
        payment.setTransactionId("CASH_" + order.getOrderNumber());
        payment.setPaymentMethod(Payment.PaymentMethod.CASH_ON_DELIVERY);
        payment.setStatus(Payment.PaymentStatus.PENDING); // Admin will confirm later
        payment.setAmount(order.getTotalAmount());
        payment.setCurrency("USD");
        payment.setGatewayName("Manual");
        
        return paymentRepository.save(payment);
    }

    /**
     * Admin confirms cash payment
     */
    @Transactional
    public boolean confirmCashPayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        
        if (payment.getPaymentMethod() == Payment.PaymentMethod.CASH_ON_DELIVERY) {
            payment.setStatus(Payment.PaymentStatus.COMPLETED);
            payment.setProcessedAt(LocalDateTime.now());
            
            // Update order status
            Order order = payment.getOrder();
            order.setPaymentStatus(Order.PaymentStatus.PAID);
            order.setOrderStatus(Order.OrderStatus.CONFIRMED);
            
            paymentRepository.save(payment);
            orderRepository.save(order);
            return true;
        }
        
        return false;
    }

    /**
     * Get test card numbers for development
     */
    public Map<String, String> getTestCards() {
        Map<String, String> testCards = new HashMap<>();
        testCards.put("visa_success", "4242424242424242");
        testCards.put("visa_declined", "4000000000000002");
        testCards.put("mastercard_success", "5555555555554444");
        testCards.put("amex_success", "378282246310005");
        testCards.put("visa_3d_secure", "4000000000003220");
        
        return testCards;
    }
}