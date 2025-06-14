package com.example.stage24.order.repository;

import com.example.stage24.order.domain.Order;
import com.example.stage24.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    // Find orders by user
    List<Order> findByUser(User user);
    
    List<Order> findByUserOrderByCreatedAtDesc(User user);
    
    // Find orders by status
    List<Order> findByOrderStatus(Order.OrderStatus orderStatus);
    
    // Find orders by payment status
    List<Order> findByPaymentStatus(Order.PaymentStatus paymentStatus);
    
    // Find orders by user and status
    List<Order> findByUserAndOrderStatus(User user, Order.OrderStatus orderStatus);
    
    // Find orders by user and payment status
    List<Order> findByUserAndPaymentStatus(User user, Order.PaymentStatus paymentStatus);
    
    // Find orders within date range
    @Query("SELECT o FROM Order o WHERE o.createdAt BETWEEN :startDate AND :endDate")
    List<Order> findOrdersBetweenDates(@Param("startDate") LocalDateTime startDate, 
                                      @Param("endDate") LocalDateTime endDate);
    
    // Find orders by user within date range
    @Query("SELECT o FROM Order o WHERE o.user = :user AND o.createdAt BETWEEN :startDate AND :endDate")
    List<Order> findUserOrdersBetweenDates(@Param("user") User user,
                                          @Param("startDate") LocalDateTime startDate, 
                                          @Param("endDate") LocalDateTime endDate);
    
    // Find orders by order number
    Optional<Order> findByOrderNumber(String orderNumber);
    
    // Find orders by payment method
    List<Order> findByPaymentMethod(String paymentMethod);
    
    // Find orders with total amount greater than specified value
    @Query("SELECT o FROM Order o WHERE o.totalAmount >= :minAmount")
    List<Order> findOrdersWithMinAmount(@Param("minAmount") Double minAmount);
    
    // Find recent orders (last N days)
    @Query("SELECT o FROM Order o WHERE o.createdAt >= :sinceDate ORDER BY o.createdAt DESC")
    List<Order> findRecentOrders(@Param("sinceDate") LocalDateTime sinceDate);
    
    // Count orders by user
    long countByUser(User user);
    
    // Count orders by status
    long countByOrderStatus(Order.OrderStatus orderStatus);
    
    // Get total sales amount for user
    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.user = :user AND o.paymentStatus = 'PAID'")
    Double getTotalSalesAmountByUser(@Param("user") User user);
    
    // Get total sales amount for all orders
    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.paymentStatus = 'PAID'")
    Double getTotalSalesAmount();
    
    // Find pending orders (not completed and not cancelled)
    @Query("SELECT o FROM Order o WHERE o.orderStatus NOT IN ('COMPLETED', 'CANCELLED') ORDER BY o.createdAt DESC")
    List<Order> findPendingOrders();
    
    // Find orders that need shipping
    @Query("SELECT o FROM Order o WHERE o.orderStatus IN ('CONFIRMED', 'PROCESSING') AND o.paymentStatus = 'PAID'")
    List<Order> findOrdersReadyForShipping();
}