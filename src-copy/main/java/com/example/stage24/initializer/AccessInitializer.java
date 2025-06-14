package com.example.stage24.initializer;

import com.example.stage24.user.domain.*;
import com.example.stage24.user.repository.AccessRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;


@Component
@AllArgsConstructor
public class AccessInitializer implements CommandLineRunner {

    private final AccessRepository accessRepository;


    @Override
    public void run(String... args) throws Exception {


        for (AccessType accessType : AccessType.values()) {
            accessRepository.findByType(accessType)
                    .orElseGet(() -> accessRepository.save(new Access(accessType)));
        }

    }
}