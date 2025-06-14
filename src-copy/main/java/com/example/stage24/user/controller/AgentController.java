package com.example.stage24.user.controller;


import com.example.stage24.shared.FileStorageService;
import com.example.stage24.shared.SharedServiceInterface;
import com.example.stage24.user.domain.User;
import com.example.stage24.user.model.request.NewAgentRequest;
import com.example.stage24.user.model.response.AgentResponse;
import com.example.stage24.user.service.interfaces.AgentServiceInterface;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;

//@CrossOrigin(origins = "*", maxAge = 3600)
@Validated
@RequestMapping("/api/agent")
@AllArgsConstructor
@RestController
@Slf4j
public class AgentController {

    AgentServiceInterface agentService;
    SharedServiceInterface sharedService;
    FileStorageService fileStorageService;


    @PostMapping("/upload")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) throws IOException {
        String result = fileStorageService.store(file);
        log.info("file here " + result);
        return ResponseEntity.ok(
                result
        );
    }


    @PostMapping("/")
    public ResponseEntity<?> createAgent(
            @RequestParam("firstName") String firstName,
            @RequestParam("lastName") String lastName,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam("phoneNumber") String phoneNumber,
            @RequestParam("address") String address,
            @RequestParam("accesses") List<String> accesses,
            @RequestParam(value = "file", required = false) MultipartFile image
    ) throws IOException {

        String result = fileStorageService.store(image);

        NewAgentRequest agentRequest = new NewAgentRequest(firstName, lastName, email, password, phoneNumber, address, accesses, result);
        log.info("agentRequest " + " " + agentRequest);
        User user = agentService.registerAgent(agentRequest);

        return ResponseEntity.ok(
                user
        );
    }


    @PutMapping("/")
    public ResponseEntity<?> updateAgent(
            @RequestParam("id") long id,
            @RequestParam("firstName") String firstName,
            @RequestParam("lastName") String lastName,
            @RequestParam("email") String email,
            @RequestParam(value = "password", required = false) String password,
            @RequestParam("phoneNumber") String phoneNumber,
            @RequestParam("address") String address,
            @RequestParam("accesses") List<String> accesses,
            @RequestParam(value = "file", required = false) MultipartFile image
    ) throws IOException {
        String result = null;
        if (image != null)
            result = fileStorageService.store(image);

        NewAgentRequest agentRequest = new NewAgentRequest(
                firstName,
                lastName,
                email,
                password == null ? "" : password,  // Handle null password
                phoneNumber,
                address,
                accesses,
                result
        );

        log.info("agentRequest: " + agentRequest);
        User user = agentService.updateAgent(id, agentRequest);

        return ResponseEntity.ok(user);
    }



    @GetMapping("/")
    public ResponseEntity<?> getAgents() {
        return ResponseEntity.ok(
                agentService.getAgents().stream().map((agent) ->
                        new AgentResponse(agent.getId(), agent.getFirstName(), agent.getLastName(), agent.getEmail(), agent.getPassword(), agent.getPhoneNumber(), agent.getAddress(), agent.getAccesses().stream().map(a -> a.getType().name()).toList(), agent.getImage(), agent.getCreatedAt())
                ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getAgentById(@PathVariable String id) {
        log.info("getAgentById");
        return ResponseEntity.ok(
                null
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateAgent(@PathVariable String id) {

        return ResponseEntity.ok(
                null
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAgent(@PathVariable long id) {
        log.info("deleteAgent");

        return ResponseEntity.ok(
                agentService.deleteAgent(id)
        );
    }






}
