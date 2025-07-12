package com.example.stage24.homepage.repository;

import com.example.stage24.homepage.model.HomepageContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HomepageContentRepository extends JpaRepository<HomepageContent, Long> {
    
    /**
     * Find homepage content by user ID
     */
    Optional<HomepageContent> findByUserId(Long userId);
    
    /**
     * Check if homepage content exists for a user
     */
    boolean existsByUserId(Long userId);
    
    /**
     * Delete homepage content by user ID
     */
    void deleteByUserId(Long userId);
    
    /**
     * Find homepage content with all related entities
     */
    @Query("SELECT hc FROM HomepageContent hc " +
           "LEFT JOIN FETCH hc.heroProducts hp " +
           "LEFT JOIN FETCH hc.companyInfo ci " +
           "LEFT JOIN FETCH hc.advantages a " +
           "LEFT JOIN FETCH hc.spotlightProducts sp " +
           "LEFT JOIN FETCH hc.artisanInfo ai " +
           "WHERE hc.userId = :userId")
    Optional<HomepageContent> findByUserIdWithAllRelations(@Param("userId") Long userId);
    
    /**
     * Find homepage content by ID with all related entities
     */
    @Query("SELECT hc FROM HomepageContent hc " +
           "LEFT JOIN FETCH hc.heroProducts hp " +
           "LEFT JOIN FETCH hc.companyInfo ci " +
           "LEFT JOIN FETCH hc.advantages a " +
           "LEFT JOIN FETCH hc.spotlightProducts sp " +
           "LEFT JOIN FETCH hc.artisanInfo ai " +
           "WHERE hc.id = :id")
    Optional<HomepageContent> findByIdWithAllRelations(@Param("id") Long id);
}