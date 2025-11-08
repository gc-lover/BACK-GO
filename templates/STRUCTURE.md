# Структура шаблонов OpenAPI Generator

## 🎯 Философия: Контракты vs Реализация

**OpenAPI спецификация = источник правды ТОЛЬКО для контрактов**

### ✅ Что генерируется АВТОМАТИЧЕСКИ (контракты):
1. **DTOs** - модели данных (можно безопасно перегенерировать)
2. **API Interfaces** - контракты REST API (можно безопасно перегенерировать)
3. **Service Interfaces** - контракты бизнес-логики (можно безопасно перегенерировать)

### ✍️ Что создаётся ВРУЧНУЮ (реализация):
4. **Entities** - JPA сущности с relationships, indexes, constraints
5. **Repositories** - Spring Data репозитории с custom queries
6. **Controllers** - REST контроллеры с бизнес-логикой
7. **ServiceImpl** - реализация бизнес-логики

## 📁 Активные шаблоны в `templates/`

### 1. **`api.mustache`** → Service интерфейсы ✅
   - **Генерирует**: `*Service.java` в `src/main/java/com/necpgame/backjava/service/`
   - **Параметр**: `--api-name-suffix Service` + `interfaceOnly=true`
   - **Содержит**: Чистые Java интерфейсы без Spring аннотаций
   - **Пример**: `AuthService.java`, `CharactersService.java`
   - **Использование**: Реализуем в `src/main/java/service/impl/AuthServiceImpl.java`

### 2. Стандартные шаблоны Spring Generator (OpenAPI - источник истины!)
   - **API Interfaces**: генерируются со Spring MVC аннотациями (@RequestMapping, @RequestParam)
   - **DTOs**: генерируются через стандартный генератор
   - **Расположение**: `src/main/java/com/necpgame/backjava/api/` и `src/main/java/com/necpgame/backjava/model/`

### ✅ Controllers реализуют API интерфейсы из OpenAPI:
```java
@RestController
@RequiredArgsConstructor
public class FactionsController implements FactionsApi {
    // Все аннотации (@RequestMapping, @RequestParam) определены в FactionsApi
    @Override
    public ResponseEntity<GetFactions200Response> getFactions(String origin) {
        ...
    }
}
```

### 🗑️ Шаблоны удалены (не используются):
- `serviceImpl.mustache` - ServiceImpl всегда создаются вручную

## 🔄 Процесс генерации

### Использование скрипта:

```powershell
# Валидация OpenAPI спецификаций
.\scripts\validate-openapi.ps1 -ApiDirectory ../API-SWAGGER/api/v1/

# Генерация контрактов во все микросервисы
.\scripts\generate-openapi-microservices.ps1 -ApiDirectory ../API-SWAGGER/api/v1/

# Генерация только DTOs и API Interfaces
.\scripts\generate-openapi-microservices.ps1 -ApiDirectory ../API-SWAGGER/api/v1/ -Layers DTOs

# Генерация только Service Interfaces
.\scripts\generate-openapi-microservices.ps1 -ApiDirectory ../API-SWAGGER/api/v1/ -Layers Services
```

### Генерируемая структура:

```
target/generated-sources/
├── openapi/
│   ├── api/          ← API Interfaces (REST контракты)
│   │   ├── AuthApi.java
│   │   └── CharactersApi.java
│   └── model/        ← DTOs (модели данных)
│       ├── LoginRequest.java
│       ├── LoginResponse.java
│       └── Account.java
└── services/         ← Service Interfaces (бизнес-логика контракты)
    ├── AuthService.java
    └── CharactersService.java
```

## ✍️ Ручное создание реализации

После генерации контрактов создай в `src/main/java/`:

### 1. Entities (`src/main/java/entity/`)

```java
package com.necpgame.backjava.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.*;

@Data
@Entity
@Table(name = "accounts", indexes = {
    @Index(name = "idx_email", columnList = "email", unique = true),
    @Index(name = "idx_username", columnList = "username", unique = true)
})
public class AccountEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(nullable = false, unique = true)
    private String email;
    
    @Column(nullable = false, unique = true)
    private String username;
    
    @Column(nullable = false)
    private String passwordHash;
    
    // Relationships
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    private List<CharacterEntity> characters = new ArrayList<>();
    
    // Timestamps
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
```

### 2. Repositories (`src/main/java/repository/`)

```java
package com.necpgame.backjava.repository;

import com.necpgame.backjava.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public interface AccountRepository extends JpaRepository<AccountEntity, UUID> {
    
    // Spring Data Derived Queries
    Optional<AccountEntity> findByEmail(String email);
    Optional<AccountEntity> findByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    
    // Custom JPQL Query
    @Query("SELECT a FROM AccountEntity a WHERE a.email = :email AND a.active = true")
    Optional<AccountEntity> findActiveByEmail(String email);
}
```

### 3. Controllers (`src/main/java/controller/`)

```java
package com.necpgame.backjava.controller;

import com.necpgame.backjava.api.AuthApi;  // Сгенерированный контракт
import com.necpgame.backjava.model.*;       // Сгенерированные DTOs
import com.necpgame.backjava.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthController implements AuthApi {
    
    private final AuthService authService;
    
    @Override
    public ResponseEntity<LoginResponse> login(LoginRequest request) {
        log.info("Login attempt for: {}", request.getEmail());
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
    
    @Override
    public ResponseEntity<Register201Response> register(RegisterRequest request) {
        log.info("Registration attempt for: {}", request.getEmail());
        Register201Response response = authService.register(request);
        return ResponseEntity.status(201).body(response);
    }
}
```

### 4. ServiceImpl (`src/main/java/service/impl/`)

```java
package com.necpgame.backjava.service.impl;

import com.necpgame.backjava.service.AuthService;  // Сгенерированный контракт
import com.necpgame.backjava.model.*;              // Сгенерированные DTOs
import com.necpgame.backjava.entity.AccountEntity;
import com.necpgame.backjava.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    
    @Override
    @Transactional
    public LoginResponse login(LoginRequest request) {
        log.info("Processing login for: {}", request.getEmail());
        
        // Бизнес-логика
        AccountEntity account = accountRepository
            .findByEmail(request.getEmail())
            .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));
        
        if (!passwordEncoder.matches(request.getPassword(), account.getPasswordHash())) {
            throw new UnauthorizedException("Invalid credentials");
        }
        
        String token = tokenProvider.createToken(account.getId());
        
        // Маппинг Entity → DTO
        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setAccount(mapToDto(account));
        
        return response;
    }
    
    // Вспомогательные методы
    private AccountDto mapToDto(AccountEntity entity) {
        AccountDto dto = new AccountDto();
        dto.setId(entity.getId());
        dto.setEmail(entity.getEmail());
        dto.setUsername(entity.getUsername());
        return dto;
    }
}
```

## 🎯 Workflow разработки

### 1. Изменяем OpenAPI спецификацию
```bash
# Редактируем API-SWAGGER/api/v1/auth/character-creation.yaml
```

### 2. Генерируем контракты в целевой микросервис
```powershell
.\scripts\validate-openapi.ps1 -ApiSpec ../API-SWAGGER/api/v1/auth/character-creation.yaml
.\scripts\generate-openapi-microservices.ps1 -ApiDirectory ../API-SWAGGER/api/v1/auth/
```

### 3. Проверяем изменения в контрактах
```
target/generated-sources/
├── openapi/api/      ← Смотрим новые/изменённые API Interfaces
├── openapi/model/    ← Смотрим новые/изменённые DTOs
└── services/         ← Смотрим новые/изменённые Service Interfaces
```

### 4. Обновляем реализацию в `src/main/java/`
- Если появились новые endpoints → создаём методы в Controller и ServiceImpl
- Если изменились DTOs → обновляем маппинг в ServiceImpl
- Если появились новые сущности → создаём Entity и Repository

### 5. Компилируем и тестируем
```bash
mvn clean compile
mvn test
```

### 6. Коммитим
```powershell
.\scripts\autocommit.ps1 "feat: Add new authentication endpoints"
```

## ⚡ Преимущества подхода

### ✅ Контракты (генерируются):
- ✅ **Всегда актуальны** - синхронизированы с OpenAPI
- ✅ **Нет риска перезаписи** - никогда не редактируем вручную
- ✅ **Type Safety** - полная типизация через Java
- ✅ **Быстрая генерация** - 2-3 секунды для всех контрактов

### ✅ Реализация (пишется вручную):
- ✅ **Полный контроль** - пишем бизнес-логику как хотим
- ✅ **Нет риска потери кода** - никогда не перегенерируем
- ✅ **Гибкость** - relationships, custom queries, сложная логика
- ✅ **Безопасно** - код в `src/main/java/` никогда не трогается генератором

## 📚 Дополнительная информация

- [OpenAPI Generator Templates](https://openapi-generator.tech/docs/templating)
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- [Spring Boot 3 Documentation](https://spring.io/projects/spring-boot)

## 🔧 Настройка Maven

`pom.xml` содержит `build-helper-maven-plugin`, который автоматически добавляет сгенерированные источники:

```xml
<plugin>
    <groupId>org.codehaus.mojo</groupId>
    <artifactId>build-helper-maven-plugin</artifactId>
    <executions>
        <execution>
            <phase>generate-sources</phase>
            <goals>
                <goal>add-source</goal>
            </goals>
            <configuration>
                <sources>
                    <source>target/generated-sources/openapi/src/main/java</source>
                    <source>target/generated-sources/services/src/main/java</source>
                </sources>
            </configuration>
        </execution>
    </executions>
</plugin>
```

Это позволяет Maven видеть сгенерированные контракты при компиляции.
