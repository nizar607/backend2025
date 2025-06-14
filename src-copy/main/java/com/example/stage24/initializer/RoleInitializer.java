package com.example.stage24.initializer;



import com.example.stage24.user.domain.Role;
import com.example.stage24.user.domain.RoleType;
import com.example.stage24.user.repository.RoleRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.core.annotation.Order;


@Component
@Order(1)
@AllArgsConstructor
public class RoleInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;


    @Override
    public void run(String... args) throws Exception {

        for (RoleType roleType : RoleType.values()) {
            roleRepository.findByName(roleType)
                    .orElseGet(() -> roleRepository.save(new Role(roleType)));
        }

    }
}