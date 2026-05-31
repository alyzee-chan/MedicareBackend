package com.medicare.backend.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            try {
                if (jwtUtil.isValid(token)) {
                    long userId = jwtUtil.getUserId(token);
                    String email = jwtUtil.getEmail(token);
                    String role = jwtUtil.parseToken(token).get("role", String.class);

                    System.out.println("DEBUG JWT: Valid token for " + email + " [ID: " + userId + "]");

                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                            userId,
                            email,
                            List.of(new SimpleGrantedAuthority(
                                    "ROLE_" + (role != null ? role.toUpperCase() : "PATIENT"))));
                    SecurityContextHolder.getContext().setAuthentication(auth);
                } else {
                    System.err.println("DEBUG JWT: Token is INVALID (expired or tampered)");
                }
            } catch (Exception e) {
                System.err.println("DEBUG JWT: Error processing token: " + e.getMessage());
            }
        } else if (header != null) {
            System.err.println("DEBUG JWT: Auth header present but invalid format: " + header);
        }

        filterChain.doFilter(request, response);
    }
}
