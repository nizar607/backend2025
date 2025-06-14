package com.example.stage24.order.repository;

import com.example.stage24.article.domain.Article;
import com.example.stage24.order.domain.Order;
import com.example.stage24.order.domain.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    
    // Find order items by order
    List<OrderItem> findByOrder(Order order);
    
    // Find order items by article
    List<OrderItem> findByArticle(Article article);
    
    // Find specific order item by order and article
    Optional<OrderItem> findByOrderAndArticle(Order order, Article article);
    
    // Count items in an order
    long countByOrder(Order order);
    
    // Get total quantity for an order
    @Query("SELECT COALESCE(SUM(oi.quantity), 0) FROM OrderItem oi WHERE oi.order = :order")
    Integer getTotalQuantityByOrder(@Param("order") Order order);
    
    // Get total amount for an order (sum of all item totals)
    @Query("SELECT COALESCE(SUM(oi.totalPrice), 0) FROM OrderItem oi WHERE oi.order = :order")
    Double getTotalAmountByOrder(@Param("order") Order order);
    
    // Find order items by product name (for search)
    @Query("SELECT oi FROM OrderItem oi WHERE LOWER(oi.productName) LIKE LOWER(CONCAT('%', :productName, '%'))")
    List<OrderItem> findByProductNameContaining(@Param("productName") String productName);
    
    // Find order items with quantity greater than specified value
    @Query("SELECT oi FROM OrderItem oi WHERE oi.quantity >= :minQuantity")
    List<OrderItem> findItemsWithMinQuantity(@Param("minQuantity") Integer minQuantity);
    
    // Find order items with total price greater than specified value
    @Query("SELECT oi FROM OrderItem oi WHERE oi.totalPrice >= :minAmount")
    List<OrderItem> findItemsWithMinAmount(@Param("minAmount") Double minAmount);
    
    // Get most popular articles (by quantity sold)
    @Query("SELECT oi.article, SUM(oi.quantity) as totalSold FROM OrderItem oi " +
           "GROUP BY oi.article ORDER BY totalSold DESC")
    List<Object[]> findMostPopularArticles();
    
    // Get best selling articles (by revenue)
    @Query("SELECT oi.article, SUM(oi.totalPrice) as totalRevenue FROM OrderItem oi " +
           "GROUP BY oi.article ORDER BY totalRevenue DESC")
    List<Object[]> findBestSellingArticles();
    
    // Get total quantity sold for a specific article
    @Query("SELECT COALESCE(SUM(oi.quantity), 0) FROM OrderItem oi WHERE oi.article = :article")
    Integer getTotalQuantitySoldByArticle(@Param("article") Article article);
    
    // Get total revenue for a specific article
    @Query("SELECT COALESCE(SUM(oi.totalPrice), 0) FROM OrderItem oi WHERE oi.article = :article")
    Double getTotalRevenueByArticle(@Param("article") Article article);
    
    // Delete all order items for a specific order
    void deleteByOrder(Order order);
    
    // Find order items by order and sort by creation date
    List<OrderItem> findByOrderOrderByCreatedAtAsc(Order order);
}