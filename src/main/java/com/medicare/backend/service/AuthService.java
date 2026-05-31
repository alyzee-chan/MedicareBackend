package com.medicare.backend.service;

import com.medicare.backend.config.JwtUtil;
import com.medicare.backend.dto.AuthResponse;
import com.medicare.backend.dto.LoginRequest;
import com.medicare.backend.dto.RegisterRequest;
import com.medicare.backend.dto.UserDto;
import com.medicare.backend.repository.MedicareRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final MedicareRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final NotificationService notificationService;

    public AuthService(MedicareRepository repository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil,
            NotificationService notificationService) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.notificationService = notificationService;
    }

    public AuthResponse register(RegisterRequest request) {
        // Check if email already exists
        UserDto existing = repository.findUserByEmail(request.email());
        if (existing != null) {
            throw new IllegalArgumentException("Un compte avec cet email existe déjà.");
        }

        // Hash password and create user
        String hash = passwordEncoder.encode(request.password());
        long userId = repository.createUser(
                request.email(),
                hash,
                request.fullName(),
                request.phone(),
                request.dateOfBirth(),
                request.bloodGroup(),
                request.role());

        // Build token
        String token = jwtUtil.generateToken(userId, request.email(),
                request.role() != null ? request.role() : "PATIENT");

        // Simulate sending notifications
        notificationService.sendWelcomeEmail(request.email(), request.fullName());
        notificationService.sendOtpSms(request.phone(), "1234"); // Mock OTP

        return new AuthResponse(
                token,
                userId,
                request.fullName(),
                request.email(),
                request.phone(),
                request.role() != null ? request.role() : "PATIENT",
                request.bloodGroup(),
                request.dateOfBirth());
    }

    public AuthResponse login(LoginRequest request) {
        String storedHash = repository.findPasswordHashByEmail(request.email());
        if (storedHash == null) {
            throw new IllegalArgumentException("Email ou mot de passe incorrect.");
        }

        if (!passwordEncoder.matches(request.password(), storedHash)) {
            throw new IllegalArgumentException("Email ou mot de passe incorrect.");
        }

        UserDto user = repository.findUserByEmail(request.email());
        String token = jwtUtil.generateToken(user.id(), user.email(), user.role());

        return new AuthResponse(
                token,
                user.id(),
                user.fullName(),
                user.email(),
                user.phone(),
                user.role(),
                user.bloodGroup(),
                user.dateOfBirth());
    }

    public UserDto getProfile(long userId) {
        UserDto user = repository.findUserById(userId);
        if (user == null) {
            throw new IllegalArgumentException("Utilisateur non trouvé.");
        }
        return user;
    }
}
