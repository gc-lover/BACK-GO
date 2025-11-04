package com.necpgame.backjava.service;

import com.necpgame.backjava.model.Error;
import com.necpgame.backjava.model.LoginRequest;
import com.necpgame.backjava.model.LoginResponse;
import com.necpgame.backjava.model.Register201Response;
import com.necpgame.backjava.model.RegisterRequest;
import org.springframework.validation.annotation.Validated;

/**
 * Service interface for AuthService.
 * Generated from OpenAPI specification.
 * 
 * This is a service interface that should be implemented by a service implementation class.
 */
@Validated
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.17.0")public interface AuthService {

    /**
     * POST /auth/login : Вход в систему
     * Аутентификация игрока по email или username и паролю. Возвращает JWT токен.
     *
     * @param loginRequest  (required)
     * @return LoginResponse
     */
    LoginResponse login(LoginRequest loginRequest);

    /**
     * POST /auth/register : Регистрация нового аккаунта
     * Создает новый аккаунт игрока. Проверяет уникальность email и username.
     *
     * @param registerRequest  (required)
     * @return Register201Response
     */
    Register201Response register(RegisterRequest registerRequest);
}

