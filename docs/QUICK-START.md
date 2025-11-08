# Быстрый старт BACK-GO

## 🎯 Workflow: Контракты + Реализация

### Что генерируется автоматически:
- ✅ DTOs (модели данных)
- ✅ API Interfaces (REST контракты)
- ✅ Service Interfaces (контракты бизнес-логики)

### Что создаётся вручную:
- ✍️ Entities (JPA сущности)
- ✍️ Repositories (Spring Data)
- ✍️ Controllers (REST контроллеры)
- ✍️ ServiceImpl (бизнес-логика)
- ✍️ Mappers (MapStruct)
- ✍️ Liquibase миграции (XML/YAML)

---

## Установка зависимостей

```bash
cd BACK-GO
mvn clean install
```

## Запуск PostgreSQL

```bash
docker-compose up -d
docker-compose ps
```

## Генерация контрактов из OpenAPI

```powershell
# Проверка перед генерацией
.\scripts\validate-openapi.ps1 -ApiDirectory ../API-SWAGGER/api/v1/

# Генерация контрактов в микросервисы
.\scripts\generate-openapi-microservices.ps1 -ApiDirectory ../API-SWAGGER/api/v1/

# Генерация из одного файла
.\scripts\generate-openapi-microservices.ps1 -ApiSpec ../API-SWAGGER/api/v1/auth/character-creation.yaml
```
> ⚠️ Обязательное требование: каждая спецификация должна содержать `x-microservice` с точным именем микросервиса. Без этого поле скрипт не выполнит маршрутизацию контрактов.

**Результат (для микросервисов):**
- ✅ DTOs в `microservices/<service>/src/main/java/com/necpgame/<service>/model/`
- ✅ API Interfaces в `microservices/<service>/src/main/java/com/necpgame/<service>/api/`
- ✅ Service Interfaces в `microservices/<service>/src/main/java/com/necpgame/<service>/service/`

## Создание реализации вручную

Используй шаблоны из [MANUAL-TEMPLATES.md](./docs/MANUAL-TEMPLATES.md):

1. **Entity** - `microservices/<service>/src/main/java/com/necpgame/<service>/entity/AccountEntity.java`
2. **Repository** - `microservices/<service>/src/main/java/com/necpgame/<service>/repository/AccountRepository.java`
3. **Controller** - `microservices/<service>/src/main/java/com/necpgame/<service>/controller/AuthController.java`
4. **ServiceImpl** - `microservices/<service>/src/main/java/com/necpgame/<service>/service/impl/AuthServiceImpl.java`
5. **Mapper (MapStruct)** - `microservices/<service>/src/main/java/com/necpgame/<service>/mapper/AccountMapperMS.java`
6. **Liquibase миграция** - `microservices/<service>/src/main/resources/db/changelog/changes/001-create-accounts-table.xml`

## Запуск микросервисов

```bash
# Запуск конкретного микросервиса (пример: auth-service)
mvn -pl microservices/auth-service -am spring-boot:run

# Запуск API Gateway после старта микросервисов
mvn -pl infrastructure/api-gateway -am spring-boot:run
```

## Компиляция проекта

```bash
# Компиляция (включая сгенерированные контракты)
mvn clean compile

# Запуск тестов
mvn test
```

## Проверка работы

```bash
# Пример: health-check auth-service
curl http://localhost:8081/api/v1/health

# Через API Gateway (после запуска gateway)
curl http://localhost:8080/api/v1/health
```

## Доступные endpoints

После создания реализации:
- `POST /api/v1/auth/register` (auth-service, порт 8081)
- `POST /api/v1/auth/login` (auth-service, порт 8081)
- `GET /api/v1/characters` (character-service, порт 8082)
- `GET /swagger-ui.html` (через API Gateway на 8080)

## Остановка сервера

Нажмите `Ctrl+C` для graceful shutdown

## Остановка PostgreSQL

```bash
docker-compose down
```

## Troubleshooting

### Проблема: Порт 8080 уже занят

**Windows PowerShell:**
```powershell
# Найти процессы на порту 8080
Get-NetTCPConnection -LocalPort 8080 | Select-Object OwningProcess

# Остановить процесс (замените PID на реальный)
taskkill /F /PID <PID>
```

**Linux/Mac:**
```bash
# Найти процессы на порту 8080
lsof -i :8080

# Остановить процесс (замените PID на реальный)
kill -9 <PID>
```

### Проблема: БД не подключена

```bash
# Проверить, что PostgreSQL запущен
docker-compose ps

# Проверить логи
docker-compose logs postgres
```
