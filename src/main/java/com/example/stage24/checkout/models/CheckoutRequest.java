package com.example.stage24.checkout.models;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckoutRequest {
    
    // Customer Information
    @NotBlank(message = "First name is required")
    @Size(max = 50, message = "First name must not exceed 50 characters")
    private String firstName;
    
    @NotBlank(message = "Last name is required")
    @Size(max = 50, message = "Last name must not exceed 50 characters")
    private String lastName;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    private String email;
    
    @Size(max = 20, message = "Phone number must not exceed 20 characters")
    private String phoneNumber;
    
    // Shipping Address
    @NotBlank(message = "Shipping address is required")
    @Size(max = 255, message = "Shipping address must not exceed 255 characters")
    private String shippingAddress;
    
    @NotBlank(message = "Shipping city is required")
    @Size(max = 100, message = "Shipping city must not exceed 100 characters")
    private String shippingCity;
    
    @NotBlank(message = "Shipping state is required")
    @Size(max = 100, message = "Shipping state must not exceed 100 characters")
    private String shippingState;
    
    @NotBlank(message = "Shipping ZIP code is required")
    @Size(max = 20, message = "Shipping ZIP code must not exceed 20 characters")
    private String shippingZipCode;
    
    @NotBlank(message = "Shipping country is required")
    @Size(max = 100, message = "Shipping country must not exceed 100 characters")
    private String shippingCountry;
    
    // Billing Address (optional - can use shipping address)
    private Boolean useSameAddressForBilling = true;
    
    @Size(max = 255, message = "Billing address must not exceed 255 characters")
    private String billingAddress;
    
    @Size(max = 100, message = "Billing city must not exceed 100 characters")
    private String billingCity;
    
    @Size(max = 100, message = "Billing state must not exceed 100 characters")
    private String billingState;
    
    @Size(max = 20, message = "Billing ZIP code must not exceed 20 characters")
    private String billingZipCode;
    
    @Size(max = 100, message = "Billing country must not exceed 100 characters")
    private String billingCountry;
    
    // Payment Information
    @NotBlank(message = "Payment method is required")
    private String paymentMethod; // CREDIT_CARD, DEBIT_CARD, PAYPAL, BANK_TRANSFER, etc.
    
    // Credit Card Information (if applicable)
    private String cardNumber;
    private String cardHolderName;
    private String expiryMonth;
    private String expiryYear;
    private String cvv;
    
    // PayPal Information (if applicable)
    private String paypalEmail;
    
    // Bank Transfer Information (if applicable)
    private String bankAccountNumber;
    private String routingNumber;
    
    // Order Information
    private String discountCode;
    
    @Size(max = 500, message = "Customer notes must not exceed 500 characters")
    private String customerNotes;
    
    // Terms and Conditions
    @NotNull(message = "You must agree to the terms and conditions")
    private Boolean agreeToTerms = false;
    
    // Newsletter subscription (optional)
    private Boolean subscribeToNewsletter = false;
    
    // Gift wrap option (optional)
    private Boolean giftWrap = false;
    
    // Express delivery option (optional)
    private Boolean expressDelivery = false;
    
    // Gift message (optional)
    private String giftMessage;
    
    // Special instructions (optional)
    private String specialInstructions;
    
    // Helper methods for billing address
    public String getBillingAddressOrShipping() {
        return useSameAddressForBilling ? shippingAddress : billingAddress;
    }
    
    public String getBillingCityOrShipping() {
        return useSameAddressForBilling ? shippingCity : billingCity;
    }
    
    public String getBillingStateOrShipping() {
        return useSameAddressForBilling ? shippingState : billingState;
    }
    
    public String getBillingZipCodeOrShipping() {
        return useSameAddressForBilling ? shippingZipCode : billingZipCode;
    }
    
    public String getBillingCountryOrShipping() {
        return useSameAddressForBilling ? shippingCountry : billingCountry;
    }
    
    // Validation helper methods
    public boolean isPaymentDetailsValid() {
        if (paymentMethod == null || paymentMethod.trim().isEmpty()) {
            return false;
        }
        
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
                       routingNumber != null && !routingNumber.trim().isEmpty();
            case "CASH":
                return true; // Cash payments don't need additional validation
            default:
                return false;
        }
    }
    
    public boolean hasValidBillingAddress() {
        if (useSameAddressForBilling) {
            return hasValidShippingAddress();
        }
        
        return billingAddress != null && !billingAddress.trim().isEmpty() &&
               billingCity != null && !billingCity.trim().isEmpty() &&
               billingState != null && !billingState.trim().isEmpty() &&
               billingZipCode != null && !billingZipCode.trim().isEmpty() &&
               billingCountry != null && !billingCountry.trim().isEmpty();
    }
    
    // Additional validation methods
    public boolean isTermsAccepted() {
        return agreeToTerms != null && agreeToTerms;
    }
    
    public boolean hasDiscountCode() {
        return discountCode != null && !discountCode.trim().isEmpty();
    }
    
    public boolean hasValidShippingAddress() {
        return shippingAddress != null && !shippingAddress.trim().isEmpty() &&
               shippingCity != null && !shippingCity.trim().isEmpty() &&
               shippingState != null && !shippingState.trim().isEmpty() &&
               shippingZipCode != null && !shippingZipCode.trim().isEmpty() &&
               shippingCountry != null && !shippingCountry.trim().isEmpty();
    }
    
    public String getFullShippingAddress() {
        return String.format("%s, %s, %s %s, %s", 
                           shippingAddress, shippingCity, shippingState, shippingZipCode, shippingCountry);
    }
    
    public String getFullBillingAddress() {
        return String.format("%s, %s, %s %s, %s", 
                           getBillingAddressOrShipping(), 
                           getBillingCityOrShipping(), 
                           getBillingStateOrShipping(), 
                           getBillingZipCodeOrShipping(), 
                           getBillingCountryOrShipping());
    }
    
    public String getCustomerFullName() {
        return firstName + " " + lastName;
    }
    
    // Business logic helpers
    public boolean requiresSpecialHandling() {
        return (expressDelivery != null && expressDelivery) || 
               (giftWrap != null && giftWrap) || 
               (specialInstructions != null && !specialInstructions.trim().isEmpty());
    }
    
    public boolean isGiftOrder() {
        return (giftWrap != null && giftWrap) || 
               (giftMessage != null && !giftMessage.trim().isEmpty());
    }
    
    public Double calculateAdditionalFees() {
        Double fees = 0.0;
        if (expressDelivery != null && expressDelivery) {
            fees += 15.0; // Express delivery fee
        }
        if (giftWrap != null && giftWrap) {
            fees += 5.0; // Gift wrap fee
        }
        return fees;
    }
    
    @Override
    public String toString() {
        return "CheckoutRequest{" +
                "customerName='" + getCustomerFullName() + '\'' +
                ", email='" + email + '\'' +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", shippingAddress='" + getFullShippingAddress() + '\'' +
                ", expressDelivery=" + expressDelivery +
                ", giftWrap=" + giftWrap +
                '}';
    }
}