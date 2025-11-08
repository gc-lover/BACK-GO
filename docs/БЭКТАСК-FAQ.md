# БЭКТАСК-FAQ.md

**Типичные проблемы, решения и примеры использования**

📖 **Навигация:** [БЭКТАСК.MD](./БЭКТАСК.MD) | [MANUAL-TEMPLATES.md](./MANUAL-TEMPLATES.md) | [БЭКТАСК-ARCHITECTURE.md](./БЭКТАСК-ARCHITECTURE.md)

---

## Типичные проблемы и решения

### Q: Как генерировать контракты из OpenAPI?

**A:** Используй PowerShell скрипт для генерации в микросервисы:

```powershell
# Валидация перед генерацией
.\scripts\validate-openapi.ps1 -ApiDirectory ../API-SWAGGER/api/v1/

# Генерация во все микросервисы
.\scripts\generate-openapi-microservices.ps1 -ApiDirectory ../API-SWAGGER/api/v1/
```

**Что генерируется:**
- ✅ DTOs - модели данных
- ✅ API Interfaces - контракты REST API
- ✅ Service Interfaces - контракты бизнес-логики

**Где находится:**
- `microservices/<service>/src/main/java/com/necpgame/<service>/model/` - DTOs
- `microservices/<service>/src/main/java/com/necpgame/<service>/api/` - API Interfaces
- `microservices/<service>/src/main/java/com/necpgame/<service>/service/` - Service Interfaces
- ⚠️ Скрипт читает `x-microservice` из OpenAPI спецификации и размещает контракты только в обозначенном микросервисе

---

### Q: Как создать реализацию после генерации контрактов?

**A:** Используй шаблоны из [MANUAL-TEMPLATES.md](./MANUAL-TEMPLATES.md):

**1. Entities** - создай JPA сущности в `microservices/<service>/src/main/java/com/necpgame/<service>/entity/`
**2. Repositories** - создай Spring Data репозитории в `microservices/<service>/src/main/java/com/necpgame/<service>/repository/`
**3. Controllers** - создай REST контроллеры в `microservices/<service>/src/main/java/com/necpgame/<service>/controller/`
**4. ServiceImpl** - создай реализации сервисов в `microservices/<service>/src/main/java/com/necpgame/<service>/service/impl/`

**Все шаблоны с примерами кода в:** [MANUAL-TEMPLATES.md](./MANUAL-TEMPLATES.md)

---

### Q: Почему Entities/Repositories не генерируются автоматически?

**A:** Мы сознательно отказались от автогенерации реализации, потому что:

**Проблемы автогенерации:**
- ❌ Перезапись кастомного кода при перегенерации
- ❌ Невозможно описать relationships в OpenAPI
- ❌ Отсутствие indexes, constraints
- ❌ Нет поддержки custom queries

**Преимущества ручного создания:**
- ✅ Полный контроль над кодом
- ✅ Relationships: `@OneToMany`, `@ManyToOne`, `@ManyToMany`
- ✅ Indexes, constraints, lifecycle callbacks
- ✅ Custom queries, сложная логика
- ✅ Нет риска перезаписи кода

---

### Q: Что делать, если файл Controller превышает 400 строк?

**A:** ОБЯЗАТЕЛЬНО разбей ответственность внутри микросервиса, сохраняя единый контроллер, который реализует OpenAPI интерфейс:

1. **Введи обработчики (handler) для отдельных use-case:**
   ```java
   // microservices/social-service/src/main/java/com/necpgame/socialservice/controller/SocialController.java
   @RestController
   @RequiredArgsConstructor
   public class SocialController implements SocialApi {
       private final GetFeedsHandler getFeedsHandler;
       private final PublishPostHandler publishPostHandler;
   
       @Override
       public ResponseEntity<GetFeedsResponse> getFeeds(UUID playerId) {
           return ResponseEntity.ok(getFeedsHandler.handle(playerId));
       }
   
       @Override
       public ResponseEntity<PostResponse> publishPost(UUID playerId, PostRequest request) {
           return ResponseEntity.ok(publishPostHandler.handle(playerId, request));
       }
   }
   ```

2. **Каждый handler содержит отдельную бизнес-логику:**
   ```java
   // microservices/social-service/src/main/java/com/necpgame/socialservice/controller/handler/GetFeedsHandler.java
   @Component
   @RequiredArgsConstructor
   public class GetFeedsHandler {
       private final SocialFeedService socialFeedService;
   
       public GetFeedsResponse handle(UUID playerId) {
           return socialFeedService.fetchFeeds(playerId);
       }
   }
   ```

3. **Раздели сервисный слой, если он разрастается:**
   ```java
   // microservices/social-service/src/main/java/com/necpgame/socialservice/service/impl/SocialFeedServiceImpl.java
   @Service
   @RequiredArgsConstructor
   public class SocialFeedServiceImpl implements SocialFeedService {
       private final SocialFeedRepository socialFeedRepository;
       private final SocialFeedMapper socialFeedMapper;
   
       @Override
       public GetFeedsResponse fetchFeeds(UUID playerId) {
           // бизнес-логика + MapStruct
       }
   }
   ```

---

### Q: Как обрабатывать ошибки в Controllers?

**A:** Используй стандартные HTTP коды и @ControllerAdvice:

```java
@RestController
@RequiredArgsConstructor
public class PersonalNpcController implements PersonalNpcApi {
    
    private final PersonalNpcService service;
    
    @Override
    public ResponseEntity<PersonalNpc> getPersonalNPC(UUID id) {
        try {
            PersonalNpc npc = service.getNPCById(id);
            return ResponseEntity.ok(npc);
        } catch (NotFoundException e) {
            log.error("Failed to get NPC: {}", id, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            log.error("Unexpected error getting NPC: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

// Глобальный обработчик ошибок
@ControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse(e.getMessage()));
    }
    
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidation(ValidationException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(new ErrorResponse(e.getMessage()));
    }
}
```

---

### Q: Как использовать транзакции для критических операций?

**A:** Используй @Transactional аннотацию:

```java
@Service
@RequiredArgsConstructor
public class PersonalNpcService {
    
    private final PersonalNpcRepository repository;
    
    @Transactional
    public PersonalNpc createNPCWithRelations(PersonalNpc npc) {
        // Все операции в одном методе выполняются в транзакции
        PersonalNpc saved = repository.save(npc);
        
        // Если произойдет ошибка, все изменения откатятся
        createNPCRelations(saved);
        
        return saved;
    }
    
    @Transactional(readOnly = true)
    public List<PersonalNpc> getAllNPCs() {
        // Только чтение, оптимизация производительности
        return repository.findAll();
    }
}
```

---

### Q: Как создать миграции БД?

**A:** Создай Liquibase миграции вручную на основе Entity классов:

**Процесс:**
1. Создай Entity в `microservices/<service>/src/main/java/com/necpgame/<service>/entity/`
2. Проанализируй поля, relationships, constraints
3. Создай XML changelog в `microservices/<service>/src/main/resources/db/changelog/changes/`

**Пример Entity:**
```java
@Entity
@Table(name = "accounts")
public class AccountEntity {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(nullable = false, unique = true)
    private String email;
}
```

**Соответствующая миграция:**
```xml
<!-- 001-create-accounts-table.xml -->
<changeSet id="001-create-accounts-table" author="system">
    <createTable tableName="accounts">
        <column name="id" type="UUID" defaultValueComputed="gen_random_uuid()">
            <constraints primaryKey="true" nullable="false"/>
        </column>
        <column name="email" type="VARCHAR(255)">
            <constraints nullable="false" unique="true"/>
        </column>
        <column name="created_at" type="TIMESTAMP" defaultValueComputed="CURRENT_TIMESTAMP">
            <constraints nullable="false"/>
        </column>
    </createTable>
    
    <createIndex tableName="accounts" indexName="idx_accounts_email">
        <column name="email"/>
    </createIndex>
</changeSet>
```

**Применение миграций:**
```bash
# Автоматически при запуске Spring Boot
mvn spring-boot:run
```

**ВАЖНО: Отслеживание изменений**
- Liquibase автоматически отслеживает применённые changeSet в таблице `databasechangelog`
- Каждый changeSet должен иметь уникальный `id` и `author`
- Повторное применение применённых changeSet не происходит

---

### Q: Как создать тестовые данные (seed данные)?

**A:** **ОБЯЗАТЕЛЬНО: Проверка существования данных перед заливкой**

**Способ 1: Seed миграция (рекомендуется)**
```xml
<!-- 007-seed-personal-npc-data.xml -->
<changeSet id="007-seed-personal-npc-data" author="system">
    <preConditions onFail="MARK_RAN">
        <sqlCheck expectedResult="0">SELECT COUNT(*) FROM personal_npc</sqlCheck>
    </preConditions>
    
    <insert tableName="personal_npc">
        <column name="id" value="00000000-0000-0000-0000-000000000001"/>
        <column name="name" value="Test NPC 1"/>
        <column name="owner_id" value="00000000-0000-0000-0000-000000000001"/>
    </insert>
    <insert tableName="personal_npc">
        <column name="id" value="00000000-0000-0000-0000-000000000002"/>
        <column name="name" value="Test NPC 2"/>
        <column name="owner_id" value="00000000-0000-0000-0000-000000000001"/>
    </insert>
</changeSet>
```

**Способ 2: Java CommandLineRunner**
```java
@Component
@RequiredArgsConstructor
public class PersonalNpcSeeder implements CommandLineRunner {
    
    private final PersonalNpcRepository repository;
    
    @Override
    public void run(String... args) {
        // Проверка наличия данных
        if (repository.count() > 0) {
            log.info("Seed data already exists, skipping...");
            return;
        }
        
        // Создание тестовых данных
        PersonalNpc npc1 = new PersonalNpc();
        npc1.setName("Test NPC 1");
        // ... установка полей
        repository.save(npc1);
        
        log.info("Seed data created successfully");
    }
}
```

**Применение seed данных:**
```bash
# Через Liquibase (автоматически при запуске Spring Boot)
mvn spring-boot:run

# Или через CommandLineRunner (автоматически при запуске)
```

**Принципы создания seed данных:**
1. **Проверка существования:** Всегда проверять наличие данных перед вставкой
2. **Идемпотентность:** Можно запускать несколько раз без дублирования
3. **Минимальность:** Только необходимые данные для тестирования функционала
4. **Откат:** Возможность отката seed данных (через отдельную миграцию)

---

### Q: Как настроить Spring Security для аутентификации?

**A:** Используй Spring Security с JWT:

```java
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    
    private final JwtAuthenticationFilter jwtFilter;
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/v1/auth/**").permitAll()
                .requestMatchers("/swagger-ui/**", "/api-docs/**").permitAll()
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
}

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private final JwtTokenProvider tokenProvider;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                   HttpServletResponse response, 
                                   FilterChain filterChain) throws ServletException, IOException {
        String token = extractToken(request);
        
        if (token != null && tokenProvider.validateToken(token)) {
            Authentication auth = tokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
        
        filterChain.doFilter(request, response);
    }
    
    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
```

---

### Q: Как организовать структуру директорий?

**A:** Следуй иерархии API-SWAGGER:

```
BACK-GO/microservices/<service>/src/main/java/com/necpgame/<service>/
├── api/                                    # Сгенерированные интерфейсы
├── model/                                  # Сгенерированные модели
├── service/                                # Сгенерированные интерфейсы сервисов
├── controller/                             # Контроллеры (вручную)
│   └── PersonalNpcController.java
├── service/impl/                           # Реализации сервисов (вручную)
│   └── PersonalNpcServiceImpl.java
├── repository/                             # Репозитории (вручную)
│   └── PersonalNpcRepository.java
├── entity/                                 # JPA Entities (вручную)
│   └── PersonalNpcEntity.java
└── config/                                 # Конфигурация (вручную)
    └── SecurityConfig.java
```

---

### Q: Как настроить PostgreSQL в Docker?

**A:** **ОБЯЗАТЕЛЬНО: PostgreSQL в Docker для локальной разработки**

**ВАЖНО:** В репозитории уже создан `docker-compose.yml` с настроенным PostgreSQL контейнером. Используй его!

**Запуск PostgreSQL контейнера:**
```bash
# Запуск контейнера (из корня репозитория BACK-GO)
docker-compose up -d

# Проверка статуса
docker-compose ps

# Просмотр логов
docker-compose logs postgres

# Остановка контейнера
docker-compose down

# Остановка с удалением данных (ОСТОРОЖНО!)
docker-compose down -v
```

**Файл docker-compose.yml уже создан** в корне репозитория `BACK-GO/docker-compose.yml` и содержит:
- PostgreSQL 15
- Настроенные переменные окружения
- Healthcheck для проверки готовности БД
- Volume для сохранения данных

**Подключение к БД:**
```
Host: localhost
Port: 5433 (внешний), 5432 (внутренний)
User: necpgame
Password: necpgame
Database: necpgame
```

**Пример подключения в Spring Boot:**
```yaml
# application.yml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5433/necpgame
    username: necpgame
    password: necpgame
    driver-class-name: org.postgresql.Driver
```

**Применение миграций:**
```bash
# Автоматически при запуске Spring Boot через Liquibase
mvn spring-boot:run
```

---

### Q: Как использовать структурированное логирование?

**A:** Используй Logback (встроен в Spring Boot):

```java
@Service
@Slf4j  // Lombok автоматически создает logger
public class PersonalNpcService {
    
    public PersonalNpc getNPCById(UUID id) {
        log.info("Getting NPC by id: {}", id);
        
        try {
            PersonalNpc npc = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("NPC not found: " + id));
            
            log.debug("NPC retrieved successfully: {}", npc);
            return npc;
        } catch (NotFoundException e) {
            log.warn("NPC not found: {}", id);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error getting NPC: {}", id, e);
            throw new InternalServerException("Failed to get NPC", e);
        }
    }
}
```

**Конфигурация логирования (application.yml):**
```yaml
logging:
  level:
    root: INFO
    com.necpgame: DEBUG
    org.springframework.web: INFO
    org.hibernate.SQL: DEBUG
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} - %msg%n"
```

---

## Workflow разработки

### Процесс создания нового API

**Шаг 1: Создать OpenAPI спецификацию**
```yaml
# API-SWAGGER/api/v1/users/users.yaml
paths:
  /users:
    get:
      operationId: listUsers
      responses:
        '200':
          content:
            application/json:
              schema:
                type: array
                items:
                  $ref: '#/components/schemas/User'
```

**Шаг 2: Сгенерировать контракты в целевой микросервис**
```powershell
.\scripts\validate-openapi.ps1 -ApiSpec ../API-SWAGGER/api/v1/users/users.yaml
.\scripts\generate-openapi-microservices.ps1 -ApiDirectory ../API-SWAGGER/api/v1/users/
```
> ⚠️ Убедись, что в спецификации задан `x-microservice`; без него скрипт не сможет определить целевой микросервис.

**Шаг 3: Создать реализацию вручную**

Используя [MANUAL-TEMPLATES.md](./MANUAL-TEMPLATES.md):
- Entity: `microservices/<service>/src/main/java/com/necpgame/<service>/entity/UserEntity.java`
- Repository: `microservices/<service>/src/main/java/com/necpgame/<service>/repository/UserRepository.java`
- Controller: `microservices/<service>/src/main/java/com/necpgame/<service>/controller/UsersController.java`
- ServiceImpl: `microservices/<service>/src/main/java/com/necpgame/<service>/service/impl/UsersServiceImpl.java`
- Mapper: `microservices/<service>/src/main/java/com/necpgame/<service>/mapper/UserMapper.java`

**Шаг 4: Создать Liquibase миграцию**
```xml
<!-- microservices/<service>/src/main/resources/db/changelog/changes/005-create-users-table.xml -->
<changeSet id="005-create-users-table" author="system">
    <createTable tableName="users">
        <column name="id" type="UUID">
            <constraints primaryKey="true" nullable="false"/>
        </column>
        <column name="email" type="VARCHAR(255)">
            <constraints nullable="false" unique="true"/>
        </column>
        <column name="username" type="VARCHAR(100)">
            <constraints nullable="false" unique="true"/>
        </column>
    </createTable>
</changeSet>
```

**Шаг 5: Компиляция и тестирование**
```bash
mvn clean compile
mvn test
mvn spring-boot:run
```

---

## Примеры команд

### Пример 1: Один API файл

```
Делай бекенд для API-SWAGGER/api/v1/auth/character-creation.yaml
```

### Пример 2: Директория с API

```
Делай бекенд для всех API из API-SWAGGER/api/v1/auth/
```

### Пример 3: Все API

```
Делай бекенд для всех API из API-SWAGGER/api/v1/
```

---

## Инструменты для проверки

### Валидация Java кода:

```bash
# Сборка проекта
mvn clean install

# Форматирование кода (через IDE или Spotless)
# Линтинг (Checkstyle)
mvn checkstyle:check

# Статический анализ (SpotBugs)
mvn spotbugs:check

# Проверка всех модулей
mvn verify
```

### Проверка размера файлов:

```bash
# Подсчет строк в файле (Windows)
powershell -Command "(Get-Content microservices\social-service\src\main\java\com\necpgame\socialservice\controller\SocialController.java).Count"

# Подсчет строк во всех Java файлах (Windows)
powershell -Command "Get-ChildItem -Recurse -Filter *.java | ForEach-Object { (Get-Content $_.FullName).Count }"
```

---

📖 **Навигация:** [БЭКТАСК.MD](./БЭКТАСК.MD) | [MANUAL-TEMPLATES.md](./MANUAL-TEMPLATES.md) | [БЭКТАСК-ARCHITECTURE.md](./БЭКТАСК-ARCHITECTURE.md)
