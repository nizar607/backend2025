package com.example.stage24.checkout.service.interfaces;

import com.example.stage24.checkout.models.CheckoutRequest;
import com.example.stage24.checkout.models.CheckoutResponse;
import com.example.stage24.invoice.domain.Invoice;
import com.example.stage24.order.domain.Order;
import com.example.stage24.payment.domain.Payment;
import com.example.stage24.user.domain.User;

import java.util.List;

public interface CheckoutServiceInterface {
    
    /**
     * Process the complete checkout flow
     * @param checkoutRequest The checkout request containing order details
     * @param user The user making the purchase
     * @return CheckoutResponse containing order, payment, and invoice details
     */
    CheckoutResponse processCheckout(CheckoutRequest checkoutRequest, User user);
    
    /**
     * Create an order from shopping cart
     * @param user The user whose cart to convert
     * @param checkoutRequest The checkout request with shipping/billing info
     * @return Created order
     */
    Order createOrderFromCart(User user, CheckoutRequest checkoutRequest);
    
    /**
     * Process payment for an order
     * @param order The order to process payment for
     * @param checkoutRequest The checkout request with payment details
     * @return Payment result
     */
    Payment processPayment(Order order, CheckoutRequest checkoutRequest);
    
    /**
     * Generate invoice for a paid order
     * @param order The order to generate invoice for
     * @return Generated invoice
     */
    Invoice generateInvoice(Order order);
    
    /**
     * Validate checkout request
     * @param checkoutRequest The request to validate
     * @param user The user making the request
     * @return List of validation errors (empty if valid)
     */
    List<String> validateCheckoutRequest(CheckoutRequest checkoutRequest, User user);
    
    /**
     * Calculate order totals including tax and shipping
     * @param user The user whose cart to calculate
     * @param checkoutRequest The checkout request with shipping info
     * @return Order with calculated totals
     */
    Order calculateOrderTotals(User user, CheckoutRequest checkoutRequest);
    
    /**
     * Apply discount code to order
     * @param order The order to apply discount to
     * @param discountCode The discount code
     * @return Updated order with discount applied
     */
    Order applyDiscountCode(Order order, String discountCode);
    
    /**
     * Calculate tax for an order
     * @param order The order to calculate tax for
     * @return Tax amount
     */
    Double calculateTax(Order order);
    
    /**
     * Calculate shipping cost for an order
     * @param order The order to calculate shipping for
     * @return Shipping cost
     */
    Double calculateShipping(Order order);
    
    /**
     * Send order confirmation email
     * @param order The order to send confirmation for
     */
    void sendOrderConfirmation(Order order);
    
    /**
     * Send invoice email
     * @param invoice The invoice to send
     */
    void sendInvoiceEmail(Invoice invoice);
    
    /**
     * Update inventory after successful order
     * @param order The order to update inventory for
     */
    void updateInventory(Order order);
    
    /**
     * Clear user's shopping cart after successful checkout
     * @param user The user whose cart to clear
     */
    void clearShoppingCart(User user);
    
    /**
     * Handle payment failure
     * @param order The order with failed payment
     * @param payment The failed payment
     * @param errorMessage The error message
     */
    void handlePaymentFailure(Order order, Payment payment, String errorMessage);
    
    /**
     * Retry failed payment
     * @param payment The payment to retry
     * @return Updated payment result
     */
    Payment retryPayment(Payment payment);
    
    /**
     * Cancel order
     * @param order The order to cancel
     * @param reason The cancellation reason
     * @return Updated order
     */
    Order cancelOrder(Order order, String reason);
    
    /**
     * Refund order
     * @param order The order to refund
     * @param amount The amount to refund (null for full refund)
     * @param reason The refund reason
     * @return Refund payment record
     */
    Payment refundOrder(Order order, Double amount, String reason);
}