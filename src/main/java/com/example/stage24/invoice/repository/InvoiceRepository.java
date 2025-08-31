package com.example.stage24.invoice.repository;

import com.example.stage24.invoice.domain.Invoice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    // Basic queries
    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);
    boolean existsByInvoiceNumber(String invoiceNumber);

    // Status-based queries
    List<Invoice> findByStatus(Invoice.InvoiceStatus status);
    Page<Invoice> findByStatus(Invoice.InvoiceStatus status, Pageable pageable);
    Long countByStatus(Invoice.InvoiceStatus status);

    // User-based queries (accessing user's email and name)
    List<Invoice> findByUserEmail(String userEmail);
    List<Invoice> findByUserEmailOrderByCreatedAtDesc(String userEmail);
    List<Invoice> findByUserFirstNameContainingIgnoreCaseOrUserLastNameContainingIgnoreCase(String firstName, String lastName);
    Page<Invoice> findByUserEmail(String userEmail, Pageable pageable);
    
    // Company-based queries (these replace customer queries since your entity has company fields)
    List<Invoice> findByCompanyEmail(String companyEmail);
    List<Invoice> findByCompanyEmailOrderByCreatedAtDesc(String companyEmail);
    List<Invoice> findByCompanyName(String companyName);
    List<Invoice> findByCompanyNameContainingIgnoreCase(String companyName);
    Page<Invoice> findByCompanyEmail(String companyEmail, Pageable pageable);

    // Date-based queries (only createdAt exists in your entity)
    List<Invoice> findByCreatedAtBetween(LocalDateTime startDateTime, LocalDateTime endDateTime);

    // Financial queries
    @Query("SELECT SUM(i.totalAmount) FROM Invoice i WHERE i.status = :status")
    Double getTotalAmountByStatus(@Param("status") Invoice.InvoiceStatus status);
}