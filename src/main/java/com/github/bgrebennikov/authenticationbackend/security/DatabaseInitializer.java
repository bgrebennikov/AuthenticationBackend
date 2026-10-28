package com.github.bgrebennikov.authenticationbackend.security;

import com.github.bgrebennikov.authenticationbackend.data.entity.Role;
import com.github.bgrebennikov.authenticationbackend.repository.RoleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DatabaseInitializer implements CommandLineRunner {


    private final RoleRepository roleRepository;

    public DatabaseInitializer(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) throws Exception {
//        if (roleRepository.findByName("ROLE_USER").isEmpty()) {
//            Role role = new Role();
//            role.setName("ROLE_USER");
//            roleRepository.save(role);
//            log.info("Role ROLE_USER has been created");
//        }
//
//        if (roleRepository.findByName("ROLE_ADMIN").isEmpty()) {
//            Role role = new Role();
//            role.setName("ROLE_ADMIN");
//            roleRepository.save(role);
//            log.info("Role ROLE_ADMIN has been created");
//        }
    }
}
