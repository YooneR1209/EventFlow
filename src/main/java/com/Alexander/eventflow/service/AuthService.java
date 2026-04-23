package com.Alexander.eventflow.service;

import com.Alexander.eventflow.dto.request.LoginRequest;
import com.Alexander.eventflow.dto.request.RegisterRequest;
import com.Alexander.eventflow.dto.response.AuthResponse;
import com.Alexander.eventflow.exception.BusinessException;
import com.Alexander.eventflow.model.User;
import com.Alexander.eventflow.model.enums.Role;
import com.Alexander.eventflow.repository.UserRepository;
import com.Alexander.eventflow.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse register(RegisterRequest request) {

        // 1. Verificar que el email no esté registrado
        if (userRepository.existsByEmail(request.email())) {
            throw new BusinessException("El email ya está registrado");
        }

        // 2. Construir el usuario
        User user = User.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(Role.USER)
                .build();

        // 3. Guardar en la base de datos
        userRepository.save(user);

        // 4. Generar token y devolver respuesta
        String token = jwtService.generateToken(user);
        return buildAuthResponse(user, token);
    }

    public AuthResponse login(LoginRequest request) {

        // 1. Verificar credenciales — lanza BadCredentialsException si falla
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        ); // ---preguntar que se hace con el authentication del return

        // 2. Cargar el usuario de la base de datos
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BusinessException("Usuario no encontrado"));

        // 3. Generar token y devolver respuesta
        String token = jwtService.generateToken(user);
        return buildAuthResponse(user, token);
    }

    private AuthResponse buildAuthResponse(User user, String token) {
        return new AuthResponse(
                token,
                user.getEmail(),
                user.getFirstName(),
                user.getAuthorities().iterator().next().getAuthority()
        );
    }
}