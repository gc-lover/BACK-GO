# План генерации полноценного кода из OpenAPI

## Цель
Генерация полного стека Java Spring Boot приложения из OpenAPI спецификации, включая:
- DTOs (Data Transfer Objects) - из OpenAPI Generator
- Models (POJO) - из OpenAPI Generator
- Controllers - из OpenAPI Generator
- **JPA Entities** - из OpenAPI схем через кастомные Mustache шаблоны
- **Repositories (Spring Data JPA)** - из OpenAPI схем через кастомные Mustache шаблоны
- **Services** - из OpenAPI схем через кастомные Mustache шаблоны
- **Database Migrations (Flyway)** - из OpenAPI схем через кастомные Mustache шаблоны

## Архитектура решения

### Вариант A: Кастомные Mustache шаблоны (РЕКОМЕНДУЕТСЯ)

**Преимущества:**
- ✅ Интеграция с OpenAPI Generator нативно
- ✅ Использование существующей инфраструктуры генератора
- ✅ Единый процесс генерации всего кода
- ✅ Легче поддерживать и обновлять
- ✅ Использование всех возможностей OpenAPI Generator (переменные, хелперы)

**Инструмент:** `openapi-generator-maven-plugin` с кастомными Mustache шаблонами

### Этап 1: Генерация базового кода через OpenAPI Generator
**Инструмент:** `openapi-generator-maven-plugin`

**Генерирует:**
- DTOs/Models в `com.necpgame.backjava.model`
- Controllers (интерфейсы) в `com.necpgame.backjava.api`
- Swagger документация

**Конфигурация:**
- Использует стандартные шаблоны OpenAPI Generator
- Настройки в `pom.xml` секция `openapi-generator-maven-plugin`

### Этап 2: Генерация JPA Entities из OpenAPI схем
**Инструмент:** OpenAPI Generator с кастомным Mustache шаблоном `Entity.mustache`

**Процесс:**
1. OpenAPI Generator парсит спецификацию
2. Для каждой схемы в `components/schemas` применяется шаблон `Entity.mustache`
3. Маппинг типов OpenAPI → JPA:
   - `string` → `String` (или `@Column` с ограничениями)
   - `string format: uuid` → `UUID`
   - `string format: email` → `String` с `@Email` валидацией
   - `integer` → `Integer` или `Long`
   - `boolean` → `Boolean`
   - `date` → `LocalDate`
   - `date-time` → `LocalDateTime`
4. Генерация JPA аннотаций:
   - `@Entity`
   - `@Table`
   - `@Id` (автоматически для UUID полей)
   - `@Column` с ограничениями из OpenAPI
   - `@NotNull`, `@Size`, `@Email`, `@Pattern` из валидаций

**Результат:** Entity классы в `com.necpgame.backjava.entity`

**Шаблон:** `templates/Entity.mustache`

### Этап 3: Генерация Repositories из OpenAPI схем
**Инструмент:** OpenAPI Generator с кастомным Mustache шаблоном `Repository.mustache`

**Процесс:**
1. OpenAPI Generator для каждой схемы применяет шаблон `Repository.mustache`
2. Создание интерфейсов `Repository<Entity, ID>` для каждой Entity
3. Добавление кастомных методов на основе схемы:
   - `findByEmail(String email)` для полей с `format: email`
   - `findByUsername(String username)` для полей username
   - `findByXxx()` для уникальных полей

**Результат:** Repository интерфейсы в `com.necpgame.backjava.repository`

**Шаблон:** `templates/Repository.mustache`

### Этап 4: Генерация Services из OpenAPI контроллеров
**Инструмент:** PowerShell скрипт `generate-services.ps1`

**Процесс:**
1. Скрипт парсит сгенерированные контроллеры из `target/generated-sources/openapi/`
2. Для каждого контроллера создается Service интерфейс и ServiceImpl
3. Генерация Service методов:
   - Методы из контроллеров (без аннотаций Spring Web)
   - Использование DTOs для параметров
   - Преобразование методов контроллеров в сигнатуры интерфейсов
4. Генерация Service реализаций:
   - Добавление `@Service` и `@Transactional`
   - Добавление `@Override` перед методами
   - Сохранение структуры методов контроллеров

**Результат:** 
- ✅ Service интерфейсы в `target/generated-sources/services/src/main/java/com/necpgame/backjava/service`
- ✅ Service реализации в `target/generated-sources/services/src/main/java/com/necpgame/backjava/service/impl`

**Скрипт:** 
- `scripts/generate-services.ps1`

### Этап 5: Генерация Database Migrations (Flyway)
**Инструмент:** OpenAPI Generator с кастомным Mustache шаблоном `Migration.mustache`

**Процесс:**
1. OpenAPI Generator для каждой схемы применяет шаблон `Migration.mustache`
2. Генерация SQL DDL на основе схемы:
   - Анализ типов полей
   - Генерация CREATE TABLE
   - Добавление ограничений (NOT NULL, UNIQUE, и т.д.)
3. Создание Flyway миграций:
   - Формат: `V{version}__{description}.sql`
   - Версионирование автоматическое
   - SQL генерируется из схемы OpenAPI

**Результат:** Flyway миграции в `src/main/resources/db/migration`

**Шаблон:** `templates/Migration.mustache`

## Технологический стек

### Основные инструменты:
1. **OpenAPI Generator** - генерация всего кода из OpenAPI
2. **Mustache** - шаблонизация для генерации кода
3. **MapStruct** - маппинг между DTOs и Entities
4. **Lombok** - уменьшение boilerplate кода
5. **Flyway** - управление миграциями БД

### Maven плагины:
1. `openapi-generator-maven-plugin` - генерация из OpenAPI с кастомными шаблонами
2. `maven-compiler-plugin` - компиляция с аннотациями (MapStruct, Lombok)
3. `flyway-maven-plugin` - применение миграций

## Структура кастомных шаблонов

### Директория шаблонов:
```
BACK-GO/templates/
├── Entity.mustache                # Шаблон для JPA Entities
├── Repository.mustache            # Шаблон для Spring Data JPA Repositories
├── Service.mustache                # Шаблон для Service интерфейсов
├── ServiceImpl.mustache           # Шаблон для Service реализаций
└── Migration.mustache             # Шаблон для Flyway миграций
```

### Переменные в шаблонах:
OpenAPI Generator предоставляет следующие переменные для шаблонов:
- `{{classname}}` - имя класса
- `{{modelPackage}}` - пакет для моделей
- `{{entityPackage}}` - пакет для entities
- `{{vars}}` - список переменных/полей
- `{{requiredVars}}` - список обязательных полей
- `{{operations}}` - список операций
- И многие другие (см. документацию OpenAPI Generator)

### Пакеты для сгенерированного кода:
```
com.necpgame.backjava
├── api/                          # Controllers (из OpenAPI Generator)
├── model/                        # DTOs/Models (из OpenAPI Generator)
├── entity/                       # JPA Entities (кастомная генерация)
├── repository/                   # Repositories (кастомная генерация)
├── service/                       # Service интерфейсы (кастомная генерация)
│   └── impl/                     # Service реализации (кастомная генерация)
└── mapper/                       # MapStruct мапперы (автоматическая генерация)
```

## Порядок выполнения

### Шаг 1: Настройка OpenAPI Generator
- ✅ Уже настроен в `pom.xml`
- ✅ Генерирует DTOs, Models, Controllers

### Шаг 2: Добавление зависимостей
- ✅ Добавить `swagger-parser-v3` для парсинга OpenAPI
- ✅ Добавить `javapoet` для генерации кода
- ✅ Добавить `hibernate-jpamodelgen` для мета-модели (опционально)

### Шаг 3: Реализация генераторов
1. **EntityGenerator** - парсинг OpenAPI схем и генерация Entities
2. **RepositoryGenerator** - генерация Repositories из Entities
3. **ServiceGenerator** - генерация Services из Controllers
4. **MigrationGenerator** - генерация Flyway миграций из Entities

### Шаг 4: Настройка Maven для автоматической генерации
- Добавить `exec-maven-plugin` для запуска генераторов
- Настроить порядок выполнения:
  1. OpenAPI Generator → DTOs/Models/Controllers
  2. EntityGenerator → Entities
  3. RepositoryGenerator → Repositories
  4. ServiceGenerator → Services
  5. MigrationGenerator → Migrations
  6. MapStruct → Mappers

### Шаг 5: Тестирование
- Запустить `mvn clean generate-sources`
- Проверить сгенерированные классы
- Проверить миграции БД
- Запустить приложение и проверить работу

## Ограничения и особенности

### 1. Маппинг типов OpenAPI → JPA
- Не все типы OpenAPI имеют прямые аналоги в JPA
- Нужна таблица маппинга типов
- Специальная обработка для UUID, Email, Date, DateTime

### 2. Связи между сущностями
- OpenAPI не описывает связи напрямую
- Нужно анализировать ссылки через `$ref`
- Генерация `@OneToMany`, `@ManyToOne`, `@ManyToMany` требует дополнительной логики

### 3. Валидации
- Перенос валидаций из OpenAPI в JPA аннотации
- `minLength`, `maxLength` → `@Size`
- `pattern` → `@Pattern`
- `format: email` → `@Email`
- `required` → `@NotNull`

### 4. Имена и конвенции
- Конвертация snake_case OpenAPI → camelCase Java
- Имена таблиц: plural от entity name
- Имена колонок: snake_case из OpenAPI

## Примеры генерации

### Пример 1: Entity из OpenAPI схемы
**OpenAPI:**
```yaml
Account:
  type: object
  required: [email, username]
  properties:
    email:
      type: string
      format: email
    username:
      type: string
      minLength: 3
      maxLength: 20
```

**Сгенерированный Entity:**
```java
@Entity
@Table(name = "accounts")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(nullable = false, unique = true)
    @Email
    private String email;
    
    @Column(nullable = false, unique = true, length = 20)
    @Size(min = 3, max = 20)
    private String username;
}
```

### Пример 2: Repository из Entity
**Сгенерированный Repository:**
```java
@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {
    Optional<Account> findByEmail(String email);
    Optional<Account> findByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
}
```

### Пример 3: Service из Controller
**OpenAPI Controller метод:**
```java
@PostMapping("/auth/register")
ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest request);
```

**Сгенерированный Service:**
```java
public interface AuthService {
    RegisterResponse register(RegisterRequest request);
}

@Service
public class AuthServiceImpl implements AuthService {
    @Autowired
    private AccountRepository accountRepository;
    
    @Autowired
    private AccountMapper accountMapper;
    
    public RegisterResponse register(RegisterRequest request) {
        // Валидация, бизнес-логика, сохранение
    }
}
```

### Пример 4: Flyway миграция из Entity
**Сгенерированная миграция:**
```sql
-- V1__create_accounts_table.sql
CREATE TABLE accounts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) NOT NULL UNIQUE,
    username VARCHAR(20) NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

## Статус выполнения

### ✅ Выполнено:

1. ✅ **Настроен OpenAPI Generator** - генерирует DTOs/Models/Controllers
2. ✅ **Реализована генерация Entities** - кастомный шаблон `Entity.mustache` через `modelTemplateFiles`
3. ✅ **Реализована генерация Repositories** - скрипт `scripts/generate-repositories.ps1` через `exec-maven-plugin`
4. ✅ **Настроен Maven** - автоматическая генерация при `mvn clean generate-sources`
5. ✅ **Протестировано** - генерация работает из одного OpenAPI файла
6. ✅ **Документировано** - процесс описан в документации

### 🔄 В разработке:

1. 🔄 **Генерация Services** - решения найдены (см. [OPENAPI-GENERATION-SOLUTIONS.md](./OPENAPI-GENERATION-SOLUTIONS.md))
   - Использовать `supportingFiles` в OpenAPI Generator
   - Кастомные шаблоны `Service.mustache` и `ServiceImpl.mustache`
2. ✅ **Генерация Controller реализаций** - РЕШЕНИЕ ПРОСТОЕ!
   - Просто изменить `<interfaceOnly>false</interfaceOnly>` в `pom.xml`
   - OpenAPI Generator автоматически генерирует полные реализации контроллеров напрямую из OpenAPI!
3. 🔄 **Генерация Migrations** - решения найдены
   - Парсинг JPA Entities и генерация SQL миграций
   - Или использование Hibernate SchemaExport (требует доработки для идемпотентности)
4. 🔄 **Улучшение шаблона Entity** - переменные `{{#vars}}` не всегда заполняются правильно

### 📝 Следующие шаги:

1. ✅ Изменить `interfaceOnly` на `false` в `pom.xml` для генерации Controller реализаций (приоритет: высокий, ПРОСТОЕ РЕШЕНИЕ!)
2. Исправить генерацию Entities (разобраться с переменными `{{#vars}}`)
3. Реализовать генерацию Services через `supportingFiles` (приоритет: средний)
4. Создать скрипт для генерации Flyway миграций из JPA Entities (приоритет: средний)
5. Оптимизировать большие модели (например, Character.java - 489 строк)

**Подробнее:** См. [OPENAPI-GENERATION-SOLUTIONS.md](./OPENAPI-GENERATION-SOLUTIONS.md) - найденные решения и подходы

