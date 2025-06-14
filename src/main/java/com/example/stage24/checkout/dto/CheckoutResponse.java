package com.example.stage24.checkout.dto;

import com.example.stage24.invoice.domain.Invoice;
import com.example.stage24.order.domain.Order;
import com.example.stage24.payment.domain.Payment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckoutResponse {
    
    // Status Information
    private Boolean success;
    
    private String message;
    
    private List<String> errors;
    
    private List<String> warnings;
    
    // Order Information
    private Long orderId;
    
    private String orderNumber;
    
    private Order.OrderStatus orderStatus;
    
    private Double orderTotal;
    
    private LocalDateTime orderCreatedAt;
    
    // Payment Information
    private Long paymentId;
    
    private String transactionId;
    
    private Payment.PaymentStatus paymentStatus;
    
    private Payment.PaymentMethod paymentMethod;
    
    private Double paymentAmount;
    
    private String paymentGateway;
    
    private String gatewayTransactionId;
    
    private LocalDateTime paymentProcessedAt;
    
    // Invoice Information
    private Long invoiceId;
    
    private String invoiceNumber;
    
    private Invoice.InvoiceStatus invoiceStatus;
    
    private Double invoiceTotal;
    
    private LocalDateTime invoiceCreatedAt;
    
    // Delivery Information
    private String estimatedDeliveryDate;
    
    private String trackingNumber;
    
    private String shippingMethod;
    
    private Double shippingCost;
    
    // Customer Information
    private String customerEmail;
    
    private String customerName;
    
    // Additional Information
    private String confirmationCode;
    
    private String receiptUrl;
    
    private String invoiceUrl;
    
    private String trackingUrl;
    
    // Discount Information
    private String discountCode;
    
    private Double discountAmount;
    
    // Tax Information
    private Double taxAmount;
    
    private Double subtotalAmount;
    
    // Next Steps
    private List<String> nextSteps;
    
    private String redirectUrl;
    
    // Constructor for successful checkout
    public CheckoutResponse(Order order, Payment payment, Invoice invoice) {
        this.success = true;
        this.message = "Checkout completed successfully";
        
        // Order information
        if (order != null) {
            this.orderId = order.getId();
            this.orderNumber = order.getOrderNumber();
            this.orderStatus = order.getOrderStatus();
            this.orderTotal = order.getTotalAmount();
            this.orderCreatedAt = order.getCreatedAt();
            this.customerEmail = order.getUser().getEmail();
            this.customerName = order.getUser().getFirstName() + " " + order.getUser().getLastName();
            this.shippingCost = order.getShippingAmount();
            this.taxAmount = order.getTaxAmount();
            this.subtotalAmount = order.getSubtotalAmount();
        }
        
        // Payment information
        if (payment != null) {
            this.paymentId = payment.getId();
            this.transactionId = payment.getTransactionId();
            this.paymentStatus = payment.getStatus();
            this.paymentMethod = payment.getPaymentMethod();
            this.paymentAmount = payment.getAmount();
            this.paymentGateway = payment.getGatewayName();
            this.gatewayTransactionId = payment.getGatewayTransactionId();
            this.paymentProcessedAt = payment.getUpdatedAt();
        }
        
        // Invoice information
        if (invoice != null) {
            this.invoiceId = invoice.getId();
            this.invoiceNumber = invoice.getInvoiceNumber();
            this.invoiceStatus = invoice.getStatus();
            this.invoiceTotal = invoice.getTotalAmount();
            this.invoiceCreatedAt = invoice.getCreatedAt();
        }
    }
    
    // Constructor for failed checkout
    public CheckoutResponse(String errorMessage, List<String> errors) {
        this.success = false;
        this.message = errorMessage;
        this.errors = errors;
    }
    
    // Constructor for validation errors
    public CheckoutResponse(List<String> validationErrors) {
        this.success = false;
        this.message = "Validation failed";
        this.errors = validationErrors;
    }
    
    // Helper methods
    public boolean isSuccessful() {
        return success != null && success;
    }
    
    public boolean hasErrors() {
        return errors != null && !errors.isEmpty();
    }
    
    public boolean hasWarnings() {
        return warnings != null && !warnings.isEmpty();
    }
    
    public boolean isPaymentSuccessful() {
        return paymentStatus == Payment.PaymentStatus.COMPLETED;
    }
    
    public boolean isOrderConfirmed() {
        return orderStatus == Order.OrderStatus.CONFIRMED || 
               orderStatus == Order.OrderStatus.PROCESSING;
    }
    
    public boolean isInvoiceGenerated() {
        return invoiceId != null && invoiceNumber != null;
    }
    
    // Method to add error
    public void addError(String error) {
        if (this.errors == null) {
            this.errors = new java.util.ArrayList<>();
        }
        this.errors.add(error);
        this.success = false;
    }
    
    // Method to add warning
    public void addWarning(String warning) {
        if (this.warnings == null) {
            this.warnings = new java.util.ArrayList<>();
        }
        this.warnings.add(warning);
    }
    
    // Method to add next step
    public void addNextStep(String step) {
        if (this.nextSteps == null) {
            this.nextSteps = new java.util.ArrayList<>();
        }
        this.nextSteps.add(step);
    }
    
    // Method to generate confirmation code
    public void generateConfirmationCode() {
        if (this.orderNumber != null) {
            this.confirmationCode = "CONF-" + this.orderNumber + "-" + 
                                  String.valueOf(System.currentTimeMillis()).substring(8);
        }
    }
}