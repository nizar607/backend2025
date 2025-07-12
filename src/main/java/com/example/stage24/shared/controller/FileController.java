package com.example.stage24.shared.controller;

import com.example.stage24.shared.FileStorageService;
import com.example.stage24.roomplan.domain.RoomPlan;
import com.example.stage24.roomplan.model.ResponseRoomPlan;
import com.example.stage24.roomplan.service.RoomPlanService;
import com.example.stage24.shared.SharedServiceInterface;
import com.example.stage24.user.domain.User;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("/api/files")
public class FileController {

    private FileStorageService fileStorageService;
    private RoomPlanService roomPlanService;
    private SharedServiceInterface sharedService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {

            String filename = fileStorageService.store(file);
            return ResponseEntity.status(HttpStatus.OK)
                    .body("Uploaded the file successfully: " + filename);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED)
                    .body("Could not upload the file: " + file.getOriginalFilename() + "!");
        }
    }

    @GetMapping()
    public ResponseEntity<List<String>> getListFiles() {
        List<String> fileInfos = fileStorageService.loadAll()
                .map(path -> MvcUriComponentsBuilder
                        .fromMethodName(FileController.class, "getFile", path.getFileName().toString())
                        .build().toString())
                .collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.OK)
                .body(fileInfos);
    }

    @GetMapping("/{filename:.+}")
    public ResponseEntity<Resource> getFile(@PathVariable String filename) {
        Resource file = fileStorageService.loadAsResource(filename);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                .body(file);
    }

    @DeleteMapping("/{filename:.+}")
    public ResponseEntity<String> deleteFile(@PathVariable String filename) {
        try {
            fileStorageService.delete(filename);
            return ResponseEntity.status(HttpStatus.OK)
                    .body("Deleted the file successfully: " + filename);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED)
                    .body("Could not delete the file: " + filename + "!");
        }
    }

    // Room Plan Endpoints
    
    @PostMapping("/room-plans/upload")
    public ResponseEntity<?> uploadRoomPlan(
            @RequestParam("file") MultipartFile file,
            @RequestParam("name") String name,
            @RequestParam(value = "description", required = false) String description) {
        try {
            User user = sharedService.getConnectedUser()
                    .orElseThrow(() -> new RuntimeException("User not authenticated"));
            
            RoomPlan roomPlan = roomPlanService.saveRoomPlan(file, name, description, user);
            
            return ResponseEntity.status(HttpStatus.OK)
                    .body(roomPlan);
        } catch (Exception e) {
            log.error("Failed to upload room plan: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Could not upload room plan: " + e.getMessage());
        }
    }
    
    @GetMapping("/room-plans")
    public ResponseEntity<?> getUserRoomPlans() {
        try {
            User user = sharedService.getConnectedUser()
                    .orElseThrow(() -> new RuntimeException("User not authenticated"));
            
            List<ResponseRoomPlan> roomPlans = roomPlanService.getUserRoomPlansWithContent(user);
            
            return ResponseEntity.status(HttpStatus.OK)
                    .body(roomPlans);
        } catch (Exception e) {
            log.error("Failed to get room plans: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Could not retrieve room plans: " + e.getMessage());
        }
    }
    
    @GetMapping("/room-plans/{id}")
    public ResponseEntity<?> getRoomPlan(@PathVariable Long id) {
        try {
            User user = sharedService.getConnectedUser()
                    .orElseThrow(() -> new RuntimeException("User not authenticated"));
            
            Optional<ResponseRoomPlan> roomPlan = roomPlanService.getRoomPlanWithContent(id, user);
            
            if (roomPlan.isPresent()) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(roomPlan.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Room plan not found or access denied");
            }
        } catch (Exception e) {
            log.error("Failed to get room plan: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Could not retrieve room plan: " + e.getMessage());
        }
    }
    
    @GetMapping("/room-plans/{id}/download")
    public ResponseEntity<Resource> downloadRoomPlan(@PathVariable Long id) {
        try {
            User user = sharedService.getConnectedUser()
                    .orElseThrow(() -> new RuntimeException("User not authenticated"));
            
            Optional<RoomPlan> roomPlanOpt = roomPlanService.getRoomPlan(id, user);
            if (roomPlanOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            RoomPlan roomPlan = roomPlanOpt.get();
            Resource file = roomPlanService.downloadRoomPlan(id, user);
            
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, 
                            "attachment; filename=\"" + roomPlan.getOriginalFileName() + "\"")
                    .body(file);
        } catch (Exception e) {
            log.error("Failed to download room plan: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PutMapping("/room-plans/{id}")
    public ResponseEntity<?> updateRoomPlan(
            @PathVariable Long id,
            @RequestParam("name") String name,
            @RequestParam(value = "description", required = false) String description) {
        try {
            User user = sharedService.getConnectedUser()
                    .orElseThrow(() -> new RuntimeException("User not authenticated"));
            
            RoomPlan updatedRoomPlan = roomPlanService.updateRoomPlan(id, name, description, user);
            
            return ResponseEntity.status(HttpStatus.OK)
                    .body(updatedRoomPlan);
        } catch (Exception e) {
            log.error("Failed to update room plan: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Could not update room plan: " + e.getMessage());
        }
    }
    
    @DeleteMapping("/room-plans/{id}")
    public ResponseEntity<?> deleteRoomPlan(@PathVariable Long id) {
        try {
            User user = sharedService.getConnectedUser()
                    .orElseThrow(() -> new RuntimeException("User not authenticated"));
            
            boolean deleted = roomPlanService.deleteRoomPlan(id, user);
            
            if (deleted) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body("Room plan deleted successfully");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Room plan not found or access denied");
            }
        } catch (Exception e) {
            log.error("Failed to delete room plan: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Could not delete room plan: " + e.getMessage());
        }
    }
    
    @GetMapping("/room-plans/search")
    public ResponseEntity<?> searchRoomPlans(@RequestParam("name") String name) {
        try {
            User user = sharedService.getConnectedUser()
                    .orElseThrow(() -> new RuntimeException("User not authenticated"));
            
            List<RoomPlan> roomPlans = roomPlanService.searchRoomPlans(user, name);
            
            return ResponseEntity.status(HttpStatus.OK)
                    .body(roomPlans);
        } catch (Exception e) {
            log.error("Failed to search room plans: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Could not search room plans: " + e.getMessage());
        }
    }
    
    @GetMapping("/room-plans/count")
    public ResponseEntity<?> getUserRoomPlanCount() {
        try {
            User user = sharedService.getConnectedUser()
                    .orElseThrow(() -> new RuntimeException("User not authenticated"));
            
            long count = roomPlanService.getUserRoomPlanCount(user);
            
            return ResponseEntity.status(HttpStatus.OK)
                    .body(count);
        } catch (Exception e) {
            log.error("Failed to get room plan count: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Could not get room plan count: " + e.getMessage());
        }
    }
}