package com.example.stage24.checkout.controller;

import com.example.stage24.checkout.dto.CheckoutRequest;
import com.example.stage24.checkout.dto.CheckoutResponse;
import com.example.stage24.checkout.service.interfaces.CheckoutServiceInterface;
import com.example.stage24.order.domain.Order;
import com.example.stage24.user.domain.User;
import com.example.stage24.user.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/checkout")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class CheckoutController {
    
    private final CheckoutServiceInterface checkoutService;
    private final UserRepository userRepository;
    
    /**
     * Process complete checkout
     */
    @PostMapping("/process")
    public ResponseEntity<?> processCheckout(@Valid @RequestBody CheckoutRequest checkoutRequest) {
        try {
            User user = getCurrentUser();
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new CheckoutResponse("User not authenticated", List.of("Please log in to continue")));
            }
            
            log.info("Processing checkout for user: {}", user.getEmail());
            CheckoutResponse response = checkoutService.processCheckout(checkoutRequest, user);
            
            if (response.isSuccessful()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
            
        } catch (Exception e) {
            log.error("Error processing checkout", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new CheckoutResponse("Checkout failed", List.of("An unexpected error occurred")));
        }
    }
    
    /**
     * Calculate order totals (preview before checkout)
     */
    @PostMapping("/calculate")
    public ResponseEntity<?> calculateOrderTotals(@Valid @RequestBody CheckoutRequest checkoutRequest) {
        try {
            User user = getCurrentUser();
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("User not authenticated");
            }
            
            Order order = checkoutService.calculateOrderTotals(user, checkoutRequest);
            if (order == null) {
                return ResponseEntity.badRequest().body("Unable to calculate order totals");
            }
            
            // Return order summary
            OrderSummary summary = new OrderSummary();
            summary.setSubtotal(order.getSubtotalAmount());
            summary.setTax(order.getTaxAmount());
            summary.setShipping(order.getShippingAmount());
            summary.setDiscount(order.getDiscountAmount());
            summary.setTotal(order.getTotalAmount());
            summary.setItemCount(order.getOrderItems().size());
            
            return ResponseEntity.ok(summary);
            
        } catch (Exception e) {
            log.error("Error calculating order totals", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Failed to calculate order totals");
        }
    }
    
    /**
     * Validate checkout request
     */
    @PostMapping("/validate")
    public ResponseEntity<?> validateCheckout(@Valid @RequestBody CheckoutRequest checkoutRequest) {
        try {
            User user = getCurrentUser();
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("User not authenticated");
            }
            
            List<String> errors = checkoutService.validateCheckoutRequest(checkoutRequest, user);
            
            ValidationResponse response = new ValidationResponse();
            response.setValid(errors.isEmpty());
            response.setErrors(errors);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error validating checkout request", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Failed to validate checkout request");
        }
    }
    
    /**
     * Apply discount code
     */
    @PostMapping("/discount/{discountCode}")
    public ResponseEntity<?> applyDiscountCode(@PathVariable String discountCode,
                                              @Valid @RequestBody CheckoutRequest checkoutRequest) {
        try {
            User user = getCurrentUser();
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("User not authenticated");
            }
            
            Order order = checkoutService.calculateOrderTotals(user, checkoutRequest);
            if (order == null) {
                return ResponseEntity.badRequest().body("Unable to calculate order totals");
            }
            
            Order discountedOrder = checkoutService.applyDiscountCode(order, discountCode);
            
            DiscountResponse response = new DiscountResponse();
            response.setDiscountCode(discountCode);
            response.setDiscountAmount(discountedOrder.getDiscountAmount());
            response.setOriginalTotal(order.getSubtotalAmount() + order.getTaxAmount() + order.getShippingAmount());
            response.setNewTotal(discountedOrder.getTotalAmount());
            response.setValid(discountedOrder.getDiscountAmount() > 0);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error applying discount code: {}", discountCode, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Failed to apply discount code");
        }
    }
    
    /**
     * Get shipping options
     */
    @GetMapping("/shipping-options")
    public ResponseEntity<?> getShippingOptions() {
        try {
            List<ShippingOption> options = List.of(
                new ShippingOption("STANDARD", "Standard Shipping (5-7 business days)", 10.0),
                new ShippingOption("EXPRESS", "Express Shipping (2-3 business days)", 25.0),
                new ShippingOption("OVERNIGHT", "Overnight Shipping (1 business day)", 50.0)
            );
            
            return ResponseEntity.ok(options);
            
        } catch (Exception e) {
            log.error("Error getting shipping options", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Failed to get shipping options");
        }
    }
    
    /**
     * Get payment methods
     */
    @GetMapping("/payment-methods")
    public ResponseEntity<?> getPaymentMethods() {
        try {
            List<PaymentMethodOption> methods = List.of(
                new PaymentMethodOption("CREDIT_CARD", "Credit Card", true),
                new PaymentMethodOption("DEBIT_CARD", "Debit Card", true),
                new PaymentMethodOption("PAYPAL", "PayPal", true),
                new PaymentMethodOption("BANK_TRANSFER", "Bank Transfer", false)
            );
            
            return ResponseEntity.ok(methods);
            
        } catch (Exception e) {
            log.error("Error getting payment methods", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Failed to get payment methods");
        }
    }
    
    // Helper method to get current authenticated user
    private User getCurrentUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()) {
                String email = authentication.getName();
                Optional<User> userOpt = userRepository.findByEmail(email);
                return userOpt.orElse(null);
            }
            return null;
        } catch (Exception e) {
            log.error("Error getting current user", e);
            return null;
        }
    }
    
    // Inner classes for response DTOs
    public static class OrderSummary {
        private Double subtotal;
        private Double tax;
        private Double shipping;
        private Double discount;
        private Double total;
        private Integer itemCount;
        
        // Getters and setters
        public Double getSubtotal() { return subtotal; }
        public void setSubtotal(Double subtotal) { this.subtotal = subtotal; }
        
        public Double getTax() { return tax; }
        public void setTax(Double tax) { this.tax = tax; }
        
        public Double getShipping() { return shipping; }
        public void setShipping(Double shipping) { this.shipping = shipping; }
        
        public Double getTotal() { return total; }
        public void setTotal(Double total) { this.total = total; }
        
        public Integer getItemCount() { return itemCount; }
        public void setItemCount(Integer itemCount) { this.itemCount = itemCount; }
        
        public Double getDiscount() { return discount; }
        public void setDiscount(Double discount) { this.discount = discount; }
    }
    
    public static class ValidationResponse {
        private Boolean valid;
        private List<String> errors;
        
        public Boolean getValid() { return valid; }
        public void setValid(Boolean valid) { this.valid = valid; }
        
        public List<String> getErrors() { return errors; }
        public void setErrors(List<String> errors) { this.errors = errors; }
    }
    
    public static class DiscountResponse {
        private String discountCode;
        private Double discountAmount;
        private Double originalTotal;
        private Double newTotal;
        private Boolean valid;
        
        public String getDiscountCode() { return discountCode; }
        public void setDiscountCode(String discountCode) { this.discountCode = discountCode; }
        
        public Double getDiscountAmount() { return discountAmount; }
        public void setDiscountAmount(Double discountAmount) { this.discountAmount = discountAmount; }
        
        public Double getOriginalTotal() { return originalTotal; }
        public void setOriginalTotal(Double originalTotal) { this.originalTotal = originalTotal; }
        
        public Double getNewTotal() { return newTotal; }
        public void setNewTotal(Double newTotal) { this.newTotal = newTotal; }
        
        public Boolean getValid() { return valid; }
        public void setValid(Boolean valid) { this.valid = valid; }
    }
    
    public static class ShippingOption {
        private String code;
        private String name;
        private Double cost;
        
        public ShippingOption(String code, String name, Double cost) {
            this.code = code;
            this.name = name;
            this.cost = cost;
        }
        
        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public Double getCost() { return cost; }
        public void setCost(Double cost) { this.cost = cost; }
    }
    
    public static class PaymentMethodOption {
        private String code;
        private String name;
        private Boolean enabled;
        
        public PaymentMethodOption(String code, String name, Boolean enabled) {
            this.code = code;
            this.name = name;
            this.enabled = enabled;
        }
        
        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public Boolean getEnabled() { return enabled; }
        public void setEnabled(Boolean enabled) { this.enabled = enabled; }
    }
}