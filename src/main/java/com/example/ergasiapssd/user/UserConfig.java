package com.example.ergasiapssd.user;

import com.example.ergasiapssd.user.role.Role;
import com.example.ergasiapssd.user.role.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Configuration
public class UserConfig {

    @Bean
    CommandLineRunner userCommandLineRunner(UserRepository userRepository,
                                            RoleRepository roleRepository,
                                            PasswordEncoder passwordEncoder) {
        return args -> {
            createRoleIfNotFound("ROLE_ADMIN", roleRepository);
            createRoleIfNotFound("ROLE_TEACHER", roleRepository);
            createRoleIfNotFound("ROLE_STUDENT", roleRepository);
            Optional<Role> optionalAdminRole = roleRepository.findRoleByName("ROLE_ADMIN");
            Optional<Role> optionalTeacherRole = roleRepository.findRoleByName("ROLE_TEACHER");
            Optional<Role> optionalStudentRole = roleRepository.findRoleByName("ROLE_STUDENT");

            User admin = new User();
            admin.setName("Admin");
            admin.setEmail("admin@testmail.com");
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin"));
            admin.setRegistrationDate(LocalDateTime.now());
            optionalAdminRole.ifPresent(admin::addRole);

            User maria = new User();
            maria.setName("Maria");
            maria.setEmail("maria@testmail.com");
            maria.setUsername("maria");
            maria.setPassword(passwordEncoder.encode("maria123"));
            maria.setRegistrationDate(LocalDateTime.now());
            optionalTeacherRole.ifPresent(maria::addRole);

            User giorgos = new User();
            giorgos.setName("Giorgos");
            giorgos.setEmail("giorgos@testmail.com");
            giorgos.setUsername("giorgos");
            giorgos.setPassword(passwordEncoder.encode("giorgos123"));
            giorgos.setRegistrationDate(LocalDateTime.now());
            optionalStudentRole.ifPresent(giorgos::addRole);

            User kostas = new User();
            kostas.setName("Konstantinos");
            kostas.setEmail("kostas@testmail.com");
            kostas.setUsername("kostas");
            kostas.setPassword(passwordEncoder.encode("kostas123"));
            kostas.setRegistrationDate(LocalDateTime.now());
            optionalStudentRole.ifPresent(kostas::addRole);

            User stella = new User();
            stella.setName("Stella");
            stella.setEmail("stella@testmail.com");
            stella.setUsername("stella");
            stella.setPassword(passwordEncoder.encode("stella123"));
            stella.setRegistrationDate(LocalDateTime.now());
            optionalStudentRole.ifPresent(stella::addRole);

            User giorgis = new User();
            giorgis.setName("Giorgis");
            giorgis.setEmail("giorgis@testmail.com");
            giorgis.setUsername("giorgis");
            giorgis.setPassword(passwordEncoder.encode("giorgis123"));
            giorgis.setRegistrationDate(LocalDateTime.now());
            optionalStudentRole.ifPresent(giorgis::addRole);

            userRepository.saveAll(List.of(admin, maria, giorgos, kostas, stella, giorgis));
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
