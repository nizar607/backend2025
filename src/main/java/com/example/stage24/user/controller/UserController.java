package com.example.stage24.user.controller;


import com.example.stage24.shared.SharedServiceInterface;
import com.example.stage24.user.domain.User;
import com.example.stage24.user.model.response.UserStatsResponse;
import com.example.stage24.user.service.interfaces.IUserService;

import lombok.AllArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Validated
@RequestMapping("/api/clients")
@AllArgsConstructor
@RestController
@Slf4j
public class UserController {

    IUserService userService;
    SharedServiceInterface sharedService;

    @GetMapping("/by-company")
    @PreAuthorize("hasRole('ADMIN') or hasRole('AGENT')")
    public ResponseEntity<List<User>> getClientUsersByCompany() {
        try {
            List<User> clientUsers = userService.getClientUsersByCompany();
            return ResponseEntity.ok(clientUsers);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/enabled/{clientId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('AGENT')")
    public ResponseEntity<User> updateClientEnabledStatus(
            @PathVariable Long clientId,
            @RequestParam boolean enabled) {
        try {
            User updatedClient = userService.updateClientEnabledStatus(clientId, enabled);
            return ResponseEntity.ok(updatedClient);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN') or hasRole('AGENT')")
    public ResponseEntity<UserStatsResponse> getUserStats() {
        try {
            UserStatsResponse stats = userService.getUserStats();
            return ResponseEntity.ok(stats);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

}
