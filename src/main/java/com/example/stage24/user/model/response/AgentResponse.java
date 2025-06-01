package com.example.stage24.model.response.user;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AgentResponse {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String phoneNumber;
    private String address;
    private List<String> accesses;
    private String image;
    private LocalDateTime createdAt;

}




