package com.example.stage24.user.repository;

import com.example.stage24.user.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import org.springframework.stereotype.Repository;


@Repository
public interface AccessRepository extends JpaRepository<Access, Long> {
    Optional<Access> findByType(AccessType type);
    void deleteById(Long id);
    void deleteAll();
}
