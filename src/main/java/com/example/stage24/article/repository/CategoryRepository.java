package com.example.stage24.article.repository;

import com.example.stage24.article.domain.Category;
import com.example.stage24.company.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * Find categories by company
     */
    public Optional<List<Category>> findByCompany(Company company);

    /**
     * Find categories by company ID
     */
    @Query("SELECT c FROM Category c WHERE c.company.id = :companyId")
    public Optional<List<Category>> findByCompanyId(@Param("companyId") Long companyId);
}