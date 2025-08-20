package com.example.stage24.homepage.repository;

import com.example.stage24.homepage.model.Homepage1;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface Homepage1Repository extends JpaRepository<Homepage1, Long> {
    
    /**
     * Find homepage1 by company ID
     */
    @Query("SELECT h FROM Homepage1 h WHERE h.company.id = :companyId")
    Optional<Homepage1> findByCompanyId(@Param("companyId") Long companyId);
    
    /**
     * Find homepage1 by company ID with company details
     */
    @Query("SELECT h FROM Homepage1 h " +
           "LEFT JOIN FETCH h.company " +
           "WHERE h.company.id = :companyId")
    Optional<Homepage1> findByCompanyIdWithCompany(@Param("companyId") Long companyId);
    
    /**
     * Check if homepage1 exists for a company
     */
    @Query("SELECT COUNT(h) > 0 FROM Homepage1 h WHERE h.company.id = :companyId")
    boolean existsByCompanyId(@Param("companyId") Long companyId);
    
    /**
     * Delete homepage1 by company ID
     */
    @Query("DELETE FROM Homepage1 h WHERE h.company.id = :companyId")
    void deleteByCompanyId(@Param("companyId") Long companyId);
    
    /**
     * Find all active homepage1s
     */
    List<Homepage1> findByIsActiveTrue();
    
    /**
     * Find homepage1s by multiple company IDs
     */
    @Query("SELECT h FROM Homepage1 h WHERE h.company.id IN :companyIds")
    List<Homepage1> findByCompanyIdIn(@Param("companyIds") List<Long> companyIds);
    
    /**
     * Find homepage1 by ID with company details
     */
    @Query("SELECT h FROM Homepage1 h " +
           "LEFT JOIN FETCH h.company " +
           "WHERE h.id = :id")
    Optional<Homepage1> findByIdWithCompany(@Param("id") Long id);
}