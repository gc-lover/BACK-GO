# Docker Setup для локальной разработки

## PostgreSQL в Docker

Для локальной разработки используется PostgreSQL в Docker контейнере.

### Быстрый старт

1. **Запуск контейнера:**
   ```bash
   docker-compose up -d
   ```

2. **Проверка статуса:**
   ```bash
   docker-compose ps
   ```

3. **Проверка логов:**
   ```bash
   docker-compose logs postgres
   ```

4. **Остановка контейнера:**
   ```bash
   docker-compose down
   ```

### Параметры подключения

- **Host:** `localhost`
- **Port:** `5433` (внешний), `5432` (внутренний)
- **User:** `necpgame`
- **Password:** `necpgame`
- **Database:** `necpgame`

**Строка подключения JDBC:**
```
jdbc:postgresql://localhost:5433/necpgame
```

### Применение миграций

Миграции применяются автоматически при запуске Spring Boot приложения через Flyway.

Миграции находятся в `src/main/resources/db/migration/`

### Подключение через psql

```bash
# Подключение к БД
docker exec -it necpgame-postgres psql -U necpgame -d necpgame

# Или через docker-compose
docker-compose exec postgres psql -U necpgame -d necpgame
```

### Управление данными

**Остановка с сохранением данных:**
```bash
docker-compose down
```

**Остановка с удалением данных (ОСТОРОЖНО!):**
```bash
docker-compose down -v
```

### Healthcheck

Контейнер имеет healthcheck, который проверяет готовность PostgreSQL:
- Проверка каждые 10 секунд
- Timeout: 5 секунд
- Retries: 5

Проверка статуса:
```bash
docker-compose ps
```

### Изменение настроек

Если нужно изменить настройки БД (пользователь, пароль, имя БД), отредактируй `docker-compose.yml` и пересоздай контейнер:

```bash
docker-compose down -v
docker-compose up -d
```

### Troubleshooting

**Проблема: Порт 5433 уже занят**
```bash
# Измени порт в docker-compose.yml
ports:
  - "5434:5432"  # Внешний порт:Внутренний порт
```

**Проблема: Контейнер не запускается**
```bash
# Проверь логи
docker-compose logs postgres

# Проверь, что Docker запущен
docker ps
```

**Проблема: Нет доступа к БД**
```bash
# Проверь, что контейнер запущен
docker-compose ps

# Проверь healthcheck
docker inspect necpgame-postgres | grep Health
```
