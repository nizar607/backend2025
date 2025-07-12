package com.example.stage24.roomplan.model;

import com.example.stage24.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseRoomPlan {
    
    private Long id;
    private String name;
    private String description;
    private String filePath;
    private Long fileSize;
    private String fileType;
    private String originalFileName;
    private User user;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String content; // New field for JSON file content
    

}