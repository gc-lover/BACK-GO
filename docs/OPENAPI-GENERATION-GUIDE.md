# OpenAPI Generation Guide

## 📖 Обзор

Этот документ описывает процесс генерации **КОНТРАКТОВ** из OpenAPI спецификаций для проекта NECPGAME Backend.

**Главный принцип:** OpenAPI спецификация является **единственным источником правды ТОЛЬКО для контрактов** (API Interfaces, DTOs, Service Interfaces). Реализация (Entities, Repositories, Controllers, ServiceImpl) создаётся вручную в `src/main/java/`.

## 🎯 Философия: Контракты vs Реализация

### ✅ Генерируется АВТОМАТИЧЕСКИ (контракты):

Эти файлы можно **безопасно перегенерировать** при изменении OpenAPI спецификации:

1. **DTOs** (`target/generated-sources/openapi/model/`)
   - Модели данных для REST API
   - Содержат: поля, валидацию, геттеры/сеттеры
   - **Никогда не редактируем вручную**

2. **API Interfaces** (`target/generated-sources/openapi/api/`)
   - Контракты REST API
   - Содержат: Spring MVC аннотации, сигнатуры методов
   - **Никогда не редактируем вручную**

3. **Service Interfaces** (`target/generated-sources/services/`)
   - Контракты бизнес-логики
   - Содержат: сигнатуры методов без реализации
   - **Никогда не редактируем вручную**

### ✍️ Создаётся ВРУЧНУЮ (реализация):

Эти файлы создаются в `src/main/java/` и **никогда не перегенерируются**:

4. **Entities** (`src/main/java/entity/`)
   - JPA сущности с relationships, indexes, constraints
   - Сложная логика, которую невозможно вывести из OpenAPI

5. **Repositories** (`src/main/java/repository/`)
   - Spring Data репозитории с custom queries
   - Специфичные для БД запросы

6. **Controllers** (`src/main/java/controller/`)
   - REST контроллеры, реализующие API Interfaces
   - Бизнес-логика обработки запросов

7. **ServiceImpl** (`src/main/java/service/impl/`)
   - Реализация бизнес-логики
   - Основная логика приложения

## 🚀 Быстрый старт

### Генерация контрактов из одного файла

```powershell
# Из корня проекта BACK-GO
.\scripts\generate-openapi-layers.ps1 -ApiSpec ../API-SWAGGER/api/v1/auth/character-creation.yaml
```

Эта команда:
- Очистит `target/generated-sources/`
- Сгенерирует DTOs, API Interfaces и Service Interfaces
- Покажет статистику генерации

### Генерация контрактов из всех файлов в директории

```powershell
.\scripts\generate-openapi-layers.ps1 -ApiDirectory ../API-SWAGGER/api/v1/
```

Обработает **все** `.yaml` и `.yml` файлы в указанной директории.

### Генерация только определённых контрактов

```powershell
# Только DTOs и API Interfaces
.\scripts\generate-openapi-layers.ps1 -ApiSpec path/to/api.yaml -Layers DTOs

# Только Service Interfaces
.\scripts\generate-openapi-layers.ps1 -ApiSpec path/to/api.yaml -Layers Services
```

Доступные контракты: `DTOs`, `Services`, `All`

## 📁 Структура проекта

### Сгенерированные контракты (в `target/`):

```
target/generated-sources/
├── openapi/          # DTOs + API Interfaces (КОНТРАКТЫ)
│   └── src/main/java/com/necpgame/backjava/
│       ├── api/      # AuthApi, CharactersApi, etc.
│       │             # Реализуются в src/main/java/controller/
│       └── model/    # LoginRequest, LoginResponse, Account, etc.
│                     # Используются везде
└── services/         # Service Interfaces (КОНТРАКТЫ)
    └── src/main/java/com/necpgame/backjava/
        └── service/  # AuthService, CharactersService, etc.
                      # Реализуются в src/main/java/service/impl/
```

### Ручная реализация (в `src/main/java/`):

```
src/main/java/com/necpgame/backjava/
├── entity/           # JPA Entities (ВРУЧНУЮ)
│   ├── AccountEntity.java
│   ├── CharacterEntity.java
│   └── ... (с relationships, indexes, constraints)
│
├── repository/       # Spring Data Repositories (ВРУЧНУЮ)
│   ├── AccountRepository.java
│   ├── CharacterRepository.java
│   └── ... (с custom queries)
│
├── controller/       # REST Controllers (ВРУЧНУЮ)
│   ├── AuthController.java       # implements AuthApi
│   ├── CharactersController.java # implements CharactersApi
│   └── ... (реализация API контрактов)
│
├── service/
│   └── impl/         # Service Implementations (ВРУЧНУЮ)
│       ├── AuthServiceImpl.java       # implements AuthService
│       ├── CharactersServiceImpl.java # implements CharactersService
│       └── ... (вся бизнес-логика)
│
├── exception/        # Custom Exceptions (ВРУЧНУЮ)
│   ├── NotFoundException.java
│   ├── ConflictException.java
│   └── ...
│
└── mapper/           # Entity <-> DTO Mappers (ВРУЧНУЮ)
    ├── AccountMapper.java
    ├── CharacterMapper.java
    └── ...
```

## 🔧 Кастомные шаблоны

Проект использует кастомные Mustache шаблоны для генерации кода с нужной структурой:

### Активные шаблоны (в `templates/`)

1. **`api.mustache`** - генерирует Service интерфейсы
   ```java
   public interface AuthService {
       LoginResponse login(LoginRequest request);
   }
   ```

2. **`apiController.mustache`** - генерирует REST контроллеры
   ```java
   @Controller
   public class AuthApiController implements AuthApi {
       // Полная реализация с Spring MVC аннотациями
   }
   ```

3. **`model.mustache`** - генерирует JPA Entities
   ```java
   @Entity
   @Table(name = "account")
   public class Account {
       @Id
       @GeneratedValue(strategy = GenerationType.UUID)
       private UUID id;
       // + Lombok, timestamps, validation
   }
   ```

4. **`repositoryModel.mustache`** - генерирует Spring Data репозитории
   ```java
   @Repository
   public interface AccountRepository extends JpaRepository<Account, UUID> {
       // Spring Data автоматически реализует CRUD
   }
   ```

## 🔄 Интеграция с Maven

Хотя основная генерация происходит через PowerShell скрипт, в `pom.xml` настроен `build-helper-maven-plugin`, который автоматически добавляет сгенерированные источники в classpath:

```xml
<plugin>
    <groupId>org.codehaus.mojo</groupId>
    <artifactId>build-helper-maven-plugin</artifactId>
    <execution>
        <phase>generate-sources</phase>
        <goals>
            <goal>add-source</goal>
        </goals>
    </execution>
</plugin>
```

Это позволяет Maven видеть сгенерированный код при компиляции.

## 📝 Создание реализации вручную

После генерации контрактов необходимо создать реализацию в `src/main/java/`.

### Примеры и шаблоны

Полные шаблоны для создания всех компонентов смотри в **[MANUAL-TEMPLATES.md](MANUAL-TEMPLATES.md)**:
- Entity Template
- Repository Template
- Controller Template
- ServiceImpl Template
- Exception Templates
- Mapper Template

### Краткий пример: ServiceImpl

```java
package com.necpgame.backjava.service.impl;

import com.necpgame.backjava.service.AuthService;  // Сгенерированный контракт
import com.necpgame.backjava.model.*;              // Сгенерированные DTOs
import com.necpgame.backjava.entity.AccountEntity;
import com.necpgame.backjava.repository.AccountRepository;
import com.necpgame.backjava.mapper.AccountMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    
    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    
    @Override
    @Transactional
    public LoginResponse login(LoginRequest request) {
        log.info("Login attempt for: {}", request.getEmail());
        
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
        response.setAccount(accountMapper.toDto(account));
        
        return response;
    }
}
```

### Краткий пример: Controller

```java
package com.necpgame.backjava.controller;

import com.necpgame.backjava.api.AuthApi;  // Сгенерированный контракт
import com.necpgame.backjava.model.*;      // Сгенерированные DTOs
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
        log.info("Login request for: {}", request.getEmail());
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}
```

## 🛠️ Параметры генерации

### Общие параметры для всех слоёв

```bash
-g spring                    # Spring generator
-t templates                 # Использовать кастомные шаблоны
useSpringBoot3=true         # Spring Boot 3.x
useJakartaEe=true           # Jakarta EE (javax -> jakarta)
```

### Специфичные параметры

#### DTOs & API Interfaces
```bash
interfaceOnly=true          # Только интерфейсы, без реализации
useBeanValidation=true      # Jakarta Validation аннотации
openApiNullable=false       # Не использовать JsonNullable
```

#### JPA Entities
```bash
generateApis=false                              # Не генерировать API
generateModels=true                             # Генерировать модели
modelTemplateFiles=model.mustache=Entity.java  # Кастомный шаблон
```

#### Service Interfaces
```bash
interfaceOnly=true          # Только интерфейсы
--api-name-suffix Service   # Добавить суффикс Service к именам
```

#### Controllers
```bash
interfaceOnly=false         # Генерировать классы, не интерфейсы
delegatePattern=false       # Не использовать Delegate pattern
```

## 🔍 Проверка результатов

После генерации проверьте:

1. **Количество файлов** - должны быть сгенерированы все сущности из OpenAPI
2. **Компиляция** - `mvn compile` должна пройти без ошибок
3. **Импорты** - проверьте, что используется `jakarta.*`, а не `javax.*`
4. **Аннотации** - JPA, Spring, Validation должны быть на месте

## 📚 Дополнительные ресурсы

- [OpenAPI Generator Documentation](https://openapi-generator.tech/docs/generators/spring)
- [Mustache Template Syntax](https://mustache.github.io/mustache.5.html)
- [Spring Boot 3 Migration Guide](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-3.0-Migration-Guide)

## ❓ FAQ

### Почему не используется Maven plugin для генерации?

PowerShell скрипт проще, понятнее и даёт больше контроля:
- Легко отлаживать каждую команду
- Можно генерировать отдельные слои
- Не зависит от Maven lifecycle quirks
- Прозрачность выполнения

### Как добавить новый OpenAPI файл для генерации?

Просто укажите путь к файлу:
```powershell
.\scripts\generate-openapi-layers.ps1 -ApiSpec ../API-SWAGGER/api/v1/new/feature.yaml
```

### Что делать, если генерация падает с ошибкой?

1. Проверьте корректность OpenAPI спецификации
2. Убедитесь, что Node.js и npx установлены
3. Проверьте наличие `templates/` директории с шаблонами
4. Запустите генерацию отдельного слоя для отладки:
   ```powershell
   .\scripts\generate-openapi-layers.ps1 -Layers DTOs
   ```

### Можно ли изменить структуру генерируемого кода?

Да! Отредактируйте соответствующий Mustache шаблон в `templates/`:
- `api.mustache` - Service интерфейсы
- `apiController.mustache` - Controllers
- `model.mustache` - JPA Entities  
- `repositoryModel.mustache` - Repositories

После изменения шаблона перегенерируйте код.

## 🔄 Workflow разработки

### Сценарий 1: Добавление нового API endpoint

1. **Изменяем OpenAPI спецификацию** в `API-SWAGGER/`:
   ```yaml
   paths:
     /auth/logout:
       post:
         operationId: logout
         # ...
   ```

2. **Генерируем контракты**:
   ```powershell
   .\scripts\generate-openapi-layers.ps1 -ApiSpec ../API-SWAGGER/api/v1/auth/character-creation.yaml
   ```

3. **Проверяем сгенерированные контракты**:
   - `target/generated-sources/openapi/api/AuthApi.java` - добавился метод `logout()`
   - `target/generated-sources/services/AuthService.java` - добавился метод `logout()`

4. **Реализуем в `src/main/java/`**:
   - Добавляем метод `logout()` в `AuthController.java`
   - Добавляем метод `logout()` в `AuthServiceImpl.java`
   - Добавляем бизнес-логику (инвалидация токена, логирование, etc.)

5. **Компилируем и тестируем**:
   ```bash
   mvn clean compile
   mvn test
   ```

6. **Коммитим**:
   ```powershell
   .\scripts\autocommit.ps1 "feat: Add logout endpoint"
   ```

### Сценарий 2: Изменение существующего DTO

1. **Изменяем OpenAPI спецификацию** (добавили поле в `LoginRequest`):
   ```yaml
   LoginRequest:
     properties:
       email: ...
       password: ...
       rememberMe:  # НОВОЕ ПОЛЕ
         type: boolean
   ```

2. **Генерируем контракты**:
   ```powershell
   .\scripts\generate-openapi-layers.ps1 -ApiSpec ../API-SWAGGER/api/v1/auth/character-creation.yaml
   ```

3. **Проверяем сгенерированные контракты**:
   - `target/generated-sources/openapi/model/LoginRequest.java` - добавилось поле `rememberMe`

4. **Обновляем реализацию**:
   - В `AuthServiceImpl.login()` используем новое поле `request.getRememberMe()`
   - Обновляем логику создания токена (срок жизни токена)

5. **Компилируем Maven**:
   ```bash
   mvn clean compile  # Покажет ошибки компиляции, если забыли обновить код
   ```

6. **Коммитим**:
   ```powershell
   .\scripts\autocommit.ps1 "feat: Add rememberMe support to login"
   ```

### Сценарий 3: Создание новой фичи с нуля

1. **Создаём OpenAPI спецификацию** в `API-SWAGGER/api/v1/inventory/items.yaml`

2. **Генерируем контракты**:
   ```powershell
   .\scripts\generate-openapi-layers.ps1 -ApiSpec ../API-SWAGGER/api/v1/inventory/items.yaml
   ```

3. **Создаём реализацию вручную** (используя [MANUAL-TEMPLATES.md](MANUAL-TEMPLATES.md)):
   - `src/main/java/entity/ItemEntity.java`
   - `src/main/java/repository/ItemRepository.java`
   - `src/main/java/controller/ItemsController.java`
   - `src/main/java/service/impl/ItemsServiceImpl.java`
   - `src/main/java/mapper/ItemMapper.java`

4. **Создаём Liquibase миграцию**:
   ```bash
   mvn liquibase:diff -Dliquibase.diffChangeLogFile=db/changelog/changes/003-create-items-table.yaml
   ```

5. **Компилируем, тестируем, коммитим**

### Сценарий 4: Перегенерация всех контрактов

Если изменилось много в OpenAPI:

```powershell
# Генерация из всей директории
.\scripts\generate-openapi-layers.ps1 -ApiDirectory ../API-SWAGGER/api/v1/
```

Это перегенерирует **все** контракты, но **не затронет** реализацию в `src/main/java/`.
