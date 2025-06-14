package com.example.stage24.user.repository;

import com.example.stage24.user.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

  @Query("SELECT r FROM Role r LEFT JOIN FETCH r.users WHERE r.name = :name")
  Optional<Role> findByName(@Param("name") RoleType name);

}
