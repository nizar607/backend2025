package com.example.stage24.roomplan.domain;

import com.example.stage24.user.domain.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "room_plans")
public class RoomPlan {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Column(nullable = false)
    private String name;
    
    @Column(length = 1000)
    private String description;
    
    @NotBlank
    @Column(nullable = false)
    private String filePath;
    
    @NotNull
    @Column(nullable = false)
    private Long fileSize;
    
    @NotBlank
    @Column(nullable = false)
    private String fileType;
    
    @NotBlank
    @Column(nullable = false)
    private String originalFileName;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @NotNull
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @NotNull
    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    public RoomPlan(String name, String description, String filePath, Long fileSize, String fileType, String originalFileName, User user) {
        this.name = name;
        this.description = description;
        this.filePath = filePath;
        this.fileSize = fileSize;
        this.fileType = fileType;
        this.originalFileName = originalFileName;
        this.user = user;
    }
}