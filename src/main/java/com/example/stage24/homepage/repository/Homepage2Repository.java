package com.example.stage24.homepage.repository;

import com.example.stage24.homepage.model.Homepage2;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface Homepage2Repository extends JpaRepository<Homepage2, Long> {
    
    /**
     * Find homepage2 by company ID
     */
    @Query("SELECT h FROM Homepage2 h WHERE h.company.id = :companyId")
    Optional<Homepage2> findByCompanyId(@Param("companyId") Long companyId);
    
    /**
     * Find homepage2 by company ID with company details
     */
    @Query("SELECT h FROM Homepage2 h " +
           "LEFT JOIN FETCH h.company " +
           "WHERE h.company.id = :companyId")
    Optional<Homepage2> findByCompanyIdWithCompany(@Param("companyId") Long companyId);
    
    /**
     * Check if homepage2 exists for a company
     */
    @Query("SELECT COUNT(h) > 0 FROM Homepage2 h WHERE h.company.id = :companyId")
    boolean existsByCompanyId(@Param("companyId") Long companyId);
    
    /**
     * Delete homepage2 by company ID
     */
    @Query("DELETE FROM Homepage2 h WHERE h.company.id = :companyId")
    void deleteByCompanyId(@Param("companyId") Long companyId);
    
    /**
     * Find all active homepage2 entries
     */
    @Query("SELECT h FROM Homepage2 h WHERE h.isActive = true")
    List<Homepage2> findAllActive();
    
    /**
     * Find homepage2 by connected user (through company relationship)
     */
    @Query("SELECT h FROM Homepage2 h " +
           "JOIN h.company c " +
           "JOIN c.users u " +
           "WHERE u.id = :userId")
    Optional<Homepage2> findByConnectedUser(@Param("userId") Long userId);
    
    /**
     * Count homepage2 entries by company ID
     */
    @Query("SELECT COUNT(h) FROM Homepage2 h WHERE h.company.id = :companyId")
    long countByCompanyId(@Param("companyId") Long companyId);
}