package com.necpgame.backjava.service.impl;

import com.necpgame.backjava.entity.AccountEntity;
import com.necpgame.backjava.exception.AuthException;
import com.necpgame.backjava.exception.BusinessException;
import com.necpgame.backjava.exception.ValidationException;
import com.necpgame.backjava.exception.ErrorCode;
import com.necpgame.backjava.mapper.AccountMapper;
import com.necpgame.backjava.model.LoginRequest;
import com.necpgame.backjava.model.LoginResponse;
import com.necpgame.backjava.model.Register201Response;
import com.necpgame.backjava.model.RegisterRequest;
import com.necpgame.backjava.repository.AccountRepository;
import com.necpgame.backjava.security.JwtTokenProvider;
import com.necpgame.backjava.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

/**
 * Реализация AuthService - регистрация и авторизация пользователей
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AccountMapper accountMapper;
    
    /**
     * Регистрация нового аккаунта
     */
    @Override
    @Transactional
    public Register201Response register(RegisterRequest request) {
        log.info("Registration attempt for email: {}", request.getEmail());
        
        // Валидация: пароли должны совпадать
        if (!request.getPassword().equals(request.getPasswordConfirm())) {
            throw new ValidationException(ErrorCode.INVALID_INPUT, "Passwords do not match");
        }
        
        // Валидация: принятие условий
        if (!request.getTermsAccepted()) {
            throw new ValidationException(ErrorCode.MISSING_REQUIRED_FIELD, "Terms and conditions must be accepted");
        }
        
        // Проверка: email уже существует
        if (accountRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException(ErrorCode.RESOURCE_ALREADY_EXISTS, "Email already exists: " + request.getEmail());
        }
        
        // Проверка: username уже существует
        if (accountRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException(ErrorCode.RESOURCE_ALREADY_EXISTS, "Username already exists: " + request.getUsername());
        }
        
        // Создание нового аккаунта
        AccountEntity account = new AccountEntity();
        account.setEmail(request.getEmail());
        account.setUsername(request.getUsername());
        account.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        account.setIsActive(true);
        account.setCreatedAt(OffsetDateTime.now());
        account.setUpdatedAt(OffsetDateTime.now());
        
        // Сохранение в БД
        account = accountRepository.save(account);
        
        log.info("Account created successfully: {}", account.getId());
        
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
    @Transactional
    public LoginResponse login(LoginRequest request) {
        log.info("Login attempt for: {}", request.getLogin());
        
        // Поиск аккаунта по email или username
        AccountEntity account = accountRepository
                .findActiveByEmailOrUsername(request.getLogin())
                .orElseThrow(() -> new AuthException(ErrorCode.INVALID_CREDENTIALS));
        
        // Проверка пароля
        if (!passwordEncoder.matches(request.getPassword(), account.getPasswordHash())) {
            log.warn("Invalid password for account: {}", account.getId());
            throw new AuthException(ErrorCode.INVALID_CREDENTIALS);
        }
        
        // Обновление last_login
        account.setLastLogin(OffsetDateTime.now());
        accountRepository.save(account);
        
        // Создание JWT токена
        String token = jwtTokenProvider.createToken(account.getId());
        
        log.info("Login successful for account: {}", account.getId());
        
        // Формирование ответа
        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setAccountId(account.getId());
        response.setExpiresAt(jwtTokenProvider.getTokenExpiration());
        
        return response;
    }
}

