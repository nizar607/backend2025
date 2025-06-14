package com.example.stage24.invoice.repository;

import com.example.stage24.article.domain.Article;
import com.example.stage24.invoice.domain.Invoice;
import com.example.stage24.invoice.domain.InvoiceItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceItemRepository extends JpaRepository<InvoiceItem, Long> {
    
    // Find invoice items by invoice
    List<InvoiceItem> findByInvoice(Invoice invoice);
    
    // Find invoice items by article
    List<InvoiceItem> findByArticle(Article article);
    
    // Find specific invoice item by invoice and article
    Optional<InvoiceItem> findByInvoiceAndArticle(Invoice invoice, Article article);
    
    // Count items in an invoice
    long countByInvoice(Invoice invoice);
    
    // Get total quantity for an invoice
    @Query("SELECT COALESCE(SUM(ii.quantity), 0) FROM InvoiceItem ii WHERE ii.invoice = :invoice")
    Integer getTotalQuantityByInvoice(@Param("invoice") Invoice invoice);
    
    // Get subtotal amount for an invoice (sum of all item totals before tax)
    @Query("SELECT COALESCE(SUM(ii.totalPrice), 0) FROM InvoiceItem ii WHERE ii.invoice = :invoice")
    Double getSubtotalByInvoice(@Param("invoice") Invoice invoice);
    
    // Get total tax amount for an invoice
    @Query("SELECT COALESCE(SUM(ii.taxAmount), 0) FROM InvoiceItem ii WHERE ii.invoice = :invoice")
    Double getTotalTaxByInvoice(@Param("invoice") Invoice invoice);
    
    // Get total discount amount for an invoice
    @Query("SELECT COALESCE(SUM(ii.discountAmount), 0) FROM InvoiceItem ii WHERE ii.invoice = :invoice")
    Double getTotalDiscountByInvoice(@Param("invoice") Invoice invoice);
    
    // Get line total for an invoice (sum of all line totals)
    @Query("SELECT COALESCE(SUM(ii.lineTotal), 0) FROM InvoiceItem ii WHERE ii.invoice = :invoice")
    Double getLineTotalByInvoice(@Param("invoice") Invoice invoice);
    
    // Find invoice items by product name (for search)
    @Query("SELECT ii FROM InvoiceItem ii WHERE LOWER(ii.productName) LIKE LOWER(CONCAT('%', :productName, '%'))")
    List<InvoiceItem> findByProductNameContaining(@Param("productName") String productName);
    
    // Find invoice items with quantity greater than specified value
    @Query("SELECT ii FROM InvoiceItem ii WHERE ii.quantity >= :minQuantity")
    List<InvoiceItem> findItemsWithMinQuantity(@Param("minQuantity") Integer minQuantity);
    
    // Find invoice items with line total greater than specified value
    @Query("SELECT ii FROM InvoiceItem ii WHERE ii.lineTotal >= :minAmount")
    List<InvoiceItem> findItemsWithMinAmount(@Param("minAmount") Double minAmount);
    
    // Find invoice items with discount
    @Query("SELECT ii FROM InvoiceItem ii WHERE ii.discountAmount > 0")
    List<InvoiceItem> findItemsWithDiscount();
    
    // Find invoice items with tax
    @Query("SELECT ii FROM InvoiceItem ii WHERE ii.taxAmount > 0")
    List<InvoiceItem> findItemsWithTax();
    
    // Get most invoiced articles (by quantity)
    @Query("SELECT ii.article, SUM(ii.quantity) as totalInvoiced FROM InvoiceItem ii " +
           "GROUP BY ii.article ORDER BY totalInvoiced DESC")
    List<Object[]> findMostInvoicedArticles();
    
    // Get highest revenue articles from invoices (by line total)
    @Query("SELECT ii.article, SUM(ii.lineTotal) as totalRevenue FROM InvoiceItem ii " +
           "GROUP BY ii.article ORDER BY totalRevenue DESC")
    List<Object[]> findHighestRevenueArticles();
    
    // Get total quantity invoiced for a specific article
    @Query("SELECT COALESCE(SUM(ii.quantity), 0) FROM InvoiceItem ii WHERE ii.article = :article")
    Integer getTotalQuantityInvoicedByArticle(@Param("article") Article article);
    
    // Get total revenue invoiced for a specific article
    @Query("SELECT COALESCE(SUM(ii.lineTotal), 0) FROM InvoiceItem ii WHERE ii.article = :article")
    Double getTotalRevenueInvoicedByArticle(@Param("article") Article article);
    
    // Delete all invoice items for a specific invoice
    void deleteByInvoice(Invoice invoice);
    
    // Find invoice items by invoice and sort by creation date
    List<InvoiceItem> findByInvoiceOrderByCreatedAtAsc(Invoice invoice);
    
    // Find invoice items by product name (since productSku doesn't exist)
    List<InvoiceItem> findByProductName(String productName);
    
    // Get average unit price for a specific article across all invoices
    @Query("SELECT AVG(ii.unitPrice) FROM InvoiceItem ii WHERE ii.article = :article")
    Double getAverageUnitPriceByArticle(@Param("article") Article article);
    
    // Find invoice items with custom pricing (different from article price)
    @Query("SELECT ii FROM InvoiceItem ii WHERE ii.unitPrice != ii.article.price")
    List<InvoiceItem> findItemsWithCustomPricing();
}