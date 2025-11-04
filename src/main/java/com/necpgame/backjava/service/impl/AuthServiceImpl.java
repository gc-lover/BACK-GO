package com.necpgame.backjava.service.impl;

import com.necpgame.backjava.entity.AccountEntity;
import com.necpgame.backjava.exception.AuthException;
import com.necpgame.backjava.exception.BusinessException;
import com.necpgame.backjava.exception.ErrorCode;
import com.necpgame.backjava.model.LoginRequest;
import com.necpgame.backjava.model.LoginResponse;
import com.necpgame.backjava.model.Register201Response;
import com.necpgame.backjava.model.RegisterRequest;
import com.necpgame.backjava.repository.AccountRepository;
import com.necpgame.backjava.service.AuthService;
import com.necpgame.backjava.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

/**
 * Реализация сервиса аутентификации
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    
    /**
     * Регистрация нового аккаунта
     */
    @Override
    @Transactional
    public Register201Response register(RegisterRequest request) {
        log.info("Registering new account: {}", request.getEmail());
        
        // Проверка уникальности email
        if (accountRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException(ErrorCode.RESOURCE_ALREADY_EXISTS, 
                "Account with this email already exists");
        }
        
        // Проверка уникальности username
        if (accountRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException(ErrorCode.RESOURCE_ALREADY_EXISTS, 
                "Account with this username already exists");
        }
        
        // Создание нового аккаунта
        AccountEntity account = new AccountEntity();
        account.setEmail(request.getEmail());
        account.setUsername(request.getUsername());
        account.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        
        account = accountRepository.save(account);
        log.info("Account created successfully: {}", account.getId());
        
        // Генерация JWT токена
        String token = jwtUtil.generateToken(account.getId(), account.getEmail());
        
        // Формирование ответа
        Register201Response response = new Register201Response();
        response.setAccountId(account.getId());
        response.setMessage("Account created successfully");
        
        return response;
    }
    
    /**
     * Вход в систему
     */
    @Override
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        log.info("Login attempt for: {}", request.getLogin());
        
        // Поиск аккаунта по email или username
        AccountEntity account = accountRepository.findByEmail(request.getLogin())
            .or(() -> accountRepository.findByUsername(request.getLogin()))
            .orElseThrow(() -> new AuthException(ErrorCode.INVALID_CREDENTIALS));
        
        // Проверка пароля
        if (!passwordEncoder.matches(request.getPassword(), account.getPasswordHash())) {
            throw new AuthException(ErrorCode.INVALID_CREDENTIALS);
        }
        
        log.info("Login successful for account: {}", account.getId());
        
        // Генерация JWT токена
        String token = jwtUtil.generateToken(account.getId(), account.getEmail());
        
        // Формирование ответа
        LoginResponse response = new LoginResponse();
        response.setAccountId(account.getId());
        response.setToken(token);
        response.setExpiresAt(OffsetDateTime.now().plusHours(24));
        
        return response;
    }
}

