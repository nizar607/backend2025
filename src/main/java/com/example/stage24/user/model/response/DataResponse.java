package com.example.stage24.user.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
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
    private boolean hasCompany;
    private boolean hasAboutUs;
    private boolean enabled;


    public DataResponse(String type, String firstName, String lastName, String email, String refreshToken,
            List<String> roles, List<String> accesses) {
        this.type = type;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.refreshToken = refreshToken;
        this.roles = roles;
        this.accesses = accesses;
    }

    public DataResponse(String id, String type, String firstName, String lastName, String email, String refreshToken,
            List<String> roles, List<String> accesses) {
        this.type = type;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.refreshToken = refreshToken;
        this.roles = roles;
        this.accesses = accesses;
    }

    public DataResponse(String id, String type, String firstName, String lastName, String email, String refreshToken,
            List<String> roles, List<String> accesses, boolean hasCompany, boolean hasAboutUs, boolean enabled) {
        this.id = id;
        this.type = type;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.refreshToken = refreshToken;
        this.roles = roles;
        this.accesses = accesses;
        this.hasCompany = hasCompany;
        this.hasAboutUs = hasAboutUs;
        this.enabled = enabled;
    }

}
