# БЭКТАСК-ARCHITECTURE.md

**Архитектура, структура директорий и соответствие API-SWAGGER**

📖 **Навигация:** [БЭКТАСК.MD](./БЭКТАСК.MD) | [OPENAPI-GENERATION-GUIDE.md](./OPENAPI-GENERATION-GUIDE.md) | [MANUAL-TEMPLATES.md](./MANUAL-TEMPLATES.md)

---

## 🎯 Философия: Контракты vs Реализация

**OpenAPI спецификация = источник правды ТОЛЬКО для контрактов**

### ✅ Генерируется автоматически (контракты):
- **DTOs** - модели данных (`target/generated-sources/openapi/model/`)
- **API Interfaces** - контракты REST API (`target/generated-sources/openapi/api/`)
- **Service Interfaces** - контракты бизнес-логики (`target/generated-sources/services/`)

### ✍️ Создаётся вручную (реализация):
- **Entities** - JPA сущности (`src/main/java/entity/`)
- **Repositories** - Spring Data репозитории (`src/main/java/repository/`)
- **Controllers** - REST контроллеры (`src/main/java/controller/`)
- **ServiceImpl** - реализация бизнес-логики (`src/main/java/service/impl/`)
- **Flyway миграции** - SQL миграции (`src/main/resources/db/migration/`)

---

## Основные принципы архитектуры

1. **Соответствие API-SWAGGER:** Структура бекенд кода должна соответствовать структуре API-SWAGGER
2. **Иерархическая структура:** От общего к частному, соответствие структуре API-SWAGGER
3. **Именование:** Файлы - `PascalCase.java` для Java файлов, директории - `kebab-case` или `snake_case`
4. **Ограничение размера:** МАКСИМУМ 400 строк на файл, если больше - разбить на несколько файлов
5. **Разделение ответственности:** Разделение на Controllers, Services, Repositories, Entities
6. **Контракты в `target/`:** Сгенерированные контракты только в `target/`, никогда не редактируем
7. **Реализация в `src/main/java/`:** Вся реализация создаётся вручную и никогда не перегенерируется

---

## Структура директорий бекенд кода

```
BACK-JAVA/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/necpgame/backjava/
│   │   │       ├── NecpgameBackendApplication.java
│   │   │       │
│   │   │       ├── controller/                    # REST Controllers (ВРУЧНУЮ)
│   │   │       │   ├── AuthController.java        # implements AuthApi
│   │   │       │   ├── CharactersController.java   # implements CharactersApi
│   │   │       │   └── ...
│   │   │       │
│   │   │       ├── service/
│   │   │       │   └── impl/                       # Service Implementations (ВРУЧНУЮ)
│   │   │       │       ├── AuthServiceImpl.java    # implements AuthService
│   │   │       │       ├── CharactersServiceImpl.java
│   │   │       │       └── ...
│   │   │       │
│   │   │       ├── repository/                     # Spring Data Repositories (ВРУЧНУЮ)
│   │   │       │   ├── AccountRepository.java
│   │   │       │   ├── CharacterRepository.java
│   │   │       │   └── ...
│   │   │       │
│   │   │       ├── entity/                         # JPA Entities (ВРУЧНУЮ)
│   │   │       │   ├── AccountEntity.java
│   │   │       │   ├── CharacterEntity.java
│   │   │       │   └── ...
│   │   │       │
│   │   │       ├── exception/                      # Custom Exceptions (ВРУЧНУЮ)
│   │   │       │   ├── NotFoundException.java
│   │   │       │   ├── ConflictException.java
│   │   │       │   └── ...
│   │   │       │
│   │   │       ├── mapper/                         # Entity ↔ DTO Mappers (ВРУЧНУЮ)
│   │   │       │   ├── AccountMapper.java
│   │   │       │   ├── CharacterMapper.java
│   │   │       │   └── ...
│   │   │       │
│   │   │       └── config/                         # Конфигурация (ВРУЧНУЮ)
│   │   │           ├── SecurityConfig.java
│   │   │           ├── WebConfig.java
│   │   │           └── DatabaseConfig.java
│   │   │
│   │   └── resources/
│   │       ├── application.yml
│   │       └── db/
│   │           └── migration/                     # Flyway миграции (ВРУЧНУЮ)
│   │               ├── V001__create_accounts_table.sql
│   │               ├── V002__create_characters_table.sql
│   │               └── ...
│   │
│   └── test/
│       └── java/
│           └── com/necpgame/backjava/
│               └── ...                             # Тесты
│
├── target/
│   └── generated-sources/                          # КОНТРАКТЫ (автогенерация)
│       ├── openapi/                                # DTOs + API Interfaces
│       │   └── src/main/java/com/necpgame/backjava/
│       │       ├── api/                            # API Interfaces
│       │       │   ├── AuthApi.java
│       │       │   ├── CharactersApi.java
│       │       │   └── ...
│       │       └── model/                          # DTOs
│       │           ├── LoginRequest.java
│       │           ├── LoginResponse.java
│       │           ├── Account.java
│       │           └── ...
│       └── services/                               # Service Interfaces
│           └── src/main/java/com/necpgame/backjava/service/
│               ├── AuthService.java
│               ├── CharactersService.java
│               └── ...
│
├── scripts/
│   ├── generate-openapi-layers.ps1               # Скрипт генерации контрактов
│   ├── autocommit.ps1
│   └── autocommit.sh
│
└── pom.xml                                        # Maven конфигурация
```

---

## Соответствие API-SWAGGER

**ВАЖНО:** Структура бекенд кода должна соответствовать структуре API-SWAGGER

### Примеры соответствия:

| API-SWAGGER | BACK-JAVA (контракты) | BACK-JAVA (реализация) |
|-------------|------------------------|-------------------------|
| `api/v1/auth/` | `target/.../api/AuthApi.java` | `src/.../controller/AuthController.java` |
| `api/v1/characters/` | `target/.../api/CharactersApi.java` | `src/.../controller/CharactersController.java` |
| `api/v1/gameplay/social/` | `target/.../api/SocialApi.java` | `src/.../controller/gameplay/SocialController.java` |

**Принципы соответствия:**
- Имена API Interfaces соответствуют путям в OpenAPI
- Controllers реализуют соответствующие API Interfaces
- ServiceImpl реализуют соответствующие Service Interfaces
- Именование: файлы - `PascalCase.java`, директории - `kebab-case` или `snake_case`

---

## Разделение ответственности

### 1. Controllers (REST API слой)

**Создаётся:** ВРУЧНУЮ в `src/main/java/controller/`

**Ответственность:**
- Реализует сгенерированные API Interfaces из `target/generated-sources/openapi/api/`
- Обработка HTTP запросов и ответов
- Валидация входных данных (через Bean Validation)
- Делегирование бизнес-логики Service слою
- Обработка исключений (через `@ControllerAdvice`)

**Пример:**
```java
@RestController
@RequiredArgsConstructor
public class AuthController implements AuthApi {
    private final AuthService authService;
    
    @Override
    public ResponseEntity<LoginResponse> login(LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
```

### 2. Services (бизнес-логика)

**Service Interfaces создаются:** АВТОМАТИЧЕСКИ в `target/generated-sources/services/`

**ServiceImpl создаётся:** ВРУЧНУЮ в `src/main/java/service/impl/`

**Ответственность:**
- Вся бизнес-логика приложения
- Работа с Repositories
- Маппинг между Entity и DTO
- Транзакции (`@Transactional`)
- Обработка ошибок бизнес-правил

**Пример:**
```java
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    
    @Override
    @Transactional
    public LoginResponse login(LoginRequest request) {
        // Бизнес-логика
    }
}
```

### 3. Repositories (доступ к данным)

**Создаётся:** ВРУЧНУЮ в `src/main/java/repository/`

**Ответственность:**
- Работа с базой данных через Spring Data JPA
- CRUD операции (автоматически через JpaRepository)
- Custom queries (JPQL, native SQL)
- Derived query methods

**Пример:**
```java
@Repository
public interface AccountRepository extends JpaRepository<AccountEntity, UUID> {
    Optional<AccountEntity> findByEmail(String email);
    boolean existsByEmail(String email);
}
```

### 4. Entities (доменная модель)

**Создаётся:** ВРУЧНУЮ в `src/main/java/entity/`

**Ответственность:**
- JPA сущности для работы с БД
- Relationships: `@OneToMany`, `@ManyToOne`, `@ManyToMany`
- Indexes: `@Index`
- Constraints: `@Column(nullable = false, unique = true)`
- Lifecycle callbacks: `@PrePersist`, `@PreUpdate`

**Пример:**
```java
@Entity
@Table(name = "accounts")
public class AccountEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(nullable = false, unique = true)
    private String email;
    
    // Relationships
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    private List<CharacterEntity> characters = new ArrayList<>();
}
```

### 5. DTOs (контракты данных)

**Создаётся:** АВТОМАТИЧЕСКИ в `target/generated-sources/openapi/model/`

**Ответственность:**
- Модели данных для REST API
- Валидация: `@NotNull`, `@Email`, `@Size`, `@Pattern`
- JSON сериализация/десериализация

**Пример (сгенерированный):**
```java
public class LoginRequest {
    @NotNull
    @Email
    private String email;
    
    @NotNull
    @Size(min = 8)
    private String password;
    
    // Геттеры/сеттеры
}
```

---

## Ограничение размера файлов

**ОБЯЗАТЕЛЬНО:** Каждый файл не должен превышать 400 строк

### Если файл больше 400 строк:

#### Вариант 1: Разделить Controllers по методам HTTP

```
controller/
├── AuthController.java          # Главный контроллер
├── AuthControllerGet.java       # GET методы
├── AuthControllerPost.java      # POST методы
└── AuthControllerDelete.java    # DELETE методы
```

#### Вариант 2: Вынести логику в отдельные сервисы

```
service/impl/
├── AuthServiceImpl.java         # Основная логика
├── AuthValidationService.java   # Валидация
└── AuthTokenService.java        # Работа с токенами
```

#### Вариант 3: Разделить Repository queries

```
repository/
├── AccountRepository.java       # Основные queries
├── AccountSearchRepository.java # Поиск
└── AccountStatsRepository.java  # Статистика
```

---

## Примеры структуры проекта

### Пример 1: Простой API (Authentication)

```
BACK-JAVA/
├── target/generated-sources/
│   ├── openapi/
│   │   ├── api/AuthApi.java
│   │   └── model/
│   │       ├── LoginRequest.java
│   │       ├── LoginResponse.java
│   │       └── RegisterRequest.java
│   └── services/
│       └── AuthService.java
│
└── src/main/java/
    ├── controller/
    │   └── AuthController.java      # implements AuthApi
    ├── service/impl/
    │   └── AuthServiceImpl.java     # implements AuthService
    ├── repository/
    │   └── AccountRepository.java
    ├── entity/
    │   └── AccountEntity.java
    ├── mapper/
    │   └── AccountMapper.java
    └── exception/
        └── UnauthorizedException.java
```

### Пример 2: Сложный API с иерархией (Characters)

```
BACK-JAVA/
├── target/generated-sources/
│   ├── openapi/
│   │   ├── api/
│   │   │   ├── CharactersApi.java
│   │   │   ├── CharacterClassesApi.java
│   │   │   └── CharacterOriginsApi.java
│   │   └── model/
│   │       ├── Character.java
│   │       ├── CharacterClass.java
│   │       └── CharacterOrigin.java
│   └── services/
│       ├── CharactersService.java
│       ├── CharacterClassesService.java
│       └── CharacterOriginsService.java
│
└── src/main/java/
    ├── controller/
    │   ├── CharactersController.java
    │   ├── CharacterClassesController.java
    │   └── CharacterOriginsController.java
    ├── service/impl/
    │   ├── CharactersServiceImpl.java
    │   ├── CharacterClassesServiceImpl.java
    │   └── CharacterOriginsServiceImpl.java
    ├── repository/
    │   ├── CharacterRepository.java
    │   ├── CharacterClassRepository.java
    │   └── CharacterOriginRepository.java
    ├── entity/
    │   ├── CharacterEntity.java
    │   ├── CharacterClassEntity.java
    │   └── CharacterOriginEntity.java
    └── mapper/
        ├── CharacterMapper.java
        ├── CharacterClassMapper.java
        └── CharacterOriginMapper.java
```

---

## Важные моменты

### ✅ DO (делать):

1. ✅ **Использовать шаблоны** из [MANUAL-TEMPLATES.md](./MANUAL-TEMPLATES.md)
2. ✅ **Соблюдать иерархию** - соответствие структуре API-SWAGGER
3. ✅ **Генерировать контракты** через PowerShell скрипт
4. ✅ **Создавать реализацию вручную** в `src/main/java/`
5. ✅ **Проверять размер файлов** - не более 400 строк
6. ✅ **Использовать Flyway миграции** для управления БД
7. ✅ **Покрывать тестами** - не менее 50%

### ❌ DON'T (не делать):

1. ❌ **Не редактировать сгенерированные контракты** в `target/`
2. ❌ **Не генерировать Entities/Repositories/Controllers/ServiceImpl** автоматически
3. ❌ **Не хардкодить данные** в коде - всё в БД
4. ❌ **Не создавать файлы больше 400 строк**
5. ❌ **Не дублировать код** - использовать DRY принцип
6. ❌ **Не смешивать ответственности** - SOLID принцип

---

📖 **Навигация:** [БЭКТАСК.MD](./БЭКТАСК.MD) | [OPENAPI-GENERATION-GUIDE.md](./OPENAPI-GENERATION-GUIDE.md) | [MANUAL-TEMPLATES.md](./MANUAL-TEMPLATES.md)
