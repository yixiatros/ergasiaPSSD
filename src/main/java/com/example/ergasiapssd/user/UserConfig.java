package com.example.ergasiapssd.user;

import com.example.ergasiapssd.user.role.Role;
import com.example.ergasiapssd.user.role.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Configuration
public class UserConfig {

    @Bean
    CommandLineRunner userCommandLineRunner(UserRepository userRepository,
                                            RoleRepository roleRepository) {
        return args -> {
            createRoleIfNotFound("ROLE_ADMIN", roleRepository);
            createRoleIfNotFound("ROLE_USER", roleRepository);
            Optional<Role> optionalAdminRole = roleRepository.findRoleByName("ROLE_ADMIN");
            Optional<Role> optionalUserRole = roleRepository.findRoleByName("ROLE_USER");

            User maria = new User();
            maria.setName("Maria");
            maria.setEmail("maria@testmail.com");
            maria.setUsername("maria");
            maria.setPassword("maria123");
            optionalAdminRole.ifPresent(role -> maria.setRoles(Arrays.asList(role)));

            User giorgos = new User();
            giorgos.setName("Giorgos");
            giorgos.setEmail("giorgos@testmail.com");
            giorgos.setUsername("giorgos");
            giorgos.setPassword("giorgos123");
            optionalUserRole.ifPresent(role -> giorgos.setRoles(Arrays.asList(role)));

            userRepository.saveAll(List.of(maria, giorgos));
        };
    }

    Role createRoleIfNotFound(String name, RoleRepository roleRepository) {
        Optional<Role> optionalRole = roleRepository.findRoleByName(name);

        if (optionalRole.isPresent())
            return null;

        Role role = new Role();
        role.setName(name);
        roleRepository.save(role);

        return role;
    }
}
