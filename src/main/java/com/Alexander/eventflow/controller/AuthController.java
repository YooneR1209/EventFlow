package com.Alexander.eventflow.controller;

import com.Alexander.eventflow.dto.request.LoginRequest;
import com.Alexander.eventflow.dto.request.RegisterRequest;
import com.Alexander.eventflow.dto.response.AuthResponse;
import com.Alexander.eventflow.model.User;
import com.Alexander.eventflow.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody RegisterRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/me")
    public ResponseEntity<AuthResponse> me(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(new AuthResponse(
                null,
                user.getEmail(),
                user.getFirstName(),
                user.getAuthorities().iterator().next().getAuthority()
        ));
    }
}