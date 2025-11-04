# Скрипт для генерации Services и ServiceImpl из сгенерированных контроллеров
# Используется для автоматической генерации Services из OpenAPI контроллеров

$ErrorActionPreference = "Continue"
$script:exitCode = 0

$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$projectRoot = Split-Path -Parent $scriptDir
$controllersDir = Join-Path $projectRoot "target\generated-sources\openapi\src\main\java\com\necpgame\backjava\api"
$servicesDir = Join-Path $projectRoot "target\generated-sources\services\src\main\java\com\necpgame\backjava\service"
$serviceImplDir = Join-Path $projectRoot "target\generated-sources\services\src\main\java\com\necpgame\backjava\service\impl"

# Создаем директории если их нет
New-Item -ItemType Directory -Force -Path $servicesDir | Out-Null
New-Item -ItemType Directory -Force -Path $serviceImplDir | Out-Null

Write-Host "=== Генерация Services и ServiceImpl ===" -ForegroundColor Cyan
Write-Host "Контроллеры: $controllersDir"
Write-Host "Services: $servicesDir"
Write-Host "ServiceImpl: $serviceImplDir"
Write-Host ""

# Получаем все контроллеры
$controllers = Get-ChildItem -Path $controllersDir -Filter "*Controller.java" -ErrorAction SilentlyContinue

if (-not $controllers) {
    Write-Host "⚠️  Контроллеры не найдены в $controllersDir" -ForegroundColor Yellow
    Write-Host "Сначала запустите: mvn clean generate-sources" -ForegroundColor Yellow
    exit 0
}

foreach ($controller in $controllers) {
    $controllerContent = Get-Content $controller.FullName -Raw
    $controllerName = $controller.BaseName
    
    # Извлекаем имя Service (убираем "Controller")
    $serviceName = $controllerName -replace "Controller$", "Service"
    $serviceImplName = $serviceName + "Impl"
    
    Write-Host "Обработка: $controllerName -> $serviceName" -ForegroundColor Green
    
    # Генерируем Service интерфейс
    $serviceContent = $controllerContent -replace "public class $controllerName", "public interface $serviceName"
    $serviceContent = $serviceContent -replace "@RestController", ""
    $serviceContent = $serviceContent -replace "@RequestMapping.*", ""
    $serviceContent = $serviceContent -replace "@Generated.*", '@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-11-04")'
    $serviceContent = $serviceContent -replace "package com\.necpgame\.backjava\.api;", "package com.necpgame.backjava.service;"
    $serviceContent = $serviceContent -replace "import com\.necpgame\.backjava\.model\.", "import com.necpgame.backjava.model."
    
    # Удаляем аннотации Spring Web из методов
    $serviceContent = $serviceContent -replace "    @.*Mapping\(`r?`n.*`r?`n.*`r?`n    \)`r?`n", ""
    $serviceContent = $serviceContent -replace "    @.*Mapping\([^)]*\)`r?`n", ""
    
    # Удаляем тело методов и оставляем только сигнатуру (для интерфейса)
    # Используем более точное регулярное выражение для удаления тел методов
    $serviceContent = $serviceContent -replace "(    public \w+ \w+\([^)]*\))\s*\{[^}]*\}", '$1;'
    
    # Удаляем TODO комментарии и return statements
    $serviceContent = $serviceContent -replace "// TODO:.*`r?`n", ""
    $serviceContent = $serviceContent -replace "return null;`r?`n", ""
    $serviceContent = $serviceContent -replace "return ResponseEntity\.ok\(\)\.build\(\);`r?`n", ""
    $serviceContent = $serviceContent -replace "return ResponseEntity\.ok\(null\);`r?`n", ""
    
    # Удаляем импорты Spring Web
    $serviceContent = $serviceContent -replace "import org\.springframework\.web\.bind\.annotation\..*`r?`n", ""
    $serviceContent = $serviceContent -replace "import org\.springframework\.http\..*`r?`n", ""
    
    # Исправляем сигнатуры методов - убираем аннотации Spring из параметров
    $serviceContent = $serviceContent -replace "@Valid @RequestBody\s+", "@Valid "
    $serviceContent = $serviceContent -replace "@PathVariable\([^)]+\)\s+", ""
    $serviceContent = $serviceContent -replace "@RequestParam\([^)]+\)\s+", ""
    $serviceContent = $serviceContent -replace "@RequestHeader\([^)]+\)\s+", ""
    $serviceContent = $serviceContent -replace "@CookieValue\([^)]+\)\s+", ""
    $serviceContent = $serviceContent -replace "@NotNull\s+", ""
    $serviceContent = $serviceContent -replace "@Nullable\s+", ""
    
    # Сохраняем Service
    $serviceFile = Join-Path $servicesDir "$serviceName.java"
    $serviceContent | Out-File -FilePath $serviceFile -Encoding UTF8 -NoNewline
    
    # Генерируем ServiceImpl
    $serviceImplContent = $controllerContent -replace "public class $controllerName", "public class $serviceImplName implements $serviceName"
    $serviceImplContent = $serviceImplContent -replace "@RestController", "@Service`n@Transactional"
    $serviceImplContent = $serviceImplContent -replace "@RequestMapping.*", ""
    $serviceImplContent = $serviceImplContent -replace "package com\.necpgame\.backjava\.api;", "package com.necpgame.backjava.service.impl;"
    $serviceImplContent = $serviceImplContent -replace "import com\.necpgame\.backjava\.model\.", "import com.necpgame.backjava.model."
    
    # Удаляем аннотации Spring Web из методов
    $serviceImplContent = $serviceImplContent -replace "    @.*Mapping\(`r?`n.*`r?`n.*`r?`n    \)`r?`n", ""
    $serviceImplContent = $serviceImplContent -replace "    @.*Mapping\([^)]*\)`r?`n", ""
    
    # Добавляем @Override перед методами
    $serviceImplContent = $serviceImplContent -replace "    public (\w+) (\w+)\(([^)]*)\) \{", "    @Override`n    public $1 $2($3) {"
    
    # Заменяем TODO комментарии на более понятные
    $serviceImplContent = $serviceImplContent -replace "// TODO: Реализовать бизнес-логику через Service", "// TODO: Реализовать бизнес-логику через Repository"
    
    # Удаляем импорты Spring Web
    $serviceImplContent = $serviceImplContent -replace "import org\.springframework\.web\.bind\.annotation\..*`r?`n", ""
    $serviceImplContent = $serviceImplContent -replace "import org\.springframework\.http\..*`r?`n", ""
    $serviceImplContent = $serviceImplContent -replace "import org\.springframework\.http\.ResponseEntity;", ""
    
    # Добавляем импорты для Service
    if ($serviceImplContent -notmatch "import org\.springframework\.stereotype\.Service;") {
        $serviceImplContent = $serviceImplContent -replace "(import jakarta\.validation\.Valid;)", "import org.springframework.stereotype.Service;`nimport org.springframework.transaction.annotation.Transactional;`n$1"
    }
    
    # Сохраняем ServiceImpl
    $serviceImplFile = Join-Path $serviceImplDir "$serviceImplName.java"
    try {
        $serviceImplContent | Out-File -FilePath $serviceImplFile -Encoding UTF8 -NoNewline
    } catch {
        Write-Host "  ⚠️  Ошибка при сохранении $serviceImplName.java: $_" -ForegroundColor Yellow
        $script:exitCode = 1
        continue
    }
    
    Write-Host "  ✅ Создан: $serviceName.java" -ForegroundColor Green
    Write-Host "  ✅ Создан: $serviceImplName.java" -ForegroundColor Green
}

Write-Host ""
Write-Host "=== Генерация завершена ===" -ForegroundColor Green
Write-Host "Services созданы в: $servicesDir"
Write-Host "ServiceImpl созданы в: $serviceImplDir"

if ($script:exitCode -ne 0) {
    Write-Host "⚠️  Генерация завершена с предупреждениями" -ForegroundColor Yellow
    exit 0  # Все равно возвращаем 0, чтобы не ломать сборку
} else {
    exit 0
}

