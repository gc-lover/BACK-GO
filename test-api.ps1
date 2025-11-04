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

# Проверка Swagger UI
Write-Host "`n3. Проверка /swagger-ui.html:" -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "http://localhost:8080/swagger-ui.html" -Method GET -UseBasicParsing
    Write-Host "   Status: $($response.StatusCode)" -ForegroundColor Green
    Write-Host "   Swagger UI доступен" -ForegroundColor Green
} catch {
    Write-Host "   Ошибка: $_" -ForegroundColor Red
}

# Проверка OpenAPI спецификации
Write-Host "`n4. Проверка /api-docs:" -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "http://localhost:8080/api-docs" -Method GET -UseBasicParsing
    Write-Host "   Status: $($response.StatusCode)" -ForegroundColor Green
    Write-Host "   OpenAPI спецификация доступна" -ForegroundColor Green
} catch {
    Write-Host "   Ошибка: $_" -ForegroundColor Red
}

Write-Host "`nГотово!" -ForegroundColor Cyan
