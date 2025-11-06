#!/usr/bin/env pwsh
# Скрипт для сборки Docker образа бекенда
# Использование: .\scripts\docker-build.ps1 [-Tag "latest"] [-NoCacheл]

param(
    [string]$Tag = "latest",
    [switch]$NoCache
)

Write-Host "🐳 Сборка Docker образа для NECPGAME Backend..." -ForegroundColor Cyan

# Проверка существования Dockerfile
if (-not (Test-Path "Dockerfile")) {
    Write-Host "❌ Dockerfile не найден!" -ForegroundColor Red
    exit 1
}

# Проверка существования API-SWAGGER
if (-not (Test-Path "../API-SWAGGER")) {
    Write-Host "⚠️  Директория API-SWAGGER не найдена. Генерация OpenAPI кода будет пропущена." -ForegroundColor Yellow
}

# Параметры сборки
$imageName = "necpgame-backend"
$fullTag = "${imageName}:${Tag}"

# Построение команды
$buildCommand = "docker build -t $fullTag"

if ($NoCache) {
    $buildCommand += " --no-cache"
}

$buildCommand += " ."

Write-Host "📦 Команда сборки: $buildCommand" -ForegroundColor Gray

# Выполнение сборки
Write-Host "`n🔨 Начало сборки..." -ForegroundColor Cyan
Invoke-Expression $buildCommand

if ($LASTEXITCODE -eq 0) {
    Write-Host "`n✅ Образ успешно собран: $fullTag" -ForegroundColor Green
    
    # Показываем информацию об образе
    Write-Host "`n📊 Информация об образе:" -ForegroundColor Cyan
    docker images $imageName --format "table {{.Repository}}\t{{.Tag}}\t{{.Size}}\t{{.CreatedAt}}"
    
    Write-Host "`n💡 Для запуска используйте:" -ForegroundColor Yellow
    Write-Host "   docker-compose up -d" -ForegroundColor White
    Write-Host "   или" -ForegroundColor White
    Write-Host "   docker run -p 8080:8080 $fullTag" -ForegroundColor White
} else {
    Write-Host "`n❌ Ошибка при сборке образа!" -ForegroundColor Red
    exit 1
}










