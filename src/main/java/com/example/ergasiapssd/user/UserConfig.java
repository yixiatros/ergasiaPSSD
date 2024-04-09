package com.example.ergasiapssd.user;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class UserConfig {

    @Bean
    CommandLineRunner userCommandLineRunner(UserRepository userRepository) {
        return args -> {
            User maria = new User(
                    1L,
                    "Maria",
                    "maria@testmail.com",
                    "maria",
                    "maria123"
            );

            User giorgos = new User(
                    1L,
                    "Giorgos",
                    "giorgos@testmail.com",
                    "giorgos",
                    "giorgos123"
            );

            userRepository.saveAll(List.of(maria, giorgos));
        };
    }
}
