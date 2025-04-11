package com.example.stage24.user.service.interfaces;

import com.example.stage24.user.domain.Role;
import com.example.stage24.user.domain.User;
import com.example.stage24.user.model.request.LoginRequest;
import com.example.stage24.user.model.request.SignupRequest;
import com.example.stage24.user.model.request.TokenRefreshRequest;
import com.example.stage24.user.model.response.LoginResponse;
import com.example.stage24.user.model.response.TokenRefreshResponse;

import java.util.List;
import java.util.Optional;


public interface IUserService {

    public LoginResponse authenticateUser(LoginRequest loginRequest);

    public TokenRefreshResponse refreshtoken(TokenRefreshRequest request);

    public User registerUser(SignupRequest signUpRequest);


    public Role addRole(Role role);

    public List<User> getUsers();

    public Optional<User> getUserById(Long id);

    public Optional<User> getConnectedUser();
}
