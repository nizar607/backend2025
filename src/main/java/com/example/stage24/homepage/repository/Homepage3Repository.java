package com.example.stage24.homepage.repository;

import com.example.stage24.homepage.model.Homepage3;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface Homepage3Repository extends JpaRepository<Homepage3, Long> {
    
    Optional<Homepage3> findByCompanyId(Long companyId);
    
    boolean existsByCompanyId(Long companyId);
    
    void deleteByCompanyId(Long companyId);
    
    long countByCompanyId(Long companyId);
    
    @Query("SELECT h FROM Homepage3 h WHERE h.isActive = true")
    List<Homepage3> findAllActive();
    
    @Query("SELECT h FROM Homepage3 h JOIN h.company c JOIN c.users u WHERE u.id = :userId")
    Optional<Homepage3> findByConnectedUser(@Param("userId") Long userId);
}