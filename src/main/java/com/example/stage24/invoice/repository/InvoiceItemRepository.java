package com.example.stage24.invoice.repository;

import com.example.stage24.invoice.domain.InvoiceItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InvoiceItemRepository extends JpaRepository<InvoiceItem, Long> {
    
    // Basic queries by invoice
    List<InvoiceItem> findByInvoiceId(Long invoiceId);
    List<InvoiceItem> findByInvoiceIdOrderByCreatedAtAsc(Long invoiceId);
    void deleteByInvoiceId(Long invoiceId);
    Long countByInvoiceId(Long invoiceId);
    
    // Article-based queries
    List<InvoiceItem> findByArticleId(Long articleId);
    List<InvoiceItem> findByArticleIdOrderByCreatedAtDesc(Long articleId);
    Long countByArticleId(Long articleId);
    
    // Article name queries
    List<InvoiceItem> findByArticleNameContainingIgnoreCase(String articleName);
    List<InvoiceItem> findByArticleName(String articleName);
    
    // Financial queries
    @Query("SELECT SUM(ii.lineTotal) FROM InvoiceItem ii WHERE ii.invoice.id = :invoiceId")
    Double sumLineTotalByInvoiceId(@Param("invoiceId") Long invoiceId);
    
    @Query("SELECT SUM(ii.lineTotal) FROM InvoiceItem ii WHERE ii.articleId = :articleId")
    Double sumLineTotalByArticleId(@Param("articleId") Long articleId);
    
    @Query("SELECT SUM(ii.quantity) FROM InvoiceItem ii WHERE ii.articleId = :articleId")
    Integer sumQuantityByArticleId(@Param("articleId") Long articleId);
    
    // Date-based queries
    List<InvoiceItem> findByCreatedAtBetween(LocalDateTime startDateTime, LocalDateTime endDateTime);
    List<InvoiceItem> findByCreatedAtAfter(LocalDateTime dateTime);
    
    // Price range queries
    List<InvoiceItem> findByUnitPriceBetween(double minPrice, double maxPrice);
    List<InvoiceItem> findByLineTotalBetween(double minTotal, double maxTotal);
    
    // Quantity queries
    List<InvoiceItem> findByQuantityGreaterThan(Integer quantity);
    List<InvoiceItem> findByQuantityBetween(Integer minQuantity, Integer maxQuantity);
    
    // Category queries (only category exists in entity)
    List<InvoiceItem> findByArticleCategory(String category);
    List<InvoiceItem> findByArticleCategoryContainingIgnoreCase(String category);
    
    
}