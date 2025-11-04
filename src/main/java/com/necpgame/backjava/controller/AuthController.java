package com.necpgame.backjava.controller;

import com.necpgame.backjava.model.LoginRequest;
import com.necpgame.backjava.model.LoginResponse;
import com.necpgame.backjava.model.Register201Response;
import com.necpgame.backjava.model.RegisterRequest;
import com.necpgame.backjava.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller для аутентификации и регистрации
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthService authService;
    
    /**
     * POST /auth/register - Регистрация нового аккаунта
     */
    @PostMapping("/register")
    public ResponseEntity<Register201Response> register(@Valid @RequestBody RegisterRequest request) {
        log.info("POST /auth/register - {}", request.getEmail());
        Register201Response response = authService.register(request);
        return ResponseEntity.status(201).body(response);
    }
    
    /**
     * POST /auth/login - Вход в систему
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("POST /auth/login - {}", request.getLogin());
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}

