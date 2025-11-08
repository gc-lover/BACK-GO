# БЭКТАСК-ARCHITECTURE.md

**Архитектура, структура директорий и соответствие API-SWAGGER**

📖 **Навигация:** [БЭКТАСК.MD](./БЭКТАСК.MD) | [MANUAL-TEMPLATES.md](./MANUAL-TEMPLATES.md) | [БЭКТАСК-FAQ.md](./БЭКТАСК-FAQ.md)

---

## 🎯 Философия: Контракты vs Реализация

**OpenAPI спецификация = источник правды ТОЛЬКО для контрактов**

### ✅ Генерируется автоматически (контракты):
- **DTOs** - модели данных (`microservices/<service>/src/main/java/com/necpgame/<service>/model/`)
- **API Interfaces** - REST контракты (`microservices/<service>/src/main/java/com/necpgame/<service>/api/`)
- **Service Interfaces** - контракты бизнес-логики (`microservices/<service>/src/main/java/com/necpgame/<service>/service/`)

### ✍️ Создаётся вручную (реализация):
- **Entities** - JPA сущности (`microservices/<service>/src/main/java/com/necpgame/<service>/entity/`)
- **Repositories** - Spring Data репозитории (`microservices/<service>/src/main/java/com/necpgame/<service>/repository/`)
- **Controllers** - REST контроллеры (`microservices/<service>/src/main/java/com/necpgame/<service>/controller/`)
- **ServiceImpl** - реализация бизнес-логики (`microservices/<service>/src/main/java/com/necpgame/<service>/service/impl/`)
- **Mappers (MapStruct)** - Entity ↔ DTO (`microservices/<service>/src/main/java/com/necpgame/<service>/mapper/`)
- **Liquibase миграции** - XML/YAML changelog (`microservices/<service>/src/main/resources/db/changelog/`)

---

## Основные принципы архитектуры

1. **Соответствие API-SWAGGER:** Структура бекенд кода должна соответствовать структуре API-SWAGGER
2. **Иерархическая структура:** От общего к частному, соответствие структуре API-SWAGGER
3. **Именование:** Файлы - `PascalCase.java` для Java файлов, директории - `kebab-case` или `snake_case`
4. **Ограничение размера:** МАКСИМУМ 400 строк на файл, если больше - разбить на несколько файлов
5. **Разделение ответственности:** Разделение на Controllers, Services, Repositories, Entities
6. **Контракты в `microservices/<service>/src/main/java/`:** Сгенерированные файлы хранятся прямо в каталоге нужного микросервиса, не редактируются вручную
7. **Реализация рядом с контрактами:** Вся реализация создаётся вручную внутри того же микросервиса и никогда не перегенерируется

---

## Структура директорий бекенд кода

```
BACK-GO/
├── microservices/
│   ├── auth-service/
│   │   ├── src/
│   │   │   ├── main/
│   │   │   │   ├── java/com/necpgame/authservice/
│   │   │   │   │   ├── AuthServiceApplication.java
│   │   │   │   │   ├── api/              # Сгенерированные интерфейсы (OpenAPI)
│   │   │   │   │   ├── model/            # Сгенерированные DTO
│   │   │   │   │   ├── service/          # Сгенерированные сервисные контракты
│   │   │   │   │   ├── controller/       # Реализация REST (вручную)
│   │   │   │   │   ├── service/impl/     # Бизнес-логика (вручную)
│   │   │   │   │   ├── repository/       # Spring Data (вручную)
│   │   │   │   │   ├── entity/           # JPA сущности (вручную)
│   │   │   │   │   ├── mapper/           # MapStruct (вручную)
│   │   │   │   │   └── config/           # Конфигурация (вручную)
│   │   │   │   └── resources/
│   │   │   │       ├── application.yml
│   │   │   │       └── db/changelog/     # Liquibase миграции
│   │   │   └── test/java/com/necpgame/authservice/  # Тесты
│   │   └── pom.xml
│   ├── social-service/
│   │   └── ... (аналогичная структура)
│   └── <другие микросервисы>/
│       └── ...
│
├── infrastructure/
│   ├── api-gateway/           # Spring Cloud Gateway (роутинг)
│   ├── config-server/         # Централизованная конфигурация
│   └── service-discovery/     # Eureka/Consul для регистрации сервисов
│
├── scripts/                   # Скрипты генерации и вспомогательные утилиты
├── templates/                 # Кастомные шаблоны OpenAPI Generator
└── pom.xml                    # Управляющий Maven-модуль
```

---

## Соответствие API-SWAGGER

**ВАЖНО:** Структура бекенд кода должна соответствовать структуре API-SWAGGER

### Примеры соответствия:

| API-SWAGGER | BACK-GO (контракты) | BACK-GO (реализация) |
|-------------|------------------------|-------------------------|
| `api/v1/auth/` | `microservices/auth-service/src/main/java/com/necpgame/authservice/api/AuthApi.java` | `microservices/auth-service/src/main/java/com/necpgame/authservice/controller/AuthController.java` |
| `api/v1/characters/` | `microservices/character-service/src/main/java/com/necpgame/characterservice/api/CharactersApi.java` | `microservices/character-service/src/main/java/com/necpgame/characterservice/controller/CharactersController.java` |
| `api/v1/gameplay/social/` | `microservices/gameplay-service/src/main/java/com/necpgame/gameplayservice/api/SocialApi.java` | `microservices/gameplay-service/src/main/java/com/necpgame/gameplayservice/controller/SocialController.java` |

**Принципы соответствия:**
- Имена API Interfaces соответствуют путям в OpenAPI
- Controllers реализуют соответствующие API Interfaces
- ServiceImpl реализуют соответствующие Service Interfaces
- Именование: файлы - `PascalCase.java`, директории - `kebab-case` или `snake_case`
- **x-microservice обязателен:** в каждой OpenAPI спецификации указываем `x-microservice` с именем микросервиса (`auth-service`, `social-service` и т.д.), генератор использует это поле как единственный источник для размещения контрактов

---

## Разделение ответственности

### 1. Controllers (REST API слой)

**Создаётся:** ВРУЧНУЮ в `microservices/<service>/src/main/java/com/necpgame/<service>/controller/`

**Ответственность:**
- Реализует сгенерированные API Interfaces из `microservices/<service>/src/main/java/com/necpgame/<service>/api/`
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

**Service Interfaces создаются:** АВТОМАТИЧЕСКИ в `microservices/<service>/src/main/java/com/necpgame/<service>/service/`

**ServiceImpl создаётся:** ВРУЧНУЮ в `microservices/<service>/src/main/java/com/necpgame/<service>/service/impl/`

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

**Создаётся:** ВРУЧНУЮ в `microservices/<service>/src/main/java/com/necpgame/<service>/repository/`

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

**Создаётся:** ВРУЧНУЮ в `microservices/<service>/src/main/java/com/necpgame/<service>/entity/`

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

**Создаётся:** АВТОМАТИЧЕСКИ в `microservices/<service>/src/main/java/com/necpgame/<service>/model/`

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

#### Вариант 1: Разделить обработчики внутри микросервиса

```
microservices/auth-service/src/main/java/com/necpgame/authservice/controller/
├── AuthController.java              # Реализует AuthApi и делегирует обработчикам
└── handler/
    ├── LoginHandler.java            # Обработка login
    ├── RegisterHandler.java         # Обработка register
    └── TokenRefreshHandler.java     # Обработка refresh
```

#### Вариант 2: Вынести логику в отдельные сервисы

```
microservices/auth-service/src/main/java/com/necpgame/authservice/service/impl/
├── AuthServiceImpl.java             # Основная логика
├── AuthValidationService.java       # Валидация
└── AuthTokenService.java            # Работа с токенами
```

#### Вариант 3: Разделить Repository queries

```
microservices/auth-service/src/main/java/com/necpgame/authservice/repository/
├── AccountRepository.java           # Основные queries
├── AccountSearchRepository.java     # Поиск
└── AccountStatsRepository.java      # Статистика
```

---

## Примеры структуры проекта

### Пример 1: Простой API (auth-service)

```
microservices/auth-service/
├── src/main/java/com/necpgame/authservice/
│   ├── AuthServiceApplication.java
│   ├── api/
│   │   └── AuthApi.java
│   ├── model/
│   │   ├── LoginRequest.java
│   │   └── LoginResponse.java
│   ├── service/
│   │   └── AuthService.java
│   ├── controller/
│   │   └── AuthController.java            # implements AuthApi
│   ├── service/impl/
│   │   └── AuthServiceImpl.java           # implements AuthService
│   ├── repository/
│   │   └── AccountRepository.java
│   ├── entity/
│   │   └── AccountEntity.java
│   └── mapper/
│       └── AccountMapper.java
└── src/main/resources/db/changelog/
    ├── db.changelog-master.xml
    └── changes/
        └── 001-create-accounts-table.xml
```

### Пример 2: Сложный API с иерархией (character-service)

```
microservices/character-service/
├── src/main/java/com/necpgame/characterservice/
│   ├── CharacterServiceApplication.java
│   ├── api/
│   │   ├── CharactersApi.java
│   │   ├── CharacterClassesApi.java
│   │   └── CharacterOriginsApi.java
│   ├── model/
│   │   ├── GameCharacter.java
│   │   ├── GameCharacterClass.java
│   │   └── GameCharacterOrigin.java
│   ├── service/
│   │   ├── CharactersService.java
│   │   ├── CharacterClassesService.java
│   │   └── CharacterOriginsService.java
│   ├── controller/
│   │   ├── CharactersController.java
│   │   ├── CharacterClassesController.java
│   │   └── CharacterOriginsController.java
│   ├── service/impl/
│   │   ├── CharactersServiceImpl.java
│   │   ├── CharacterClassesServiceImpl.java
│   │   └── CharacterOriginsServiceImpl.java
│   ├── repository/
│   │   ├── CharacterRepository.java
│   │   ├── CharacterClassRepository.java
│   │   └── CharacterOriginRepository.java
│   ├── entity/
│   │   ├── CharacterEntity.java
│   │   ├── CharacterClassEntity.java
│   │   └── CharacterOriginEntity.java
│   └── mapper/
│       ├── CharacterMapper.java
│       ├── CharacterClassMapper.java
│       └── CharacterOriginMapper.java
└── src/main/resources/db/changelog/
    └── changes/
        ├── 002-create-character-classes-table.xml
        ├── 003-create-character-origins-table.xml
        └── 004-create-characters-table.xml
```

---

## Важные моменты

### ✅ DO (делать):

1. ✅ **Использовать шаблоны** из [MANUAL-TEMPLATES.md](./MANUAL-TEMPLATES.md)
2. ✅ **Соблюдать иерархию** - соответствие структуре API-SWAGGER
3. ✅ **Генерировать контракты** через PowerShell скрипт
4. ✅ **Создавать реализацию вручную** в `microservices/<service>/src/main/java/`
5. ✅ **Проверять размер файлов** - не более 400 строк
6. ✅ **Использовать Liquibase миграции** для управления БД
7. ✅ **Использовать MapStruct** для маппинга Entity ↔ DTO
8. ✅ **Покрывать тестами** - не менее 50%

### ❌ DON'T (не делать):

1. ❌ **Не редактировать сгенерированные контракты** в `microservices/<service>/src/main/java/com/necpgame/<service>/api`, `model`, `service`
2. ❌ **Не генерировать Entities/Repositories/Controllers/ServiceImpl** автоматически
3. ❌ **Не хардкодить данные** в коде - всё в БД
4. ❌ **Не создавать файлы больше 400 строк**
5. ❌ **Не дублировать код** - использовать DRY принцип
6. ❌ **Не смешивать ответственности** - SOLID принцип

---

## Roadmap 2025-11 (world-service / economy-service / social-service)
- **Sprint 2025-11-10 → 2025-11-16:** world-service реализация API-TASK-241 (world-interaction-suite): контроллеры `WorldStateController`, `WorldEventsController`, WebSocket `/ws/world`.
- **Sprint 2025-11-17 → 2025-11-23:** economy-service расширение `MarketInterventionController` под API-TASK-242 (MFA, симуляции, rollback).
- **Sprint 2025-11-24 → 2025-11-30:** social-service добавление `SocialResonanceController` и кампаний (API-TASK-243), синхронизация с crisis pipeline.
- **DevOps:** обновить Kafka topics (`world.state.tick`, `market.intervention.status`, `social.index.changed`) и мониторинг Prometheus/Grafana по SLA из .BRAIN документа.

---

📖 **Навигация:** [БЭКТАСК.MD](./БЭКТАСК.MD) | [MANUAL-TEMPLATES.md](./MANUAL-TEMPLATES.md) | [БЭКТАСК-FAQ.md](./БЭКТАСК-FAQ.md)
