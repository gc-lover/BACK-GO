# БЭКТАСК-FAQ.md

**Типичные проблемы, решения и примеры использования**

📖 **Навигация:** [БЭКТАСК.MD](./БЭКТАСК.MD) | [БЭКТАСК-PROCESS.md](./БЭКТАСК-PROCESS.md) | [БЭКТАСК-REQUIREMENTS.md](./БЭКТАСК-REQUIREMENTS.md) | [БЭКТАСК-ARCHITECTURE.md](./БЭКТАСК-ARCHITECTURE.md)

---

## Типичные проблемы и решения

### Q: Как выбрать между OpenAPI Generator и Swagger Codegen?

**A:** Рекомендуется использовать OpenAPI Generator, так как он:
- Активно поддерживается и обновляется
- Поддерживает больше языков и фреймворков
- Имеет лучшую документацию
- Поддерживает последние версии OpenAPI спецификаций
- Лучше работает с Java Spring Boot серверным кодом
- Интегрируется с Maven через плагин

**Пример установки:**
```bash
# Через Maven (рекомендуется)
# Плагин уже настроен в pom.xml

# Или через npm (для CLI)
npm install @openapitools/openapi-generator-cli -g
```

---

### Q: Какой Java Spring Boot генератор использовать?

**A:** Для Java Spring Boot используется генератор `spring`:

**Через Maven (рекомендуется):**
```bash
mvn clean generate-sources
```

**Через OpenAPI Generator CLI:**
```bash
openapi-generator-cli generate \
  -i API-SWAGGER/api/v1/auth/character-creation.yaml \
  -g spring \
  -o BACK-JAVA/target/generated-sources/openapi \
  --additional-properties=library=spring-boot,useSpringBoot3=true,useJakartaEe=true
```

**Конфигурация в pom.xml:**
OpenAPI Generator настроен в `pom.xml` и автоматически генерирует код при сборке проекта.

---

### Q: Что делать, если API Swagger файл находится в поддиректории?

**A:** Используй полный путь к файлу или обнови `inputSpec` в `pom.xml`:

**Через Maven:**
Обнови `inputSpec` в `pom.xml`:
```xml
<inputSpec>${project.basedir}/../API-SWAGGER/api/v1/auth/character-creation.yaml</inputSpec>
```

**Через CLI:**
```bash
openapi-generator-cli generate \
  -i API-SWAGGER/api/v1/auth/character-creation.yaml \
  -g spring \
  -o BACK-JAVA/target/generated-sources/openapi
```

---

### Q: Что делать, если файл Controller превышает 400 строк?

**A:** ОБЯЗАТЕЛЬНО разбей файл на несколько файлов:

1. **Раздели по методам HTTP:**
   ```java
   // PersonalNpcController.java (основной controller)
   package com.necpgame.backjava.controllers;
   
   @RestController
   @RequiredArgsConstructor
   public class PersonalNpcController implements PersonalNpcApi {
       private final PersonalNpcService service;
   }
   
   // PersonalNpcControllerGet.java (GET методы)
   @RestController
   public class PersonalNpcControllerGet extends PersonalNpcController {
       @GetMapping("/api/v1/personal-npc")
       public ResponseEntity<List<PersonalNpc>> getPersonalNPCs() {
           // GET логика
       }
   }
   
   // PersonalNpcControllerPost.java (POST методы)
   @RestController
   public class PersonalNpcControllerPost extends PersonalNpcController {
       @PostMapping("/api/v1/personal-npc")
       public ResponseEntity<PersonalNpc> createPersonalNPC(@RequestBody PersonalNpc npc) {
           // POST логика
       }
   }
   ```

2. **Вынеси бизнес-логику в сервисы:**
   ```java
   // PersonalNpcService.java
   package com.necpgame.backjava.services;
   
   @Service
   @RequiredArgsConstructor
   public class PersonalNpcService {
       private final PersonalNpcRepository repository;
       
       public List<PersonalNpc> getAllNPCs() {
           // бизнес-логика
       }
   }
   ```

3. **Вынеси работу с БД в репозитории:**
   ```java
   // PersonalNpcRepository.java
   package com.necpgame.backjava.repositories;
   
   public interface PersonalNpcRepository extends JpaRepository<PersonalNpc, UUID> {
       // Spring Data JPA автоматически создает реализацию
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

**A:** Используй Flyway:

**Создание миграции:**
```bash
# Создать файл вручную: src/main/resources/db/migration/V{версия}__{название}.sql
# Пример: V6__create_personal_npc_table.sql
```

**Структура миграции:**
```sql
-- V6__create_personal_npc_table.sql
CREATE TABLE IF NOT EXISTS personal_npc (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    owner_id UUID NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    FOREIGN KEY (owner_id) REFERENCES accounts(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_personal_npc_owner_id ON personal_npc(owner_id);
```

**Применение миграций:**
```bash
# Автоматически при запуске Spring Boot
mvn spring-boot:run

# Или через Maven плагин Flyway
mvn flyway:migrate
```

**ВАЖНО: Идемпотентность миграций**
- Используй `CREATE TABLE IF NOT EXISTS` вместо `CREATE TABLE`
- Используй `CREATE INDEX IF NOT EXISTS` вместо `CREATE INDEX`
- Используй `DROP TABLE IF EXISTS` вместо `DROP TABLE`
- Миграции должны быть безопасными для повторного применения

---

### Q: Как создать тестовые данные (seed данные)?

**A:** **ОБЯЗАТЕЛЬНО: Проверка существования данных перед заливкой**

**Способ 1: Seed миграция (рекомендуется)**
```sql
-- V7__seed_personal_npc_data.sql
DO $$
BEGIN
    -- Проверка наличия данных перед вставкой
    IF NOT EXISTS (SELECT 1 FROM personal_npc LIMIT 1) THEN
        INSERT INTO personal_npc (id, name, owner_id) VALUES
        ('00000000-0000-0000-0000-000000000001', 'Test NPC 1', '00000000-0000-0000-0000-000000000001'),
        ('00000000-0000-0000-0000-000000000002', 'Test NPC 2', '00000000-0000-0000-0000-000000000001');
    END IF;
END $$;
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
# Через Flyway (автоматически при запуске Spring Boot)
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
BACK-JAVA/src/main/java/com/necpgame/backjava/
├── api/                                    # Сгенерированные интерфейсы (target/generated-sources/openapi)
├── model/                                  # Сгенерированные модели (target/generated-sources/openapi)
├── controllers/                            # Контроллеры
│   └── gameplay/
│       └── social/
│           └── PersonalNpcController.java
├── services/                               # Сервисы
│   └── gameplay/
│       └── social/
│           └── PersonalNpcService.java
├── repositories/                           # Репозитории
│   └── gameplay/
│       └── social/
│           └── PersonalNpcRepository.java
├── entities/                               # JPA Entities
│   └── gameplay/
│       └── social/
│           └── PersonalNpcEntity.java
└── config/                                 # Конфигурация
    └── SecurityConfig.java
```

---

### Q: Как настроить PostgreSQL в Docker?

**A:** **ОБЯЗАТЕЛЬНО: PostgreSQL в Docker для локальной разработки**

**ВАЖНО:** В репозитории уже создан `docker-compose.yml` с настроенным PostgreSQL контейнером. Используй его!

**Запуск PostgreSQL контейнера:**
```bash
# Запуск контейнера (из корня репозитория BACK-JAVA)
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

**Файл docker-compose.yml уже создан** в корне репозитория `BACK-JAVA/docker-compose.yml` и содержит:
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
# Автоматически при запуске Spring Boot через Flyway
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

## Использование Maven для генерации API

### Генерация через Maven

**Рекомендуется:** Использовать Maven плагин OpenAPI Generator, который уже настроен в `pom.xml`:

```bash
# Генерация Java Spring Boot кода
mvn clean generate-sources

# Или при сборке проекта
mvn clean install
```

**Конфигурация в pom.xml:**
OpenAPI Generator настроен в `pom.xml` и автоматически генерирует код при сборке проекта.

**Результат:**
- Controllers (интерфейсы) в `target/generated-sources/openapi/src/main/java/com/necpgame/backjava/api/`
- Models/DTOs в `target/generated-sources/openapi/src/main/java/com/necpgame/backjava/model/`

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
powershell -Command "(Get-Content src\main\java\com\necpgame\backjava\controllers\PersonalNpcController.java).Count"

# Подсчет строк во всех Java файлах (Windows)
powershell -Command "Get-ChildItem -Recurse -Filter *.java | ForEach-Object { (Get-Content $_.FullName).Count }"
```

---

📖 **Навигация:** [БЭКТАСК.MD](./БЭКТАСК.MD) | [БЭКТАСК-PROCESS.md](./БЭКТАСК-PROCESS.md) | [БЭКТАСК-REQUIREMENTS.md](./БЭКТАСК-REQUIREMENTS.md) | [БЭКТАСК-ARCHITECTURE.md](./БЭКТАСК-ARCHITECTURE.md)
