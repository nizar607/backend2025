package com.example.stage24.user.repository;

import com.example.stage24.user.domain.*;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;



import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {


    Optional<User> findByEmail(String email);

    Boolean existsByEmail(String email);

    List<User> findAllByRolesContaining(Role role);

    /**
     * Find all users by company and role type
     */
    @Query("SELECT u FROM User u JOIN u.roles r JOIN Company c JOIN c.users cu WHERE cu.id = u.id AND r.name = :roleType AND c.id = :companyId")
    List<User> findByCompanyAndRoleType(@Param("companyId") Long companyId, @Param("roleType") RoleType roleType);

}
