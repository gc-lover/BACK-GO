# 🐳 Docker Deployment Guide

Полное руководство по развёртыванию NECPGAME Backend в Docker.

> Production API endpoints: `https://api.necp.game/v1` (HTTP) и `wss://api.necp.game/v1` (WebSocket). Локальная разработка использует API Gateway на `http://localhost:8080`.

## 📋 Оглавление

1. [Быстрый старт](#быстрый-старт)
2. [Архитектура Docker](#архитектура-docker)
3. [Сборка образа](#сборка-образа)
4. [Запуск через Docker Compose](#запуск-через-docker-compose)
5. [Ручной запуск контейнеров](#ручной-запуск-контейнеров)
6. [Конфигурация](#конфигурация)
7. [Мониторинг и логи](#мониторинг-и-логи)
8. [Troubleshooting](#troubleshooting)

## 🚀 Быстрый старт

### Запуск всего стека (PostgreSQL + микросервисы + шлюз):

```bash
# Windows PowerShell
cd BACK-GO
docker-compose up -d

# Linux/Mac
cd BACK-GO
docker-compose up -d
```
> Результат: поднимаются `postgres`, `config-server`, `service-discovery`, `api-gateway`, а также все микросервисы (`auth-service`, `character-service`, `social-service`, `economy-service`, `world-service`).

### Запуск только PostgreSQL:

```bash
docker-compose up -d postgres
```

### Остановка всех сервисов:

```bash
docker-compose down
```

### Пересборка образов:

```bash
docker-compose up -d --build
```

## 🏗️ Архитектура Docker

### Multi-Stage Build

Каждый микросервис собирается собственным multi-stage образом (Dockerfile находится в каталоге микросервиса):

#### Stage 1: Builder (сборка микросервиса)
- **Базовый образ**: `eclipse-temurin:21-jdk-alpine`
- **Что делаем**:
  - Копируем `pom.xml` и `src` конкретного микросервиса
  - Загружаем зависимости Maven
  - Генерируем OpenAPI контракты (опционально)
  - Собираем JAR текущего микросервиса (`<service>-1.0.0.jar`)

#### Stage 2: Runtime (запуск микросервиса)
- **Базовый образ**: `eclipse-temurin:21-jre-alpine`
- **Содержимое**: только JRE 21 и JAR соответствующего микросервиса
- **Запуск**: `java -jar app.jar` с профилем `docker`

### Преимущества подхода:
- ✅ **Унификация** — каждая команда использует одинаковый шаблон Dockerfile
- ✅ **Маленький размер образа** — финальный слой содержит только нужный микросервис
- ✅ **Безопасность** — в runtime нет инструментов сборки
- ✅ **Быстрый деплой** — изменился один сервис → перерабатывается только его образ

## 🔨 Сборка образа

### Автоматическая сборка через скрипт:

```powershell
# Windows: укажи конкретный микросервис
.\scripts\docker-build.ps1 -Service auth-service
.\scripts\docker-build.ps1 -Service api-gateway -Tag "v1.0.0"
.\scripts\docker-build.ps1 -Service social-service -NoCache
```

```bash
# Linux/Mac
./scripts/docker-build.sh auth-service
./scripts/docker-build.sh api-gateway v1.0.0
./scripts/docker-build.sh social-service latest --no-cache
```

### Ручная сборка:

```bash
# Базовая сборка auth-service
docker build -f microservices/auth-service/Dockerfile -t necpgame-auth-service:latest .

# Без кэша
docker build --no-cache -f microservices/auth-service/Dockerfile -t necpgame-auth-service:latest .

# С конкретным тегом
docker build -f microservices/auth-service/Dockerfile -t necpgame-auth-service:v1.0.0 .
```

## 🐳 Запуск через Docker Compose

### Файл `docker-compose.yml`

Определяет несколько сервисов:
1. **postgres** — PostgreSQL база данных
2. **config-server** — Spring Cloud Config Server
3. **service-discovery** — Eureka/Consul для регистрации микросервисов
4. **api-gateway** — входная точка `https://api.necp.game/v1`
5. **auth-service**, **character-service**, **social-service**, **economy-service**, **world-service** — бизнес-микросервисы

### Основные команды:

```bash
# Запуск всех сервисов (фоновый режим)
docker-compose up -d

# Запуск с просмотром логов
docker-compose up

# Остановка всех сервисов
docker-compose down

# Остановка с удалением volumes (ВНИМАНИЕ: удалятся данные БД!)
docker-compose down -v

# Перезапуск конкретного микросервиса
docker-compose restart auth-service

# Пересборка образов перед запуском
docker-compose up -d --build

# Просмотр статуса сервисов
docker-compose ps

# Просмотр логов
docker-compose logs -f auth-service
docker-compose logs -f api-gateway
docker-compose logs -f postgres

# Выполнение команды в контейнере
docker-compose exec auth-service sh
docker-compose exec postgres psql -U necpgame -d necpgame
```

### Проверка здоровья сервисов:

```bash
# Проверка health check
docker-compose ps

# API Gateway health endpoint
curl http://localhost:8080/actuator/health

# Auth-service health endpoint
curl http://localhost:8081/actuator/health

# PostgreSQL
docker-compose exec postgres pg_isready -U necpgame
```

## 🔧 Ручной запуск контейнеров

### Запуск PostgreSQL:

```bash
docker run -d \
  --name necpgame-postgres \
  -e POSTGRES_USER=necpgame \
  -e POSTGRES_PASSWORD=necpgame \
  -e POSTGRES_DB=necpgame \
  -p 5433:5432 \
  -v postgres_data:/var/lib/postgresql/data \
  postgres:15
```

### Запуск микросервиса (пример: auth-service):

```bash
docker run -d \
  --name necpgame-auth-service \
  -p 8081:8081 \
  -e SPRING_PROFILES_ACTIVE=docker \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/necpgame \
  -e SPRING_DATASOURCE_USERNAME=necpgame \
  -e SPRING_DATASOURCE_PASSWORD=necpgame \
  -e SERVER_PORT=8081 \
  --link necpgame-postgres:postgres \
  necpgame-auth-service:latest
```

## ⚙️ Конфигурация

### Environment Variables

Все конфигурационные параметры можно переопределить через переменные окружения в `docker-compose.yml`:

#### Database Configuration
```yaml
SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/necpgame
SPRING_DATASOURCE_USERNAME: necpgame
SPRING_DATASOURCE_PASSWORD: necpgame
```

#### JWT Configuration
```yaml
JWT_SECRET: dev-secret-key-change-in-production
JWT_EXPIRATION: 86400000  # 24 часа
```

#### CORS Configuration
```yaml
CORS_ALLOWED_ORIGINS: http://localhost:5173,http://localhost:3000
```

#### Logging
```yaml
LOGGING_LEVEL_ROOT: INFO
LOGGING_LEVEL_COM_NECPGAME: DEBUG
```

### Profiles

Используется Spring Profile `docker` - конфигурация в `application-docker.yml`:

```yaml
spring:
  profiles:
    active: docker
```

### Volumes

#### PostgreSQL Data:
```yaml
volumes:
  - postgres_data:/var/lib/postgresql/data
```

#### Auth-service Logs:
```yaml
volumes:
  - ./logs:/app/logs
```

## 📊 Мониторинг и логи

### Просмотр логов:

```bash
# Все логи
docker-compose logs -f

# Логи конкретного сервиса
docker-compose logs -f auth-service
docker-compose logs -f postgres

# Последние 100 строк
docker-compose logs --tail=100 auth-service

# Логи с временными метками
docker-compose logs -f -t auth-service
```

### Health Checks:

```bash
# API Gateway health endpoint
curl http://localhost:8080/actuator/health

# Auth-service health endpoint
curl http://localhost:8081/actuator/health

# Полная информация (требуется авторизация)
curl http://localhost:8080/actuator/health -u admin:admin123

# Metrics
curl http://localhost:8080/actuator/metrics

# Info
curl http://localhost:8080/actuator/info
```

### Мониторинг ресурсов:

```bash
# Использование ресурсов всеми контейнерами
docker stats

# Конкретный контейнер
docker stats necpgame-auth-service
```

### Доступ внутрь контейнера:

```bash
# Auth-service
docker-compose exec auth-service sh

# PostgreSQL
docker-compose exec postgres psql -U necpgame -d necpgame
```

## 🐛 Troubleshooting

### Контейнер не запускается

1. **Проверить логи:**
```bash
docker-compose logs auth-service
```

2. **Проверить health check:**
```bash
docker-compose ps
```

3. **Проверить доступность PostgreSQL:**
```bash
docker-compose exec postgres pg_isready -U necpgame
```

### База данных недоступна

1. **Проверить статус PostgreSQL:**
```bash
docker-compose ps postgres
```

2. **Проверить подключение:**
```bash
docker-compose exec postgres psql -U necpgame -d necpgame -c "SELECT 1;"
```

3. **Проверить network:**
```bash
docker network ls
docker network inspect necpgame_necpgame-network
```

### Ошибка при сборке образа

1. **Очистить Docker кэш:**
```bash
docker system prune -a
```

2. **Пересобрать без кэша:**
```bash
docker-compose build --no-cache auth-service
```

3. **Проверить доступность Maven Central:**
```bash
curl https://repo.maven.apache.org/maven2/
```

### Liquibase ошибки миграций

1. **Проверить состояние миграций:**
```bash
docker-compose exec postgres psql -U necpgame -d necpgame -c "SELECT * FROM databasechangelog ORDER BY dateexecuted DESC LIMIT 10;"
```

2. **Откатить последнюю миграцию (если нужно):**
```bash
# Подключиться к контейнеру микросервиса
docker-compose exec auth-service sh

# Выполнить rollback через Maven (если установлен)
# или вручную через SQL
```

3. **Пересоздать БД (ВНИМАНИЕ: потеря данных!):**
```bash
docker-compose down -v
docker-compose up -d
```

### Порты заняты

```bash
# Windows
netstat -ano | findstr :8080
netstat -ano | findstr :5433

# Linux/Mac
lsof -i :8080
lsof -i :5433

# Остановить процесс или изменить порт в docker-compose.yml
```

### Out of Memory

1. **Увеличить лимиты в docker-compose.yml:**
```yaml
services:
  auth-service:
    deploy:
      resources:
        limits:
          memory: 1G
        reservations:
          memory: 512M
```

2. **Настроить JVM параметры в Dockerfile:**
```dockerfile
ENTRYPOINT ["java", \
    "-XX:MaxRAMPercentage=75.0", \
    "-Xms256m", \
    "-Xmx512m", \
    "-jar", "app.jar"]
```

## 📚 Дополнительные ресурсы

- [Docker Documentation](https://docs.docker.com/)
- [Docker Compose Documentation](https://docs.docker.com/compose/)
- [Spring Boot Docker Documentation](https://spring.io/guides/topicals/spring-boot-docker/)
- [PostgreSQL Docker Hub](https://hub.docker.com/_/postgres)

## 🔐 Security Best Practices

### Production Checklist:

- [ ] Изменить JWT_SECRET на криптографически стойкий ключ (256+ бит)
- [ ] Изменить database пароли
- [ ] Настроить CORS только для разрешённых доменов
- [ ] Отключить Swagger UI в production (`springdoc.swagger-ui.enabled=false`)
- [ ] Использовать Docker Secrets для чувствительных данных
- [ ] Настроить TLS/SSL (через reverse proxy, например Nginx)
- [ ] Включить rate limiting
- [ ] Настроить firewall правила
- [ ] Использовать non-root пользователя (уже настроено в Dockerfile)
- [ ] Регулярно обновлять базовые образы
- [ ] Сканировать образы на уязвимости (`docker scan necpgame-auth-service`)

## 📝 Notes

- Логи приложения сохраняются в `./logs/application.log` (маппинг из контейнера)
- PostgreSQL данные хранятся в Docker volume `postgres_data`
- По умолчанию используется порт `5433` для PostgreSQL (чтобы не конфликтовать с локальной установкой)
- API Gateway доступен на `http://localhost:8080`
- Auth-service доступен напрямую на `http://localhost:8081`
- Swagger UI доступен на `http://localhost:8080/swagger-ui.html`
- Health check endpoint (gateway): `http://localhost:8080/actuator/health`
- Health check endpoint (auth-service): `http://localhost:8081/actuator/health`










