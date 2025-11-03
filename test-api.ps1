# Тестовый скрипт для проверки API endpoints

Write-Host "Проверка API endpoints..." -ForegroundColor Cyan

# Проверка корневого health check
Write-Host "`n1. Проверка /health:" -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "http://localhost:8080/health" -Method GET -UseBasicParsing
    Write-Host "   Status: $($response.StatusCode)" -ForegroundColor Green
    Write-Host "   Response: $($response.Content)" -ForegroundColor Green
} catch {
    Write-Host "   Ошибка: $_" -ForegroundColor Red
}

# Проверка API v1 health check
Write-Host "`n2. Проверка /api/v1/health:" -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "http://localhost:8080/api/v1/health" -Method GET -UseBasicParsing
    Write-Host "   Status: $($response.StatusCode)" -ForegroundColor Green
    Write-Host "   Response: $($response.Content)" -ForegroundColor Green
} catch {
    Write-Host "   Ошибка: $_" -ForegroundColor Red
}

# Проверка gameplay status
Write-Host "`n3. Проверка /api/v1/gameplay/status:" -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "http://localhost:8080/api/v1/gameplay/status" -Method GET -UseBasicParsing
    Write-Host "   Status: $($response.StatusCode)" -ForegroundColor Green
    Write-Host "   Response: $($response.Content)" -ForegroundColor Green
} catch {
    Write-Host "   Ошибка: $_" -ForegroundColor Red
}

Write-Host "`nГотово!" -ForegroundColor Cyan

