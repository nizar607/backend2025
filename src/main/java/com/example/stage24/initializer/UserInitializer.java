package com.example.stage24.initializer;

import com.example.stage24.user.domain.Role;
import com.example.stage24.user.domain.RoleType;
import com.example.stage24.user.domain.User;
import com.example.stage24.user.repository.RoleRepository;
import com.example.stage24.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.util.Collections;
import java.util.List;

@Component
@AllArgsConstructor
@Order(2) // Run after RoleInitializer
public class UserInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder encoder;

    @Override
    public void run(String... args) throws Exception {
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
            user.setRoles(Collections.singleton(roleAdmin));

            userRepository.save(user);
        }
    }
}