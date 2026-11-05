package com.todo.todoList.multitenancy.security;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class PasswordTestConfig {

    @Bean
    public CommandLineRunner generatePasswordHash(
            PasswordEncoder passwordEncoder) {

        return args -> {
            System.out.println(
                    "BCrypt Hash: " +
                            passwordEncoder.encode("Alex@123")
            );
        };
    }
}