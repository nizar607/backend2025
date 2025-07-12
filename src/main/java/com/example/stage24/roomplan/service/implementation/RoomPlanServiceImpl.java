package com.example.stage24.roomplan.service.implementation;

import com.example.stage24.roomplan.domain.RoomPlan;
import com.example.stage24.roomplan.model.ResponseRoomPlan;
import com.example.stage24.roomplan.repository.RoomPlanRepository;
import com.example.stage24.roomplan.service.RoomPlanService;
import com.example.stage24.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoomPlanServiceImpl implements RoomPlanService {
    
    private final RoomPlanRepository roomPlanRepository;
    private final Path rootLocation = Paths.get("uploads/room-plans");
    
    @Override
    public RoomPlan saveRoomPlan(MultipartFile file, String name, String description, User user) throws Exception {
        try {
            // Initialize directory if it doesn't exist
            initializeDirectory();
            
            if (file.isEmpty()) {
                throw new RuntimeException("Failed to store empty file.");
            }
            
            String originalFilename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            
            // Validate file extension for 3D room plan files
            if (!isValidRoomPlanExtension(fileExtension)) {
                throw new RuntimeException("Cannot store file with extension " + fileExtension + 
                    ". Only room plan files (.json, .xml, .plan, .3d, .obj, .fbx, .dae, .gltf, .glb) are allowed.");
            }
            
            // Validate file size (max 50MB)
            if (file.getSize() > 50 * 1024 * 1024) {
                throw new RuntimeException("File size exceeds maximum limit of 50MB.");
            }
            
            // Generate unique filename
            String newFilename = UUID.randomUUID().toString() + fileExtension;
            Path destinationFile = this.rootLocation.resolve(Paths.get(newFilename))
                    .normalize().toAbsolutePath();
            
            // Security check
            if (!destinationFile.getParent().equals(this.rootLocation.toAbsolutePath())) {
                throw new RuntimeException("Cannot store file outside current directory.");
            }
            
            // Store the file
            Files.copy(file.getInputStream(), destinationFile, StandardCopyOption.REPLACE_EXISTING);
            
            // Create and save RoomPlan entity
            RoomPlan roomPlan = new RoomPlan(
                name,
                description,
                newFilename,
                file.getSize(),
                file.getContentType(),
                originalFilename,
                user
            );
            
            log.info("Room plan saved successfully: {} for user: {}", name, user.getEmail());
            return roomPlanRepository.save(roomPlan);
            
        } catch (IOException e) {
            log.error("Failed to store room plan file: {}", e.getMessage());
            throw new Exception("Failed to store room plan file.", e);
        }
    }
    
    @Override
    public List<RoomPlan> getUserRoomPlans(User user) {
        return roomPlanRepository.findByUserOrderByCreatedAtDesc(user);
    }
    
    @Override
    public List<RoomPlan> getUserRoomPlans(Long userId) {
        return roomPlanRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }
    
    @Override
    public Optional<RoomPlan> getRoomPlan(Long id) {
        return roomPlanRepository.findById(id);
    }
    
    @Override
    public Optional<RoomPlan> getRoomPlan(Long id, User user) {
        return roomPlanRepository.findByIdAndUser(id, user);
    }
    
    @Override
    public Resource downloadRoomPlan(Long id, User user) throws Exception {
        try {
            Optional<RoomPlan> roomPlanOpt = roomPlanRepository.findByIdAndUser(id, user);
            if (roomPlanOpt.isEmpty()) {
                throw new RuntimeException("Room plan not found or access denied.");
            }
            
            RoomPlan roomPlan = roomPlanOpt.get();
            Path file = rootLocation.resolve(roomPlan.getFilePath());
            Resource resource = new UrlResource(file.toUri());
            
            if (resource.exists() || resource.isReadable()) {
                log.info("Room plan downloaded: {} by user: {}", roomPlan.getName(), user.getEmail());
                return resource;
            } else {
                throw new RuntimeException("Could not read file: " + roomPlan.getFilePath());
            }
        } catch (MalformedURLException e) {
            throw new Exception("Could not read room plan file.", e);
        }
    }
    
    @Override
    public boolean deleteRoomPlan(Long id, User user) throws Exception {
        try {
            Optional<RoomPlan> roomPlanOpt = roomPlanRepository.findByIdAndUser(id, user);
            if (roomPlanOpt.isEmpty()) {
                throw new RuntimeException("Room plan not found or access denied.");
            }
            
            RoomPlan roomPlan = roomPlanOpt.get();
            
            // Delete file from filesystem
            Path file = rootLocation.resolve(roomPlan.getFilePath());
            Files.deleteIfExists(file);
            
            // Delete from database
            roomPlanRepository.delete(roomPlan);
            
            log.info("Room plan deleted: {} by user: {}", roomPlan.getName(), user.getEmail());
            return true;
            
        } catch (IOException e) {
            log.error("Failed to delete room plan file: {}", e.getMessage());
            throw new Exception("Failed to delete room plan file.", e);
        }
    }
    
    @Override
    public RoomPlan updateRoomPlan(Long id, String name, String description, User user) throws Exception {
        Optional<RoomPlan> roomPlanOpt = roomPlanRepository.findByIdAndUser(id, user);
        if (roomPlanOpt.isEmpty()) {
            throw new RuntimeException("Room plan not found or access denied.");
        }
        
        RoomPlan roomPlan = roomPlanOpt.get();
        roomPlan.setName(name);
        roomPlan.setDescription(description);
        
        log.info("Room plan updated: {} by user: {}", name, user.getEmail());
        return roomPlanRepository.save(roomPlan);
    }
    
    @Override
    public List<RoomPlan> searchRoomPlans(User user, String name) {
        return roomPlanRepository.findByUserAndNameContainingIgnoreCaseOrderByCreatedAtDesc(user, name);
    }
    
    @Override
    public long getUserRoomPlanCount(User user) {
        return roomPlanRepository.countByUser(user);
    }
    
    @Override
    public List<ResponseRoomPlan> getUserRoomPlansWithContent(User user) throws Exception {
        List<RoomPlan> roomPlans = roomPlanRepository.findByUserOrderByCreatedAtDesc(user);
        return roomPlans.stream()
                .map(this::convertToResponseRoomPlan)
                .toList();
    }
    
    @Override
    public Optional<ResponseRoomPlan> getRoomPlanWithContent(Long id, User user) throws Exception {
        Optional<RoomPlan> roomPlan = roomPlanRepository.findByIdAndUser(id, user);
        return roomPlan.map(this::convertToResponseRoomPlan);
    }
    
    private ResponseRoomPlan convertToResponseRoomPlan(RoomPlan roomPlan) {
        try {
            String content = readFileContent(roomPlan.getFilePath());
            return new ResponseRoomPlan(
                roomPlan.getId(),
                roomPlan.getName(),
                roomPlan.getDescription(),
                roomPlan.getFilePath(),
                roomPlan.getFileSize(),
                roomPlan.getFileType(),
                roomPlan.getOriginalFileName(),
                roomPlan.getUser(),
                roomPlan.getCreatedAt(),
                roomPlan.getUpdatedAt(),
                content
            );
        } catch (Exception e) {
            log.error("Failed to read file content for room plan {}: {}", roomPlan.getId(), e.getMessage());
            return new ResponseRoomPlan(
                roomPlan.getId(),
                roomPlan.getName(),
                roomPlan.getDescription(),
                roomPlan.getFilePath(),
                roomPlan.getFileSize(),
                roomPlan.getFileType(),
                roomPlan.getOriginalFileName(),
                roomPlan.getUser(),
                roomPlan.getCreatedAt(),
                roomPlan.getUpdatedAt(),
                "Error reading file content"
            );
        }
    }
    
    private String readFileContent(String fileName) throws Exception {
        try {
            Path path = this.rootLocation.resolve(fileName).normalize().toAbsolutePath();
            return Files.readString(path);
        } catch (IOException e) {
            throw new Exception("Failed to read file content: " + e.getMessage());
        }
    }
    
    private void initializeDirectory() {
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize folder for room plan uploads!", e);
        }
    }
    
    private boolean isValidRoomPlanExtension(String extension) {
        extension = extension.toLowerCase();
        return extension.equals(".json") ||
               extension.equals(".xml") ||
               extension.equals(".plan") ||
               extension.equals(".3d") ||
               extension.equals(".obj") ||
               extension.equals(".fbx") ||
               extension.equals(".dae") ||
               extension.equals(".gltf") ||
               extension.equals(".glb") ||
               extension.equals(".ifc") ||
               extension.equals(".dwg") ||
               extension.equals(".dxf");
    }
}