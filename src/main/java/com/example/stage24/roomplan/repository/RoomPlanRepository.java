package com.example.stage24.roomplan.repository;

import com.example.stage24.roomplan.domain.RoomPlan;
import com.example.stage24.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomPlanRepository extends JpaRepository<RoomPlan, Long> {
    
    /**
     * Find all room plans for a specific user
     */
    List<RoomPlan> findByUserOrderByCreatedAtDesc(User user);
    
    /**
     * Find all room plans for a specific user by user ID
     */
    List<RoomPlan> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    /**
     * Find a room plan by ID and user (for security)
     */
    Optional<RoomPlan> findByIdAndUser(Long id, User user);
    
    /**
     * Find a room plan by ID and user ID (for security)
     */
    Optional<RoomPlan> findByIdAndUserId(Long id, Long userId);
    
    /**
     * Count room plans for a specific user
     */
    long countByUser(User user);
    
    /**
     * Find room plans by name containing (case insensitive)
     */
    List<RoomPlan> findByUserAndNameContainingIgnoreCaseOrderByCreatedAtDesc(User user, String name);
}