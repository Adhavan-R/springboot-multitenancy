package com.todo.todoList.multitenancy.security.service;

import com.todo.todoList.multitenancy.identity.entity.Tenant;
import com.todo.todoList.multitenancy.identity.entity.User;
import com.todo.todoList.multitenancy.identity.repository.TenantRepository;
import com.todo.todoList.multitenancy.identity.repository.UserRepository;
import com.todo.todoList.multitenancy.security.JwtService;
import com.todo.todoList.multitenancy.security.dto.LoginRequest;
import com.todo.todoList.multitenancy.security.dto.LoginResponse;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    private final JwtService jwtService;

    public AuthService(TenantRepository tenantRepository,
                       UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {

        this.tenantRepository = tenantRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional(
            transactionManager = "identityTransactionManager",
            readOnly = true
    )
    public LoginResponse login(LoginRequest request) {

        // 1. Find company using company code
        Tenant tenant = tenantRepository
                .findByCompanyCode(request.getCompanyCode().trim().toUpperCase())
                .orElseThrow(() ->
                        new BadCredentialsException("Invalid company code")
                );

        // 2. Find user using email and tenant ID
        User user = userRepository
                .findByEmailAndTenant_TenantId(
                        request.getEmail().trim().toLowerCase(),
                        tenant.getTenantId()
                )
                .orElseThrow(() ->
                        new BadCredentialsException("Invalid company credentials")
                );

        // 3. Verify password using BCrypt
        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword())) {

            throw new BadCredentialsException(
                    "Invalid credentials"
            );
        }

        String token = jwtService.generateToken(user);

        // 4. Return user details (without password)
        return new LoginResponse(
                user.getUserId(),
                user.getName(),
                user.getEmail(),
                tenant.getTenantId(),
                tenant.getCompanyName(),
                tenant.getCompanyCode(),
                user.getRole(),
                token

        );
    }
}