package com.example.stage24.profile.controller;

import com.example.stage24.profile.dto.ProfileDTO;
import com.example.stage24.profile.dto.UpdateProfileDTO;
import com.example.stage24.shared.FileStorageService;
import com.example.stage24.shared.SharedServiceInterface;
import com.example.stage24.user.domain.User;
import com.example.stage24.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final SharedServiceInterface sharedService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final FileStorageService fileStorageService;

    /**
     * Get current user profile
     * Uses JWT token to identify the connected user and return their profile
     * details
     * 
     * @return ProfileDTO containing user details
     */
    @GetMapping
    public ResponseEntity<?> getCurrentUserProfile() {
        try {
            User currentUser = sharedService.getConnectedUser()
                    .orElseThrow(() -> new RuntimeException("User not authenticated"));

            ProfileDTO profileDTO = ProfileDTO.builder()
                    .id(currentUser.getId())
                    .firstName(currentUser.getFirstName())
                    .lastName(currentUser.getLastName())
                    .email(currentUser.getEmail())
                    .phoneNumber(currentUser.getPhoneNumber())
                    .address(currentUser.getAddress())
                    .image(currentUser.getImage())
                    .profileUrl("/api/profile/" + currentUser.getId())
                    .enabled(currentUser.isEnabled())
                    .subscribed(currentUser.isSubscribed())
                    .createdAt(currentUser.getCreatedAt())
                    .updatedAt(currentUser.getUpdatedAt())
                    .build();

            return ResponseEntity.ok(profileDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Error retrieving user profile: " + e.getMessage());
        }
    }

    /**
     * Update current user profile
     * Uses JWT token to identify the connected user and update their profile
     * 
     * @param firstName   user's first name
     * @param lastName    user's last name
     * @param email       user's email
     * @param phoneNumber user's phone number
     * @param address     user's address
     * @param password    user's new password (optional)
     * @param image       user's profile image file (optional)
     * @return updated ProfileDTO
     */
    @PutMapping
    public ResponseEntity<?> updateCurrentUserProfile(
            @RequestParam("firstName") String firstName,
            @RequestParam("lastName") String lastName,
            @RequestParam("email") String email,
            @RequestParam(value = "phoneNumber", required = false) String phoneNumber,
            @RequestParam(value = "address", required = false) String address,
            @RequestParam(value = "password", required = false) String password,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @RequestParam(value = "subscribed", required = false) String subscribed) {
        try {
            User currentUser = sharedService.getConnectedUser()
                    .orElseThrow(() -> new RuntimeException("User not authenticated"));

            // Handle image upload if provided
            String imageUrl = null;
            if (image != null && !image.isEmpty()) {
                imageUrl = fileStorageService.store(image);
            }

            // Update user fields
            currentUser.setFirstName(firstName);
            currentUser.setLastName(lastName);
            currentUser.setEmail(email);

            if (phoneNumber != null) {
                currentUser.setPhoneNumber(phoneNumber);
            }

            if (subscribed != null) {
                currentUser.setSubscribed(Boolean.parseBoolean(subscribed));
            }

            if (address != null) {
                currentUser.setAddress(address);
            }

            // Update password if provided
            if (password != null && !password.trim().isEmpty()) {
                currentUser.setPassword(passwordEncoder.encode(password));
            }

            // Update image if provided
            if (imageUrl != null) {
                currentUser.setImage(imageUrl);
            }

            // Save updated user
            User updatedUser = userRepository.save(currentUser);

            // Return updated profile
            ProfileDTO profileDTO = ProfileDTO.builder()
                    .id(updatedUser.getId())
                    .firstName(updatedUser.getFirstName())
                    .lastName(updatedUser.getLastName())
                    .email(updatedUser.getEmail())
                    .phoneNumber(updatedUser.getPhoneNumber())
                    .address(updatedUser.getAddress())
                    .image(updatedUser.getImage())
                    .profileUrl("/api/profile/" + updatedUser.getId())
                    .enabled(updatedUser.isEnabled())
                    .subscribed(updatedUser.isSubscribed())
                    .createdAt(updatedUser.getCreatedAt())
                    .updatedAt(updatedUser.getUpdatedAt())
                    .build();

            return ResponseEntity.ok(profileDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error updating user profile: " + e.getMessage());
        }
    }
}