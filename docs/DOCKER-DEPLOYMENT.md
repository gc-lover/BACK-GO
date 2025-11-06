# 🐳 Docker Deployment Guide

Полное руководство по развёртыванию NECPGAME Backend в Docker.

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

### Запуск всего стека (PostgreSQL + Backend):

```bash
# Windows PowerShell
cd BACK-GO
docker-compose up -d

# Linux/Mac
cd BACK-GO
docker-compose up -d
```

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

Используем **multi-stage build** для оптимизации размера образа:

#### Stage 1: Builder (сборка)
- **Базовый образ**: `eclipse-temurin:21-jdk-alpine`
- **Размер**: ~500MB (временный)
- **Компоненты**: JDK 21, Maven, Node.js
- **Действия**: 
  - Загрузка зависимостей
  - Генерация OpenAPI кода (если нужно)
  - Компиляция Java кода
  - Сборка JAR файла

#### Stage 2: Runtime (запуск)
- **Базовый образ**: `eclipse-temurin:21-jre-alpine`
- **Размер**: ~200MB (финальный)
- **Компоненты**: только JRE 21
- **Действия**: запуск приложения

### Преимущества подхода:
- ✅ **Маленький размер** финального образа (~200MB вместо ~500MB)
- ✅ **Безопасность** - в runtime образе нет инструментов сборки
- ✅ **Быстрый деплой** - меньше данных для загрузки
- ✅ **Кэширование** - Docker кэширует слои для ускорения пересборки

## 🔨 Сборка образа

### Автоматическая сборка через скрипт:

```powershell
# Windows
.\scripts\docker-build.ps1

# С тегом
.\scripts\docker-build.ps1 -Tag "v1.0.0"

# Без кэша (полная пересборка)
.\scripts\docker-build.ps1 -NoCache
```

```bash
# Linux/Mac
./scripts/docker-build.sh

# С тегом
./scripts/docker-build.sh v1.0.0

# Без кэша
./scripts/docker-build.sh latest --no-cache
```

### Ручная сборка:

```bash
# Базовая сборка
docker build -t necpgame-backend:latest .

# Без кэша
docker build --no-cache -t necpgame-backend:latest .

# С конкретным тегом
docker build -t necpgame-backend:v1.0.0 .
```

## 🐳 Запуск через Docker Compose

### Файл `docker-compose.yml`

Определяет 2 сервиса:
1. **postgres** - PostgreSQL база данных
2. **backend** - Java Spring Boot приложение

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

# Перезапуск конкретного сервиса
docker-compose restart backend

# Пересборка образов перед запуском
docker-compose up -d --build

# Просмотр статуса сервисов
docker-compose ps

# Просмотр логов
docker-compose logs -f backend
docker-compose logs -f postgres

# Выполнение команды в контейнере
docker-compose exec backend sh
docker-compose exec postgres psql -U necpgame -d necpgame
```

### Проверка здоровья сервисов:

```bash
# Проверка health check
docker-compose ps

# Backend health endpoint
curl http://localhost:8080/actuator/health

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

### Запуск Backend:

```bash
docker run -d \
  --name necpgame-backend \
  -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=docker \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/necpgame \
  -e SPRING_DATASOURCE_USERNAME=necpgame \
  -e SPRING_DATASOURCE_PASSWORD=necpgame \
  --link necpgame-postgres:postgres \
  necpgame-backend:latest
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

#### Backend Logs:
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
docker-compose logs -f backend
docker-compose logs -f postgres

# Последние 100 строк
docker-compose logs --tail=100 backend

# Логи с временными метками
docker-compose logs -f -t backend
```

### Health Checks:

```bash
# Backend health endpoint
curl http://localhost:8080/actuator/health

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
docker stats necpgame-backend
```

### Доступ внутрь контейнера:

```bash
# Backend
docker-compose exec backend sh

# PostgreSQL
docker-compose exec postgres psql -U necpgame -d necpgame
```

## 🐛 Troubleshooting

### Контейнер не запускается

1. **Проверить логи:**
```bash
docker-compose logs backend
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
docker-compose build --no-cache backend
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
# Подключиться к контейнеру
docker-compose exec backend sh

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
  backend:
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
- [ ] Сканировать образы на уязвимости (`docker scan necpgame-backend`)

## 📝 Notes

- Логи приложения сохраняются в `./logs/application.log` (маппинг из контейнера)
- PostgreSQL данные хранятся в Docker volume `postgres_data`
- По умолчанию используется порт `5433` для PostgreSQL (чтобы не конфликтовать с локальной установкой)
- Backend доступен на `http://localhost:8080`
- Swagger UI доступен на `http://localhost:8080/swagger-ui.html`
- Health check endpoint: `http://localhost:8080/actuator/health`










