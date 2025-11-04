package com.necpgame.backjava.controller;

import com.necpgame.backjava.api.AuthApi;
import com.necpgame.backjava.model.LoginRequest;
import com.necpgame.backjava.model.LoginResponse;
import com.necpgame.backjava.model.Register201Response;
import com.necpgame.backjava.model.RegisterRequest;
import com.necpgame.backjava.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller для аутентификации и регистрации
 * Реализует сгенерированный AuthApi интерфейс
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthController implements AuthApi {
    
    private final AuthService authService;
    
    /**
     * POST /auth/register - Регистрация нового аккаунта
     */
    @Override
    public ResponseEntity<Register201Response> register(RegisterRequest request) {
        log.info("POST /auth/register - {}", request.getEmail());
        Register201Response response = authService.register(request);
        return ResponseEntity.status(201).body(response);
    }
    
    /**
     * POST /auth/login - Вход в систему
     */
    @Override
    public ResponseEntity<LoginResponse> login(LoginRequest request) {
        log.info("POST /auth/login - {}", request.getLogin());
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}

