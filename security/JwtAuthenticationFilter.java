package com.todo.todoList.multitenancy.security;

import com.todo.todoList.multitenancy.tenant.TenantContext;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        try {

            // 1. Read Authorization header
            String authHeader = request.getHeader("Authorization");

            // 2. Check whether Bearer token exists
            if (authHeader != null && authHeader.startsWith("Bearer ")) {

                String token = authHeader.substring(7);

                // 3. Validate token
                if (!jwtService.isTokenValid(token)) {
                    response.sendError(
                            HttpServletResponse.SC_UNAUTHORIZED,
                            "Invalid or expired JWT"
                    );
                    return;
                }

                // 4. Extract user information
                Long userId = jwtService.extractUserId(token);
                Long tenantId = jwtService.extractTenantId(token);
                String role = jwtService.extractRole(token);

                // 5. Set tenant context
                TenantContext.setTenantId(tenantId);

                // 6. Create Spring Security authentication
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userId,
                                null,
                                List.of(
                                        new SimpleGrantedAuthority("ROLE_" + role)
                                )
                        );

                // 7. Store authentication in SecurityContext
                SecurityContextHolder.getContext()
                        .setAuthentication(authentication);
            }

            // 8. Continue request processing -> directs to Controller
            filterChain.doFilter(request, response);

        } finally {

            // Always clean up request-specific information
            TenantContext.clear();
        }
    }
}