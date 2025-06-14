package com.example.stage24.initializer;

import com.example.stage24.user.domain.*;
import com.example.stage24.user.repository.AccessRepository;
import com.example.stage24.user.repository.RoleRepository;
import com.example.stage24.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
@AllArgsConstructor
@Order(2) // Run after RoleInitializer
public class UserInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final AccessRepository accessRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder encoder;

    @Override
    public void run(String... args) throws Exception {


        for (RoleType roleType : RoleType.values()) {
            roleRepository.findByName(roleType)
                    .orElseGet(() -> roleRepository.save(new Role(roleType)));
        }

        for (AccessType accessType : AccessType.values()) {
            accessRepository.findByType(accessType)
                    .orElseGet(() -> accessRepository.save(new Access(accessType)));
        }

        Role roleAdmin = roleRepository.findByName(RoleType.ROLE_ADMIN)
                .orElseThrow(() -> new RuntimeException("ROLE_ADMIN not found in database. Make sure roles are initialized."));

        List<User> users = userRepository.findAllByRolesContaining(roleAdmin);

        if (users.isEmpty()) {
            User user = new User();
            user.setEmail("admin@gmail.com");
            user.setPassword(encoder.encode("admin@gmail.com"));
            user.setFirstName("Admin");
            user.setLastName("Admin");
            user.setEnabled(true); // Set to true
            user.setRoles(Collections.singletonList(roleAdmin));
            user.setAccesses(accessRepository.findAll());
            userRepository.save(user);
        }
    }
}