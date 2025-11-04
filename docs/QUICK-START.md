# Быстрый старт BACK-JAVA

## Установка зависимостей

```bash
cd BACK-GO
mvn clean install
```

## Запуск PostgreSQL

```bash
# Windows PowerShell
docker-compose up -d
docker-compose ps

# Linux/Mac
docker-compose up -d
docker-compose ps
```

## Генерация кода из OpenAPI

```bash
# Генерация всего кода одной командой
mvn clean generate-sources

# Или из конкретного OpenAPI файла
mvn clean generate-sources -Dopenapi.spec=../API-SWAGGER/api/v1/auth/character-creation.yaml
```

**Что генерируется:**
- ✅ DTOs/Models/Controllers (стандартные OpenAPI Generator шаблоны)
- ✅ JPA Entities (кастомный шаблон `Entity.mustache`)
- ✅ Repositories (скрипт `generate-repositories.ps1`)

## Запуск сервера

```bash
# Через Maven
mvn spring-boot:run

# Или через JAR
java -jar target/back-java-1.0.0.jar
```

## Проверка работы

Откройте в браузере: `http://localhost:8080/health`

Или через curl:
```bash
# Windows PowerShell
curl http://localhost:8080/health

# Linux/Mac
curl http://localhost:8080/health
```

Должен вернуться JSON:
```json
{
  "status": "ok",
  "message": "NECPGAME backend is running",
  "version": "1.0.0"
}
```

## Доступные endpoints

- `GET /health` - Health check основного сервера
- `GET /api/v1/health` - API v1 health check
- `GET /swagger-ui.html` - Swagger UI документация
- `GET /api-docs` - OpenAPI спецификация

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
