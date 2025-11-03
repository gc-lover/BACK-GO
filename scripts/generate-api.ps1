# Скрипт для генерации Go серверного кода из API Swagger спецификаций
# Использование: .\scripts\generate-api.ps1 <путь-к-api-swagger-file> <output-dir> [framework]

param(
    [Parameter(Mandatory=$true)]
    [string]$SwaggerFile,
    
    [Parameter(Mandatory=$true)]
    [string]$OutputDir,
    
    [Parameter(Mandatory=$false)]
    [string]$Framework = "go-gin-server"
)

# Проверка аргументов
if (-not $SwaggerFile -or -not $OutputDir) {
    Write-Host "Ошибка: Недостаточно аргументов" -ForegroundColor Red
    Write-Host "Использование: .\scripts\generate-api.ps1 <путь-к-api-swagger-file> <output-dir> [framework]"
    Write-Host "Пример: .\scripts\generate-api.ps1 ..\API-SWAGGER\api\v1\gameplay\social\personal-npc-tool.yaml internal\api\generated\personal-npc-tool go-gin-server"
    Write-Host ""
    Write-Host "Доступные фреймворки:"
    Write-Host "  - go-server (по умолчанию)"
    Write-Host "  - go-gin-server (Gin)"
    Write-Host "  - go-echo-server (Echo)"
    Write-Host "  - go-fiber-server (Fiber)"
    exit 1
}

# Проверка существования файла
if (-not (Test-Path $SwaggerFile)) {
    Write-Host "Ошибка: Файл $SwaggerFile не найден" -ForegroundColor Red
    exit 1
}

# Проверка установки OpenAPI Generator
try {
    $null = Get-Command openapi-generator-cli -ErrorAction Stop
} catch {
    Write-Host "OpenAPI Generator не установлен. Устанавливаю..." -ForegroundColor Yellow
    npm install -g @openapitools/openapi-generator-cli
}

# Извлечение имени пакета из пути
$PackageName = (Split-Path -Leaf $OutputDir) -replace '-', '_' -replace '([A-Z])', { $_.Value.ToLower() }

Write-Host "Генерация Go серверного кода..." -ForegroundColor Green
Write-Host "  Файл: $SwaggerFile"
Write-Host "  Выходная директория: $OutputDir"
Write-Host "  Фреймворк: $Framework"
Write-Host "  Пакет: $PackageName"
Write-Host ""

# Создание директории, если не существует
if (-not (Test-Path $OutputDir)) {
    New-Item -ItemType Directory -Path $OutputDir -Force | Out-Null
}

# Генерация кода
$result = openapi-generator-cli generate `
  -i $SwaggerFile `
  -g $Framework `
  -o $OutputDir `
  --additional-properties=packageName="$PackageName",serverPort=8080

if ($LASTEXITCODE -eq 0) {
    Write-Host "✓ Генерация завершена успешно!" -ForegroundColor Green
    Write-Host ""
    Write-Host "Следующие шаги:"
    Write-Host "  1. Проверьте сгенерированный код в $OutputDir"
    Write-Host "  2. Создайте handlers в internal/handlers/"
    Write-Host "  3. Создайте services в internal/services/"
    Write-Host "  4. Создайте repositories в internal/repositories/"
} else {
    Write-Host "✗ Ошибка при генерации кода" -ForegroundColor Red
    exit 1
}

