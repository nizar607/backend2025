package com.example.stage24.user.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DataResponse {

    private String id;
    private String type = "Bearer";
    private String firstName;
    private String lastName;
    private String email;
    private String refreshToken;
    private List<String> roles;
    private List<String> accesses;

    public DataResponse(String type, String firstName, String lastName, String email, String refreshToken, List<String> roles,List<String> accesses) {
        this.type = type;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.refreshToken = refreshToken;
        this.roles = roles;
        this.accesses = accesses;
    }
}
