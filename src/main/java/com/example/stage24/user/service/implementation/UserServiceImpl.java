package com.example.stage24.user.service.implementation;


import com.example.stage24.security.jwt.JwtUtils;


import com.example.stage24.security.services.RefreshTokenService;
import com.example.stage24.security.services.UserDetailsImpl;
import com.example.stage24.user.domain.RefreshToken;
import com.example.stage24.user.domain.Role;
import com.example.stage24.user.domain.RoleType;
import com.example.stage24.user.domain.User;
import com.example.stage24.user.model.request.LoginRequest;
import com.example.stage24.user.model.request.SignupRequest;
import com.example.stage24.user.model.request.TokenRefreshRequest;
import com.example.stage24.user.model.response.DataResponse;
import com.example.stage24.user.model.response.LoginResponse;
import com.example.stage24.user.model.response.TokenRefreshResponse;
import com.example.stage24.user.repository.RoleRepository;
import com.example.stage24.user.repository.UserRepository;
import com.example.stage24.user.service.interfaces.IUserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@Slf4j
@AllArgsConstructor
public class UserServiceImpl implements IUserService {

    AuthenticationManager authenticationManager;

    UserRepository userRepository;

    RoleRepository roleRepository;

    PasswordEncoder encoder;

    RefreshTokenService refreshTokenService;

    JwtUtils jwtUtils;



    @Override
    public LoginResponse authenticateUser(LoginRequest loginRequest) {
        try {


            Authentication authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

            String jwt = jwtUtils.generateJwtToken(userDetails);

            List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority())
                    .collect(Collectors.toList());

            RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());

            User user = userRepository.findByEmail(userDetails.getEmail()).orElseThrow(() -> new RuntimeException("User Not Found with username: " + userDetails.getUsername()));
		/*
		private String token;
		private String type = "Bearer";
		private String id;
		private String username;
		private String email;
		private String refreshToken;
		private List<String> roles;



		public class LoginResponse {
        private String status;
        private String token;
        private DataResponse data;
}

		*/

        ArrayList<String> _roles = new ArrayList<>();
        _roles.add("ROLE_ADMIN");


            return new LoginResponse(
                    "success",
                    jwt,
                    new DataResponse(
                            "",
                            "Bearer",
                            user.getFirstName(),
                            user.getLastName(),
                            user.getEmail(),
                            refreshToken.getToken(),
                            user.getRoles().stream().map((role) -> role.getName().name()).collect(Collectors.toList()),
                            user.getAccesses().stream().map((access) -> access.getType().name()).collect(Collectors.toList())
                    ));
        } catch (Exception e) {
            return new LoginResponse(
                    "failed",
                    null,
                    null);
        }
    }

    @Override
    public TokenRefreshResponse refreshtoken(TokenRefreshRequest request) {

        String requestRefreshToken = request.getRefreshToken();

        return refreshTokenService.findByToken(requestRefreshToken)
                .map((c) -> {
                    try {
                        return refreshTokenService.verifyExpiration(c);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .map(RefreshToken::getUser)
                .map(user -> {
                    String token = jwtUtils.generateTokenFromUsername(user.getEmail());
                    return new TokenRefreshResponse(token, requestRefreshToken);
                })
                .orElseThrow(() -> new RuntimeException("Refresh token not found"));
    }

    @Override
    public User registerUser(SignupRequest signUpRequest) throws RuntimeException {

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return null;
        }

        // Create new user's account
        User user = new User(signUpRequest.getEmail(),
                signUpRequest.getFirstName(),
                signUpRequest.getLastName(),
                encoder.encode(signUpRequest.getPassword()));

        Set<String> strRoles = signUpRequest.getRoles();
        List<Role> roles = new LinkedList();


        strRoles.forEach(role -> {
            switch (role) {
                case "ROLE_ADMIN":
                    log.info("admin here");
                    Role adminRole = roleRepository.findByName(RoleType.ROLE_ADMIN)
                            .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                    roles.add(adminRole);

                    break;

                case "ROLE_AGENT":
                    Role agentRole = roleRepository.findByName(RoleType.ROLE_AGENT)
                            .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                    roles.add(agentRole);

                    break;
                case "ROLE_CLIENT":
                    Role clientRole = roleRepository.findByName(RoleType.ROLE_CLIENT)
                            .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                    roles.add(clientRole);

                    break;

                case "ROLE_SYSTEM":
                    Role systemRole = roleRepository.findByName(RoleType.ROLE_SYSTEM)
                            .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                    roles.add(systemRole);

                    break;
                default:
                    throw new IllegalArgumentException("Unsupported role: " + role);

            }
        });

        user.setRoles(roles);
        return userRepository.save(user);

    }




    @Override
    public Role addRole(Role role) {
        return roleRepository.save(role);
    }

    @Override
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> getConnectedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = ((UserDetails) authentication.getPrincipal()).getUsername();
        return userRepository.findByEmail(currentUsername);
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }
}
