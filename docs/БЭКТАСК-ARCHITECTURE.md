# БЭКТАСК-ARCHITECTURE.md

**Архитектура, структура директорий и соответствие API-SWAGGER**

📖 **Навигация:** [БЭКТАСК.MD](./БЭКТАСК.MD) | [БЭКТАСК-PROCESS.md](./БЭКТАСК-PROCESS.md) | [БЭКТАСК-REQUIREMENTS.md](./БЭКТАСК-REQUIREMENTS.md) | [БЭКТАСК-FAQ.md](./БЭКТАСК-FAQ.md)

---

## Архитектура и иерархия директорий

**ВАЖНО:** Агент ОБЯЗАН следовать установленной архитектуре и иерархии директорий для лучшего контроля действий и получения лучшего результата.

---

## Основные принципы архитектуры

1. **Соответствие API-SWAGGER:** Структура бекенд кода должна соответствовать структуре API-SWAGGER
2. **Иерархическая структура:** От общего к частному, соответствие структуре API-SWAGGER
3. **Именование:** Файлы - `PascalCase.java` для Java файлов, директории - `kebab-case` или `snake_case`
4. **Ограничение размера:** МАКСИМУМ 400 строк на файл, если больше - разбить на несколько файлов
5. **Разделение ответственности:** Разделение на Controllers, Services, Repositories, Entities
6. **Документация:** Каждая директория должна иметь README.md с обзором (если нужно)

---

## Структура директорий бекенд кода

```
BACK-JAVA/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/necpgame/backjava/
│   │   │       ├── NecpgameBackendApplication.java
│   │   │       ├── controllers/                    # HTTP Controllers для обработки запросов
│   │   │       │   ├── common/                     # Общие Controllers
│   │   │       │   │   ├── HealthController.java
│   │   │       │   │   └── ErrorController.java
│   │   │       │   ├── gameplay/                   # Соответствует API-SWAGGER/api/v1/gameplay/
│   │   │       │   │   ├── social/                 # Соответствует API-SWAGGER/api/v1/gameplay/social/
│   │   │       │   │   │   └── PersonalNpcController.java
│   │   │       │   │   ├── economy/                 # Соответствует API-SWAGGER/api/v1/gameplay/economy/
│   │   │       │   │   │   └── EquipmentMatrixController.java
│   │   │       │   │   ├── combat/                 # Соответствует API-SWAGGER/api/v1/gameplay/combat/
│   │   │       │   │   └── progression/            # Соответствует API-SWAGGER/api/v1/gameplay/progression/
│   │   │       │   ├── lore/                       # Соответствует API-SWAGGER/api/v1/lore/
│   │   │       │   │   ├── FactionsController.java
│   │   │       │   │   ├── LocationsController.java
│   │   │       │   │   └── CharactersController.java
│   │   │       │   └── narrative/                  # Соответствует API-SWAGGER/api/v1/narrative/
│   │   │       │       ├── QuestsController.java
│   │   │       │       └── DialoguesController.java
│   │   │       ├── services/                       # Сервисы для бизнес-логики
│   │   │       │   ├── common/                     # Общие сервисы
│   │   │       │   │   └── AuthService.java
│   │   │       │   ├── gameplay/                   # Соответствует API-SWAGGER/api/v1/gameplay/
│   │   │       │   │   ├── social/
│   │   │       │   │   │   └── PersonalNpcService.java
│   │   │       │   │   └── ...
│   │   │       │   └── ...
│   │   │       ├── repositories/                   # Репозитории для работы с БД
│   │   │       │   ├── common/                     # Общие репозитории
│   │   │       │   │   └── BaseRepository.java
│   │   │       │   ├── gameplay/                   # Соответствует API-SWAGGER/api/v1/gameplay/
│   │   │       │   │   ├── social/
│   │   │       │   │   │   └── PersonalNpcRepository.java
│   │   │       │   │   └── ...
│   │   │       │   └── ...
│   │   │       ├── entities/                       # JPA Entities
│   │   │       │   ├── common/
│   │   │       │   │   └── AccountEntity.java
│   │   │       │   ├── gameplay/
│   │   │       │   │   ├── social/
│   │   │       │   │   │   └── PersonalNpcEntity.java
│   │   │       │   │   └── ...
│   │   │       │   └── ...
│   │   │       ├── config/                         # Конфигурация приложения
│   │   │       │   ├── SecurityConfig.java
│   │   │       │   ├── WebConfig.java
│   │   │       │   └── DatabaseConfig.java
│   │   │       └── dto/                            # DTOs (если не сгенерированы)
│   │   │           └── common/
│   │   └── resources/
│   │       ├── application.yml
│   │       └── db/
│   │           └── migration/                     # Flyway миграции БД
│   │               ├── V1__create_accounts_table.sql
│   │               ├── V2__create_characters_table.sql
│   │               └── ...
│   └── test/
│       └── java/
│           └── com/necpgame/backjava/
│               └── ...                             # Тесты
├── target/
│   └── generated-sources/
│       ├── openapi/                                 # Сгенерированные DTOs/Models/Controllers из API-SWAGGER
│       │   └── src/main/java/com/necpgame/backjava/
│       │       ├── api/                             # Сгенерированные Controller интерфейсы
│       │       │   └── PersonalNpcApi.java
│       │       └── model/                           # Сгенерированные Models/DTOs
│       │           └── PersonalNpc.java
│       ├── entities/                                # Сгенерированные JPA Entities (кастомный шаблон)
│       │   └── src/main/java/com/necpgame/backjava/entity/
│       │       └── Account.java
│       └── repositories/                            # Сгенерированные Repositories (скрипт)
│           └── src/main/java/com/necpgame/backjava/repository/
│               └── AccountRepository.java
└── pom.xml
```

---

## Соответствие API-SWAGGER

**ВАЖНО:** Структура бекенд кода должна строго соответствовать структуре API-SWAGGER:

- `API-SWAGGER/api/v1/gameplay/social/personal-npc-tool/` → `BACK-JAVA/src/main/java/com/necpgame/backjava/controllers/gameplay/social/PersonalNpcController.java`
- `API-SWAGGER/api/v1/gameplay/economy/equipment-matrix/` → `BACK-JAVA/src/main/java/com/necpgame/backjava/controllers/gameplay/economy/EquipmentMatrixController.java`
- `API-SWAGGER/api/v1/gameplay/combat/` → `BACK-JAVA/src/main/java/com/necpgame/backjava/controllers/gameplay/combat/`
- `API-SWAGGER/api/v1/lore/` → `BACK-JAVA/src/main/java/com/necpgame/backjava/controllers/lore/`
- `API-SWAGGER/api/v1/narrative/` → `BACK-JAVA/src/main/java/com/necpgame/backjava/controllers/narrative/`

**Принципы соответствия:**
- Иерархия директорий должна повторять иерархию API-SWAGGER
- Имена директорий должны совпадать (kebab-case или snake_case)
- Имена файлов Controllers должны отражать суть API (PascalCase.java)
- Сгенерированные файлы должны быть в `target/generated-sources/openapi/`

---

## Структура файлов проекта

```
BACK-JAVA/
├── БЭКТАСК.MD                           # Главный файл документации
├── БЭКТАСК-PROCESS.md                   # Процесс работы агента
├── БЭКТАСК-REQUIREMENTS.md              # Требования и критерии
├── БЭКТАСК-FAQ.md                       # FAQ и примеры
├── БЭКТАСК-ARCHITECTURE.md              # Этот файл
├── .cursor/
│   └── rules/
│       └── back-java-rules.mdc          # Правила работы с бекендом
├── src/
│   ├── main/
│   │   ├── java/com/necpgame/backjava/
│   │   │   ├── NecpgameBackendApplication.java
│   │   │   ├── controllers/            # Контроллеры
│   │   │   │   └── {path}/              # Соответствует API-SWAGGER/api/v1/{path}/
│   │   │   │       └── {ControllerName}.java  # Controllers (до 400 строк)
│   │   │   ├── services/                # Сервисы
│   │   │   │   └── {path}/              # Соответствует API-SWAGGER/api/v1/{path}/
│   │   │   │       └── {ServiceName}.java  # Services (до 400 строк)
│   │   │   ├── repositories/            # Репозитории
│   │   │   │   └── {path}/              # Соответствует API-SWAGGER/api/v1/{path}/
│   │   │   │       └── {RepositoryName}.java  # Repositories (до 400 строк)
│   │   │   ├── entities/                # JPA Entities
│   │   │   │   └── {path}/              # Соответствует API-SWAGGER/api/v1/{path}/
│   │   │   │       └── {EntityName}.java  # Entities (до 400 строк)
│   │   │   └── config/                  # Конфигурация
│   │   └── resources/
│   │       ├── application.yml
│   │       └── db/migration/           # Flyway миграции
│   └── test/
│       └── java/com/necpgame/backjava/
│           └── ...                      # Тесты
├── target/
│   └── generated-sources/
│       └── openapi/                     # Сгенерированный код
│           └── src/main/java/com/necpgame/backjava/
│               ├── api/                  # Controller интерфейсы
│               └── model/                # Models/DTOs
├── scripts/
│   ├── autocommit.ps1
│   └── autocommit.sh
└── pom.xml                               # Maven конфигурация
```

---

## Структура директории с Controllers

Когда Controller разбит на несколько файлов (для соблюдения ограничения 400 строк), структура должна быть следующей:

```
BACK-JAVA/src/main/java/com/necpgame/backjava/controllers/gameplay/social/
├── README.md                            # ОБЯЗАТЕЛЬНО: описание структуры Controllers
├── PersonalNpcController.java            # Главный Controller (до 400 строк)
├── PersonalNpcControllerGet.java        # GET методы (до 400 строк)
├── PersonalNpcControllerPost.java       # POST методы (до 400 строк)
├── PersonalNpcControllerPut.java        # PUT методы (до 400 строк)
└── PersonalNpcControllerDelete.java     # DELETE методы (до 400 строк)
```

### README.md в директории:

```markdown
# Personal NPC Controller

Controllers для работы с Personal NPC Tool API.

## Controllers

- `PersonalNpcController.java` - Основной Controller
- `PersonalNpcControllerGet.java` - GET методы
- `PersonalNpcControllerPost.java` - POST методы
- `PersonalNpcControllerPut.java` - PUT методы
- `PersonalNpcControllerDelete.java` - DELETE методы

## Services

- `PersonalNpcService.java` - Сервис для бизнес-логики Personal NPC

## Repositories

- `PersonalNpcRepository.java` - Spring Data JPA Repository для работы с БД Personal NPC

## Entities

- `PersonalNpcEntity.java` - JPA Entity для Personal NPC

## API

- Сгенерированный код: `target/generated-sources/openapi/src/main/java/com/necpgame/backjava/api/PersonalNpcApi.java`
- Источник: `API-SWAGGER/api/v1/gameplay/social/personal-npc-tool/`
```

---

## Важные моменты

### Соответствие архитектуре:

- Структура бекенд кода должна строго соответствовать структуре API-SWAGGER
- Каждая директория должна отражать путь в API-SWAGGER
- Имена файлов должны быть понятными и отражать назначение
- Файлы Controllers/Services/Repositories/Entities не должны превышать 400 строк
- Именование: файлы - `PascalCase.java`, директории - `kebab-case` или `snake_case`

### Использование сгенерированных файлов:

- **ОБЯЗАТЕЛЬНО** использовать OpenAPI Generator для генерации Java Spring Boot кода
- Сгенерированные файлы должны быть в `target/generated-sources/openapi/`
- Не редактировать сгенерированный код вручную
- Использовать типы из сгенерированных файлов

### Разделение ответственности:

- **Controllers** - только обработка HTTP запросов и валидация (реализация сгенерированных интерфейсов)
- **Services** - бизнес-логика приложения
- **Repositories** - работа с базой данных (Spring Data JPA интерфейсы)
- **Entities** - JPA Entities для работы с БД
- **DTOs** - модели данных (использовать сгенерированные из OpenAPI)

### Ограничение размера файлов:

- **ОБЯЗАТЕЛЬНО:** Каждый файл не должен превышать 400 строк
- Если файл больше 400 строк - разбить на несколько файлов:
  - Разделить Controllers по методам (GET, POST, PUT, DELETE)
  - Вынести бизнес-логику в сервисы
  - Вынести работу с БД в репозитории
  - Разделить на подфайлы по функциональности

---

## Примеры соответствия

### Пример 1: Personal NPC Tool

**API-SWAGGER:**
```
API-SWAGGER/api/v1/gameplay/social/personal-npc-tool/
├── personal-npc-tool.yaml
├── personal-npc-tool-endpoints.yaml
└── personal-npc-tool-models.yaml
```

**BACK-JAVA:**
```
BACK-JAVA/
├── target/generated-sources/openapi/
│   └── src/main/java/com/necpgame/backjava/
│       ├── api/PersonalNpcApi.java        # Сгенерированный интерфейс
│       └── model/PersonalNpc.java         # Сгенерированная модель
├── src/main/java/com/necpgame/backjava/
│   ├── controllers/gameplay/social/
│   │   └── PersonalNpcController.java     # Реализация интерфейса
│   ├── services/gameplay/social/
│   │   └── PersonalNpcService.java
│   ├── repositories/gameplay/social/
│   │   └── PersonalNpcRepository.java    # Spring Data JPA интерфейс
│   └── entities/gameplay/social/
│       └── PersonalNpcEntity.java        # JPA Entity
└── src/main/resources/db/migration/
    └── V6__create_personal_npc_table.sql
```

### Пример 2: Equipment Matrix

**API-SWAGGER:**
```
API-SWAGGER/api/v1/gameplay/economy/equipment-matrix/
├── equipment-matrix.yaml
├── equipment-matrix-items.yaml
└── equipment-matrix-models.yaml
```

**BACK-JAVA:**
```
BACK-JAVA/
├── target/generated-sources/openapi/
│   └── src/main/java/com/necpgame/backjava/
│       ├── api/EquipmentMatrixApi.java
│       └── model/EquipmentMatrix.java
├── src/main/java/com/necpgame/backjava/
│   ├── controllers/gameplay/economy/
│   │   └── EquipmentMatrixController.java
│   ├── services/gameplay/economy/
│   │   └── EquipmentMatrixService.java
│   ├── repositories/gameplay/economy/
│   │   └── EquipmentMatrixRepository.java
│   └── entities/gameplay/economy/
│       └── EquipmentMatrixEntity.java
└── src/main/resources/db/migration/
    └── V7__create_equipment_matrix_table.sql
```

---

📖 **Навигация:** [БЭКТАСК.MD](./БЭКТАСК.MD) | [БЭКТАСК-PROCESS.md](./БЭКТАСК-PROCESS.md) | [БЭКТАСК-REQUIREMENTS.md](./БЭКТАСК-REQUIREMENTS.md) | [БЭКТАСК-FAQ.md](./БЭКТАСК-FAQ.md)
