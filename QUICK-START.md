# Быстрый старт BACK-GO

## Установка зависимостей

```bash
cd BACK-GO
go mod download
```

## Запуск PostgreSQL

```bash
docker-compose up -d
```

## Запуск сервера

```bash
# Через make
make run

# Или напрямую
go run ./cmd/server/main.go
```

## Проверка работы

Откройте в браузере: `http://localhost:8080/health`

Или через curl:
```bash
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

## API эндпоинты

- `GET /health` - Health check основного сервера
- `GET /api/v1/gameplay/health` - Health check игровых данных
- `GET /api/v1/gameplay/status` - Статус игровых данных
- `GET /api/v1/social/health` - Health check социальных функций

## Остановка сервера

Нажмите `Ctrl+C` для graceful shutdown

## Остановка PostgreSQL

```bash
docker-compose down
```

