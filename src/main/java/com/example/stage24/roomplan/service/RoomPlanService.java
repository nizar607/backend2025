package com.example.stage24.roomplan.service;

import com.example.stage24.roomplan.domain.RoomPlan;
import com.example.stage24.roomplan.model.ResponseRoomPlan;
import com.example.stage24.user.domain.User;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface RoomPlanService {
    
    /**
     * Save a room plan with file upload
     */
    RoomPlan saveRoomPlan(MultipartFile file, String name, String description, User user) throws Exception;
    
    /**
     * Get all room plans for a user
     */
    List<RoomPlan> getUserRoomPlans(User user);
    
    /**
     * Get all room plans for a user by user ID
     */
    List<RoomPlan> getUserRoomPlans(Long userId);
    
    /**
     * Get a specific room plan by ID
     */
    Optional<RoomPlan> getRoomPlan(Long id);
    
    /**
     * Get a specific room plan by ID and user (for security)
     */
    Optional<RoomPlan> getRoomPlan(Long id, User user);
    
    /**
     * Download room plan file
     */
    Resource downloadRoomPlan(Long id, User user) throws Exception;
    
    /**
     * Delete a room plan
     */
    boolean deleteRoomPlan(Long id, User user) throws Exception;
    
    /**
     * Update room plan metadata
     */
    RoomPlan updateRoomPlan(Long id, String name, String description, User user) throws Exception;
    
    /**
     * Search room plans by name
     */
    List<RoomPlan> searchRoomPlans(User user, String name);
    
    /**
     * Get room plan count for user
     */
    long getUserRoomPlanCount(User user);
    
    /**
     * Get all room plans for a user with file content
     */
    List<ResponseRoomPlan> getUserRoomPlansWithContent(User user) throws Exception;
    
    /**
     * Get a specific room plan by ID and user with file content
     */
    Optional<ResponseRoomPlan> getRoomPlanWithContent(Long id, User user) throws Exception;
}