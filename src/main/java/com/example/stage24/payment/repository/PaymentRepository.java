package com.example.stage24.payment.repository;

import com.example.stage24.order.domain.Order;
import com.example.stage24.payment.domain.Payment;
import com.example.stage24.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    
    // Find payments by order
    List<Payment> findByOrder(Order order);
    
    // Find payments by user
    List<Payment> findByUser(User user);
    
    List<Payment> findByUserOrderByCreatedAtDesc(User user);
    
    // Find payments by status
    List<Payment> findByStatus(Payment.PaymentStatus status);
    
    // Find payments by payment method
    List<Payment> findByPaymentMethod(Payment.PaymentMethod paymentMethod);
    
    // Find payment by transaction ID
    Optional<Payment> findByTransactionId(String transactionId);
    
    // Find payment by gateway transaction ID
    Optional<Payment> findByGatewayTransactionId(String gatewayTransactionId);
    
    // Find successful payments for an order
    @Query("SELECT p FROM Payment p WHERE p.order = :order AND p.status = 'COMPLETED'")
    List<Payment> findSuccessfulPaymentsByOrder(@Param("order") Order order);
    
    // Find pending payments
    @Query("SELECT p FROM Payment p WHERE p.status IN ('PENDING', 'PROCESSING')")
    List<Payment> findPendingPayments();
    
    // Find failed payments
    @Query("SELECT p FROM Payment p WHERE p.status IN ('FAILED', 'CANCELLED', 'DECLINED')")
    List<Payment> findFailedPayments();
    
    // Find payments within date range
    @Query("SELECT p FROM Payment p WHERE p.createdAt BETWEEN :startDate AND :endDate")
    List<Payment> findPaymentsBetweenDates(@Param("startDate") LocalDateTime startDate, 
                                          @Param("endDate") LocalDateTime endDate);
    
    // Find payments by user within date range
    @Query("SELECT p FROM Payment p WHERE p.user = :user AND p.createdAt BETWEEN :startDate AND :endDate")
    List<Payment> findUserPaymentsBetweenDates(@Param("user") User user,
                                              @Param("startDate") LocalDateTime startDate, 
                                              @Param("endDate") LocalDateTime endDate);
    
    // Find payments with amount greater than specified value
    @Query("SELECT p FROM Payment p WHERE p.amount >= :minAmount")
    List<Payment> findPaymentsWithMinAmount(@Param("minAmount") Double minAmount);
    
    // Get total payment amount by user
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.user = :user AND p.status = 'COMPLETED'")
    Double getTotalPaymentAmountByUser(@Param("user") User user);
    
    // Get total payment amount for all completed payments
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.status = 'COMPLETED'")
    Double getTotalCompletedPaymentAmount();
    
    // Count payments by user
    long countByUser(User user);
    
    // Count payments by status
    long countByStatus(Payment.PaymentStatus status);
    
    // Count successful payments by user
    @Query("SELECT COUNT(p) FROM Payment p WHERE p.user = :user AND p.status = 'COMPLETED'")
    long countSuccessfulPaymentsByUser(@Param("user") User user);
    
    // Find refunded payments
    @Query("SELECT p FROM Payment p WHERE p.status = 'REFUNDED'")
    List<Payment> findRefundedPayments();
    
    // Find payments by gateway
    List<Payment> findByGatewayName(String gatewayName);
    
    // Find recent payments (last N days)
    @Query("SELECT p FROM Payment p WHERE p.createdAt >= :sinceDate ORDER BY p.createdAt DESC")
    List<Payment> findRecentPayments(@Param("sinceDate") LocalDateTime sinceDate);
    
    // Find payments that need retry (failed but retryable)
    @Query("SELECT p FROM Payment p WHERE p.status = 'FAILED' AND p.retryCount < 3")
    List<Payment> findPaymentsForRetry();
    
    // Get payment statistics by method
    @Query("SELECT p.paymentMethod, COUNT(p), SUM(p.amount) FROM Payment p " +
           "WHERE p.status = 'COMPLETED' GROUP BY p.paymentMethod")
    List<Object[]> getPaymentStatisticsByMethod();
}