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

**Результат (для микросервисов):**
- ✅ DTOs в `microservices/<service>/src/main/java/com/necpgame/<service>/model/`
- ✅ API Interfaces в `microservices/<service>/src/main/java/com/necpgame/<service>/api/`
- ✅ Service Interfaces в `microservices/<service>/src/main/java/com/necpgame/<service>/service/`

## Создание реализации вручную

Используй шаблоны из [MANUAL-TEMPLATES.md](./docs/MANUAL-TEMPLATES.md):

1. **Entity** - `src/main/java/entity/AccountEntity.java`
2. **Repository** - `src/main/java/repository/AccountRepository.java`
3. **Controller** - `src/main/java/controller/AuthController.java`
4. **ServiceImpl** - `src/main/java/service/impl/AuthServiceImpl.java`
5. **Mapper (MapStruct)** - `src/main/java/mapper/AccountMapperMS.java`
6. **Liquibase миграция** - `src/main/resources/db/changelog/changes/001-create-accounts-table.xml`

## Запуск сервера

```bash
# Через Maven
mvn spring-boot:run

# Или через JAR
java -jar target/back-java-1.0.0.jar
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
# Запуск сервера
mvn spring-boot:run

# Проверка health endpoint (в другом терминале)
curl http://localhost:8080/api/v1/health
```

## Доступные endpoints

После создания реализации:
- `POST /api/v1/auth/register` - Регистрация
- `POST /api/v1/auth/login` - Авторизация
- `GET /api/v1/characters` - Список персонажей
- `GET /swagger-ui.html` - Swagger UI документация

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
