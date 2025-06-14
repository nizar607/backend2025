package com.example.stage24.checkout.service.implementation;

import com.example.stage24.article.domain.Article;
import com.example.stage24.checkout.dto.CheckoutRequest;
import com.example.stage24.checkout.dto.CheckoutResponse;
import com.example.stage24.checkout.service.interfaces.CheckoutServiceInterface;
import com.example.stage24.invoice.domain.Invoice;
import com.example.stage24.invoice.domain.InvoiceItem;
import com.example.stage24.invoice.repository.InvoiceRepository;
import com.example.stage24.basket.domain.Item;
import com.example.stage24.order.domain.Order;
import com.example.stage24.order.domain.OrderItem;
import com.example.stage24.order.repository.OrderRepository;
import com.example.stage24.payment.domain.Payment;
import com.example.stage24.payment.repository.PaymentRepository;
import com.example.stage24.basket.domain.ShopingCart;
import com.example.stage24.basket.repository.ShopingCartRepository;
import com.example.stage24.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CheckoutServiceImplementation implements CheckoutServiceInterface {
    
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final InvoiceRepository invoiceRepository;
    private final ShopingCartRepository shoppingCartRepository;
    
    // Tax rate (could be configurable)
    private static final Double DEFAULT_TAX_RATE = 0.08; // 8%
    private static final Double FREE_SHIPPING_THRESHOLD = 100.0;
    private static final Double STANDARD_SHIPPING_COST = 10.0;
    
    @Override
    @Transactional
    public CheckoutResponse processCheckout(CheckoutRequest checkoutRequest, User user) {
        try {
            log.info("Starting checkout process for user: {}", user.getEmail());
            
            // Validate checkout request
            List<String> validationErrors = validateCheckoutRequest(checkoutRequest, user);
            if (!validationErrors.isEmpty()) {
                return new CheckoutResponse(validationErrors);
            }
            
            // Create order from cart
            Order order = createOrderFromCart(user, checkoutRequest);
            if (order == null) {
                return new CheckoutResponse("Failed to create order", List.of("Unable to create order from cart"));
            }
            
            // Process payment
            Payment payment = processPayment(order, checkoutRequest);
            if (payment == null || payment.getStatus() != Payment.PaymentStatus.COMPLETED) {
                handlePaymentFailure(order, payment, "Payment processing failed");
                return new CheckoutResponse("Payment failed", List.of("Unable to process payment"));
            }
            
            // Update order status after successful payment
            order.setPaymentStatus(Order.PaymentStatus.PAID);
            order.setOrderStatus(Order.OrderStatus.CONFIRMED);
            order = orderRepository.save(order);
            
            // Generate invoice
            Invoice invoice = generateInvoice(order);
            
            // Update inventory
            updateInventory(order);
            
            // Clear shopping cart
            clearShoppingCart(user);
            
            // Send confirmations
            sendOrderConfirmation(order);
            if (invoice != null) {
                sendInvoiceEmail(invoice);
            }
            
            // Create successful response
            CheckoutResponse response = new CheckoutResponse(order, payment, invoice);
            response.generateConfirmationCode();
            response.addNextStep("Check your email for order confirmation");
            response.addNextStep("Track your order using order number: " + order.getOrderNumber());
            
            log.info("Checkout completed successfully for order: {}", order.getOrderNumber());
            return response;
            
        } catch (Exception e) {
            log.error("Error during checkout process for user: {}", user.getEmail(), e);
            return new CheckoutResponse("Checkout failed", List.of("An unexpected error occurred: " + e.getMessage()));
        }
    }
    
    @Override
    @Transactional
    public Order createOrderFromCart(User user, CheckoutRequest checkoutRequest) {
        try {
            // Get user's shopping cart
            Optional<ShopingCart> cartOpt = shoppingCartRepository.findByUser(user);
            if (cartOpt.isEmpty() || cartOpt.get().getItems().isEmpty()) {
                throw new RuntimeException("Shopping cart is empty");
            }
            
            ShopingCart cart = cartOpt.get();
            
            // Create new order
            Order order = new Order();
            order.setUser(user);
            order.generateOrderNumber();
            order.setOrderStatus(Order.OrderStatus.PENDING);
            order.setPaymentStatus(Order.PaymentStatus.PENDING);
            order.setPaymentMethod(checkoutRequest.getPaymentMethod());
            
            // Set shipping information
            order.setShippingAddress(checkoutRequest.getShippingAddress());
            order.setShippingCity(checkoutRequest.getShippingCity());
            order.setShippingState(checkoutRequest.getShippingState());
            order.setShippingZipCode(checkoutRequest.getShippingZipCode());
            order.setShippingCountry(checkoutRequest.getShippingCountry());
            
            // Set billing information
            order.setBillingAddress(checkoutRequest.getBillingAddressOrShipping());
            order.setBillingCity(checkoutRequest.getBillingCityOrShipping());
            order.setBillingState(checkoutRequest.getBillingStateOrShipping());
            order.setBillingZipCode(checkoutRequest.getBillingZipCodeOrShipping());
            order.setBillingCountry(checkoutRequest.getBillingCountryOrShipping());
            
            // Set customer notes
            order.setCustomerNotes(checkoutRequest.getCustomerNotes());
            
            // Calculate subtotal from cart items
            Double subtotal = 0.0;
            List<OrderItem> orderItems = new ArrayList<>();
            
            for (Item cartItem : cart.getItems()) {
                OrderItem orderItem = new OrderItem();
                orderItem.setOrder(order);
                orderItem.setArticle(cartItem.getArticle());
                orderItem.setQuantity(cartItem.getQuantity());
                orderItem.setUnitPrice(cartItem.getArticle().getPrice());
                orderItem.setTotalPrice(cartItem.getQuantity() * cartItem.getArticle().getPrice());
                orderItem.setProductDetailsFromArticle(cartItem.getArticle());
                
                orderItems.add(orderItem);
                subtotal += orderItem.getTotalPrice();
            }
            
            order.setOrderItems(orderItems);
            order.setSubtotalAmount(subtotal);
            
            // Apply discount if provided
            if (checkoutRequest.getDiscountCode() != null && !checkoutRequest.getDiscountCode().trim().isEmpty()) {
                order = applyDiscountCode(order, checkoutRequest.getDiscountCode());
            } else {
                order.setDiscountAmount(0.0);
            }
            
            // Calculate tax
            Double taxAmount = calculateTax(order);
            order.setTaxAmount(taxAmount);
            
            // Calculate shipping
            Double shippingAmount = calculateShipping(order);
            order.setShippingAmount(shippingAmount);
            
            // Calculate total
            Double total = order.getSubtotalAmount() - order.getDiscountAmount() + order.getTaxAmount() + order.getShippingAmount();
            order.setTotalAmount(total);
            
            // Save order
            return orderRepository.save(order);
            
        } catch (Exception e) {
            log.error("Error creating order from cart for user: {}", user.getEmail(), e);
            return null;
        }
    }
    
    @Override
    @Transactional
    public Payment processPayment(Order order, CheckoutRequest checkoutRequest) {
        try {
            Payment payment = new Payment();
            payment.setOrder(order);
            payment.setUser(order.getUser());
            payment.setAmount(order.getTotalAmount());
            payment.setPaymentMethod(Payment.PaymentMethod.valueOf(checkoutRequest.getPaymentMethod().toUpperCase()));
            payment.setStatus(Payment.PaymentStatus.PENDING);
            payment.setCurrency("USD");
            payment.generateTransactionId();
            
            // Set billing information
            payment.setBillingAddress(order.getBillingAddress());
            payment.setBillingCity(order.getBillingCity());
            payment.setBillingState(order.getBillingState());
            payment.setBillingZipCode(order.getBillingZipCode());
            payment.setBillingCountry(order.getBillingCountry());
            
            // Simulate payment processing based on payment method
            boolean paymentSuccess = simulatePaymentProcessing(payment, checkoutRequest);
            
            if (paymentSuccess) {
                payment.setStatus(Payment.PaymentStatus.COMPLETED);
                payment.setGatewayTransactionId("GTX-" + UUID.randomUUID().toString().substring(0, 8));
                payment.setGatewayName(getPaymentGateway(payment.getPaymentMethod()));
            } else {
                payment.setStatus(Payment.PaymentStatus.FAILED);
                payment.setFailureReason("Payment declined by gateway");
            }
            
            return paymentRepository.save(payment);
            
        } catch (Exception e) {
            log.error("Error processing payment for order: {}", order.getOrderNumber(), e);
            return null;
        }
    }
    
    @Override
    @Transactional
    public Invoice generateInvoice(Order order) {
        try {
            Invoice invoice = new Invoice();
            invoice.setOrder(order);
            invoice.setUser(order.getUser());
            invoice.setStatus(Invoice.InvoiceStatus.DRAFT);
            invoice.generateInvoiceNumber();
            
            // Set company information (could be configurable)
            invoice.setCompanyName("Your Company Name");
            invoice.setCompanyAddress("123 Business St, City, State 12345");
            invoice.setCompanyEmail("billing@yourcompany.com");
            invoice.setCompanyPhoneNumber("+1-555-123-4567");
            invoice.setCompanyTaxId("TAX123456789");
            
            // Set customer information from order
            invoice.setCustomerDetailsFromOrder(order);
            
            // Set financial details
            invoice.setSubtotalAmount(order.getSubtotalAmount());
            invoice.setTaxAmount(order.getTaxAmount());
            invoice.setDiscountAmount(order.getDiscountAmount());
            invoice.setShippingAmount(order.getShippingAmount());
            invoice.setTotalAmount(order.getTotalAmount());
            
            // Create invoice items from order items
            List<InvoiceItem> invoiceItems = new ArrayList<>();
            for (OrderItem orderItem : order.getOrderItems()) {
                InvoiceItem invoiceItem = new InvoiceItem();
                invoiceItem.setInvoice(invoice);
                invoiceItem.setArticle(orderItem.getArticle());
                invoiceItem.setQuantity(orderItem.getQuantity());
                invoiceItem.setUnitPrice(orderItem.getUnitPrice());
                invoiceItem.setProductDetailsFromArticle(orderItem.getArticle());
                
                invoiceItems.add(invoiceItem);
            }
            
            invoice.setInvoiceItems(invoiceItems);
            invoice.setStatus(Invoice.InvoiceStatus.SENT);
            
            return invoiceRepository.save(invoice);
            
        } catch (Exception e) {
            log.error("Error generating invoice for order: {}", order.getOrderNumber(), e);
            return null;
        }
    }
    
    @Override
    public List<String> validateCheckoutRequest(CheckoutRequest checkoutRequest, User user) {
        List<String> errors = new ArrayList<>();
        
        // Validate user has items in cart
        Optional<ShopingCart> cartOpt = shoppingCartRepository.findByUser(user);
        if (cartOpt.isEmpty() || cartOpt.get().getItems().isEmpty()) {
            errors.add("Shopping cart is empty");
        }
        
        // Validate payment details
        if (!checkoutRequest.isPaymentDetailsValid()) {
            errors.add("Invalid payment details for selected payment method");
        }
        
        // Validate terms agreement
        if (!checkoutRequest.getAgreeToTerms()) {
            errors.add("You must agree to the terms and conditions");
        }
        
        return errors;
    }
    
    @Override
    public Order calculateOrderTotals(User user, CheckoutRequest checkoutRequest) {
        // This method is used for preview calculations before actual checkout
        return createOrderFromCart(user, checkoutRequest);
    }
    
    @Override
    public Order applyDiscountCode(Order order, String discountCode) {
        // Simple discount logic - in real implementation, this would check a discount table
        Double discountAmount = 0.0;
        
        switch (discountCode.toUpperCase()) {
            case "SAVE10":
                discountAmount = order.getSubtotalAmount() * 0.10;
                break;
            case "SAVE20":
                discountAmount = order.getSubtotalAmount() * 0.20;
                break;
            case "WELCOME":
                discountAmount = Math.min(order.getSubtotalAmount() * 0.15, 50.0);
                break;
            default:
                log.warn("Invalid discount code: {}", discountCode);
                break;
        }
        
        order.setDiscountAmount(discountAmount);
        return order;
    }
    
    @Override
    public Double calculateTax(Order order) {
        // Simple tax calculation - in real implementation, this would be based on location
        return (order.getSubtotalAmount() - order.getDiscountAmount()) * DEFAULT_TAX_RATE;
    }
    
    @Override
    public Double calculateShipping(Order order) {
        // Free shipping for orders over threshold
        if (order.getSubtotalAmount() >= FREE_SHIPPING_THRESHOLD) {
            return 0.0;
        }
        return STANDARD_SHIPPING_COST;
    }
    
    @Override
    public void sendOrderConfirmation(Order order) {
        // In real implementation, this would send an email
        log.info("Sending order confirmation email for order: {} to: {}", 
                order.getOrderNumber(), order.getUser().getEmail());
    }
    
    @Override
    public void sendInvoiceEmail(Invoice invoice) {
        // In real implementation, this would send an email with invoice PDF
        log.info("Sending invoice email for invoice: {} to: {}", 
                invoice.getInvoiceNumber(), invoice.getCustomerEmail());
    }
    
    @Override
    public void updateInventory(Order order) {
        // In real implementation, this would update article inventory
        log.info("Updating inventory for order: {}", order.getOrderNumber());
        for (OrderItem item : order.getOrderItems()) {
            log.info("Reducing inventory for article: {} by quantity: {}", 
                    item.getArticle().getName(), item.getQuantity());
        }
    }
    
    @Override
    @Transactional
    public void clearShoppingCart(User user) {
        Optional<ShopingCart> cartOpt = shoppingCartRepository.findByUser(user);
        if (cartOpt.isPresent()) {
            ShopingCart cart = cartOpt.get();
            cart.getItems().clear();
            cart.setTotalAmount(0.0);
            shoppingCartRepository.save(cart);
            log.info("Cleared shopping cart for user: {}", user.getEmail());
        }
    }
    
    @Override
    public void handlePaymentFailure(Order order, Payment payment, String errorMessage) {
        order.setOrderStatus(Order.OrderStatus.PAYMENT_FAILED);
        order.setPaymentStatus(Order.PaymentStatus.FAILED);
        orderRepository.save(order);
        
        log.error("Payment failed for order: {} - {}", order.getOrderNumber(), errorMessage);
    }
    
    @Override
    public Payment retryPayment(Payment payment) {
        // Implementation for payment retry logic
        payment.setRetryCount(payment.getRetryCount() + 1);
        payment.setStatus(Payment.PaymentStatus.PENDING);
        return paymentRepository.save(payment);
    }
    
    @Override
    @Transactional
    public Order cancelOrder(Order order, String reason) {
        order.setOrderStatus(Order.OrderStatus.CANCELLED);
        order.setCustomerNotes(order.getCustomerNotes() + "\nCancellation reason: " + reason);
        return orderRepository.save(order);
    }
    
    @Override
    @Transactional
    public Payment refundOrder(Order order, Double amount, String reason) {
        Double refundAmount = amount != null ? amount : order.getTotalAmount();
        
        Payment refund = new Payment();
        refund.setOrder(order);
        refund.setUser(order.getUser());
        refund.setAmount(-refundAmount); // Negative amount for refund
        refund.setPaymentMethod(Payment.PaymentMethod.REFUND);
        refund.setStatus(Payment.PaymentStatus.COMPLETED);
        refund.setFailureReason(reason);
        refund.generateTransactionId();
        
        return paymentRepository.save(refund);
    }
    
    // Helper methods
    private boolean simulatePaymentProcessing(Payment payment, CheckoutRequest checkoutRequest) {
        // Simulate payment processing - in real implementation, this would call payment gateway
        // For demo purposes, assume 95% success rate
        return Math.random() > 0.05;
    }
    
    private String getPaymentGateway(Payment.PaymentMethod paymentMethod) {
        switch (paymentMethod) {
            case CREDIT_CARD:
            case DEBIT_CARD:
                return "Stripe";
            case PAYPAL:
                return "PayPal";
            case BANK_TRANSFER:
                return "ACH";
            default:
                return "Unknown";
        }
    }
}