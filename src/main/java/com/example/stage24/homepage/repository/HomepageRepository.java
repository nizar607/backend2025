// package com.example.stage24.homepage.repository;

// import com.example.stage24.homepage.model.Homepage;
// import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.data.jpa.repository.Query;
// import org.springframework.data.repository.query.Param;
// import org.springframework.stereotype.Repository;

// import java.util.List;
// import java.util.Optional;

// @Repository
// public interface HomepageRepository extends JpaRepository<Homepage, Long> {
    
//     /**
//      * Find homepage by company ID
//      */
//     Optional<Homepage> findByCompanyId(Long companyId);
    
//     /**
//      * Find homepage by company ID with all relations
//      */
//     @Query("SELECT h FROM Homepage h " +
//            "LEFT JOIN FETCH h.featuredProducts " +
//            "LEFT JOIN FETCH h.categoryItems " +
//            "LEFT JOIN FETCH h.experienceCards " +
//            "LEFT JOIN FETCH h.galleryProducts " +
//            "LEFT JOIN FETCH h.featureItems " +
//            "LEFT JOIN FETCH h.productItems " +
//            "LEFT JOIN FETCH h.statistics " +
//            "LEFT JOIN FETCH h.whyChooseFeatures " +
//            "LEFT JOIN FETCH h.diningInfo " +
//            "WHERE h.companyId = :companyId")
//     Optional<Homepage> findByCompanyIdWithAllRelations(@Param("companyId") Long companyId);
    
//     /**
//      * Check if homepage exists for a company
//      */
//     boolean existsByCompanyId(Long companyId);
    
//     /**
//      * Delete homepage by company ID
//      */
//     void deleteByCompanyId(Long companyId);
    
//     /**
//      * Find all active homepages
//      */
//     List<Homepage> findByIsActiveTrue();
    
//     /**
//      * Find all homepages by company IDs
//      */
//     List<Homepage> findByCompanyIdIn(List<Long> companyIds);
// }