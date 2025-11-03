# Инструкция по запуску бекенда

## Проблема: 404 на /api/v1/health

Если фронтенд не может подключиться к `/api/v1/health`, выполните:

### 1. Остановите все процессы Go

```powershell
# Найти процессы на порту 8080
Get-NetTCPConnection -LocalPort 8080 | Select-Object OwningProcess

# Остановить процесс (замените PID на реальный)
taskkill /F /PID <PID>
```

### 2. Перезапустите бекенд

```powershell
cd C:\NECPGAME\BACK-GO
go run ./cmd/server/main.go
```

### 3. Проверьте endpoints

```powershell
# Корневой health check
curl http://localhost:8080/health

# API v1 health check
curl http://localhost:8080/api/v1/health

# Gameplay status
curl http://localhost:8080/api/v1/gameplay/status
```

### 4. Проверьте что бекенд запущен

В логах должно быть:
```
Starting server address=:8080 port=8080
Routes registered successfully
```

## Доступные endpoints

- `GET /health` - Корневой health check
- `GET /api/v1/health` - API v1 health check
- `GET /api/v1/gameplay/health` - Gameplay health check
- `GET /api/v1/gameplay/status` - Gameplay status
- `GET /api/v1/social/health` - Social health check

