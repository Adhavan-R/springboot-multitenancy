package com.todo.todoList.multitenancy.security.controller;

import com.todo.todoList.multitenancy.security.dto.LoginRequest;
import com.todo.todoList.multitenancy.security.dto.LoginResponse;
import com.todo.todoList.multitenancy.security.service.AuthService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request) {

        LoginResponse response = authService.login(request);

        return ResponseEntity.ok(response);
    }
}