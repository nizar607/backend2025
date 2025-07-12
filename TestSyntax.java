public class TestSyntax {
    
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