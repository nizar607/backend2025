package com.example.stage24.checkout.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckoutRequest {
    
    // Customer Information
    @NotBlank(message = "First name is required")
    private String firstName;
    
    @NotBlank(message = "Last name is required")
    private String lastName;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Valid email is required")
    private String email;
    
    private String phoneNumber;
    
    // Shipping Address
    @NotBlank(message = "Shipping address is required")
    private String shippingAddress;
    
    @NotBlank(message = "Shipping city is required")
    private String shippingCity;
    
    @NotBlank(message = "Shipping state is required")
    private String shippingState;
    
    @NotBlank(message = "Shipping zip code is required")
    private String shippingZipCode;
    
    @NotBlank(message = "Shipping country is required")
    private String shippingCountry;
    
    // Billing Address
    private Boolean sameAsBilling = false;
    
    private String billingAddress;
    
    private String billingCity;
    
    private String billingState;
    
    private String billingZipCode;
    
    private String billingCountry;
    
    // Payment Information
    @NotNull(message = "Payment method is required")
    private String paymentMethod; // CREDIT_CARD, DEBIT_CARD, PAYPAL, BANK_TRANSFER, etc.
    
    // Credit Card Details (if applicable)
    private String cardNumber;
    
    private String cardHolderName;
    
    private String expiryMonth;
    
    private String expiryYear;
    
    private String cvv;
    
    // PayPal Details (if applicable)
    private String paypalEmail;
    
    // Bank Transfer Details (if applicable)
    private String bankAccountNumber;
    
    private String bankRoutingNumber;
    
    private String bankName;
    
    // Order Details
    private String discountCode;
    
    private String customerNotes;
    
    private Boolean subscribeToNewsletter = false;
    
    private Boolean agreeToTerms = false;
    
    // Shipping Options
    private String shippingMethod; // STANDARD, EXPRESS, OVERNIGHT, etc.
    
    private Double shippingCost;
    
    // Tax Information
    private String taxId; // For business customers
    
    private Boolean isBusiness = false;
    
    private String companyName;
    
    // Special Instructions
    private String deliveryInstructions;
    
    private String giftMessage;
    
    private Boolean isGift = false;
    
    // Validation helper methods
    public String getBillingAddressOrShipping() {
        return sameAsBilling ? shippingAddress : billingAddress;
    }
    
    public String getBillingCityOrShipping() {
        return sameAsBilling ? shippingCity : billingCity;
    }
    
    public String getBillingStateOrShipping() {
        return sameAsBilling ? shippingState : billingState;
    }
    
    public String getBillingZipCodeOrShipping() {
        return sameAsBilling ? shippingZipCode : billingZipCode;
    }
    
    public String getBillingCountryOrShipping() {
        return sameAsBilling ? shippingCountry : billingCountry;
    }
    
    // Validation method for payment details
    public boolean isPaymentDetailsValid() {
        switch (paymentMethod.toUpperCase()) {
            case "CREDIT_CARD":
            case "DEBIT_CARD":
                return cardNumber != null && !cardNumber.trim().isEmpty() &&
                       cardHolderName != null && !cardHolderName.trim().isEmpty() &&
                       expiryMonth != null && !expiryMonth.trim().isEmpty() &&
                       expiryYear != null && !expiryYear.trim().isEmpty() &&
                       cvv != null && !cvv.trim().isEmpty();
            case "PAYPAL":
                return paypalEmail != null && !paypalEmail.trim().isEmpty();
            case "BANK_TRANSFER":
                return bankAccountNumber != null && !bankAccountNumber.trim().isEmpty() &&
                       bankRoutingNumber != null && !bankRoutingNumber.trim().isEmpty();
            default:
                return true; // For other payment methods, assume valid
        }
    }
    
    // Method to mask sensitive payment information for logging
    public CheckoutRequest getMaskedForLogging() {
        CheckoutRequest masked = new CheckoutRequest();
        // Copy non-sensitive fields
        masked.firstName = this.firstName;
        masked.lastName = this.lastName;
        masked.email = this.email;
        masked.shippingAddress = this.shippingAddress;
        masked.shippingCity = this.shippingCity;
        masked.shippingState = this.shippingState;
        masked.shippingZipCode = this.shippingZipCode;
        masked.shippingCountry = this.shippingCountry;
        masked.paymentMethod = this.paymentMethod;
        
        // Mask sensitive fields
        if (this.cardNumber != null && this.cardNumber.length() > 4) {
            masked.cardNumber = "****-****-****-" + this.cardNumber.substring(this.cardNumber.length() - 4);
        }
        if (this.cvv != null) {
            masked.cvv = "***";
        }
        
        return masked;
    }
}