package com.example.stage24.invoice.repository;

import com.example.stage24.invoice.domain.Invoice;
import com.example.stage24.order.domain.Order;
import com.example.stage24.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    
    // Find invoice by order
    Optional<Invoice> findByOrder(Order order);
    
    // Find invoices by user
    List<Invoice> findByUser(User user);
    
    List<Invoice> findByUserOrderByCreatedAtDesc(User user);
    
    // Find invoice by invoice number
    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);
    
    // Find invoices by status
    List<Invoice> findByStatus(Invoice.InvoiceStatus status);
    
    // Find invoices by user and status
    List<Invoice> findByUserAndStatus(User user, Invoice.InvoiceStatus status);
    
    // Find invoices within date range
    @Query("SELECT i FROM Invoice i WHERE i.issueDate BETWEEN :startDate AND :endDate")
    List<Invoice> findInvoicesBetweenDates(@Param("startDate") LocalDate startDate, 
                                          @Param("endDate") LocalDate endDate);
    
    // Find invoices by user within date range
    @Query("SELECT i FROM Invoice i WHERE i.user = :user AND i.issueDate BETWEEN :startDate AND :endDate")
    List<Invoice> findUserInvoicesBetweenDates(@Param("user") User user,
                                              @Param("startDate") LocalDate startDate, 
                                              @Param("endDate") LocalDate endDate);
    
    // Find overdue invoices
    @Query("SELECT i FROM Invoice i WHERE i.dueDate < :currentDate AND i.status NOT IN ('PAID', 'CANCELLED')")
    List<Invoice> findOverdueInvoices(@Param("currentDate") LocalDate currentDate);
    
    // Find invoices due soon (within specified days)
    @Query("SELECT i FROM Invoice i WHERE i.dueDate BETWEEN :currentDate AND :dueDate AND i.status NOT IN ('PAID', 'CANCELLED')")
    List<Invoice> findInvoicesDueSoon(@Param("currentDate") LocalDate currentDate, 
                                     @Param("dueDate") LocalDate dueDate);
    
    // Find unpaid invoices
    @Query("SELECT i FROM Invoice i WHERE i.status NOT IN ('PAID', 'CANCELLED')")
    List<Invoice> findUnpaidInvoices();
    
    // Find paid invoices
    @Query("SELECT i FROM Invoice i WHERE i.status = 'PAID'")
    List<Invoice> findPaidInvoices();
    
    // Find draft invoices
    @Query("SELECT i FROM Invoice i WHERE i.status = 'DRAFT'")
    List<Invoice> findDraftInvoices();
    
    // Find invoices with total amount greater than specified value
    @Query("SELECT i FROM Invoice i WHERE i.totalAmount >= :minAmount")
    List<Invoice> findInvoicesWithMinAmount(@Param("minAmount") Double minAmount);
    
    // Get total invoice amount by user
    @Query("SELECT COALESCE(SUM(i.totalAmount), 0) FROM Invoice i WHERE i.user = :user")
    Double getTotalInvoiceAmountByUser(@Param("user") User user);
    
    // Get total paid amount by user
    @Query("SELECT COALESCE(SUM(i.totalAmount), 0) FROM Invoice i WHERE i.user = :user AND i.status = 'PAID'")
    Double getTotalPaidAmountByUser(@Param("user") User user);
    
    // Get total outstanding amount by user
    @Query("SELECT COALESCE(SUM(i.totalAmount), 0) FROM Invoice i WHERE i.user = :user AND i.status NOT IN ('PAID', 'CANCELLED')")
    Double getTotalOutstandingAmountByUser(@Param("user") User user);
    
    // Get total invoice amount for all invoices
    @Query("SELECT COALESCE(SUM(i.totalAmount), 0) FROM Invoice i")
    Double getTotalInvoiceAmount();
    
    // Get total paid amount for all invoices
    @Query("SELECT COALESCE(SUM(i.totalAmount), 0) FROM Invoice i WHERE i.status = 'PAID'")
    Double getTotalPaidAmount();
    
    // Count invoices by user
    long countByUser(User user);
    
    // Count invoices by status
    long countByStatus(Invoice.InvoiceStatus status);
    
    // Count overdue invoices
    @Query("SELECT COUNT(i) FROM Invoice i WHERE i.dueDate < :currentDate AND i.status NOT IN ('PAID', 'CANCELLED')")
    long countOverdueInvoices(@Param("currentDate") LocalDate currentDate);
    
    // Find recent invoices (last N days)
    @Query("SELECT i FROM Invoice i WHERE i.createdAt >= :sinceDate ORDER BY i.createdAt DESC")
    List<Invoice> findRecentInvoices(@Param("sinceDate") LocalDateTime sinceDate);
    
    // Find invoices by customer email
    List<Invoice> findByCustomerEmail(String customerEmail);
    
    // Find invoices by customer name
    @Query("SELECT i FROM Invoice i WHERE LOWER(i.customerName) LIKE LOWER(CONCAT('%', :customerName, '%'))")
    List<Invoice> findByCustomerNameContaining(@Param("customerName") String customerName);
    
    // Get invoice statistics by status
    @Query("SELECT i.status, COUNT(i), SUM(i.totalAmount) FROM Invoice i GROUP BY i.status")
    List<Object[]> getInvoiceStatisticsByStatus();
    
    // Find invoices that need to be sent (draft status and ready)
    @Query("SELECT i FROM Invoice i WHERE i.status = 'DRAFT' AND i.issueDate <= :currentDate")
    List<Invoice> findInvoicesReadyToSend(@Param("currentDate") LocalDate currentDate);
}