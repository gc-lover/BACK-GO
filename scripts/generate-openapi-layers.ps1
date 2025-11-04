#!/usr/bin/env pwsh
<#
.SYNOPSIS
    Генерация всех MVC слоёв из OpenAPI спецификации через OpenAPI Generator CLI

.DESCRIPTION
    Скрипт генерирует ТОЛЬКО КОНТРАКТЫ из OpenAPI спецификации:
    - DTOs (модели данных)
    - API Interfaces (REST API контракты)
    - Service Interfaces (бизнес-логика контракты)
    
    РЕАЛИЗАЦИЯ создаётся вручную в src/main/java/:
    - Entities (с relationships, indexes)
    - Repositories (с custom queries)
    - Controllers (с бизнес-логикой)
    - ServiceImpl (вся бизнес-логика)
    
    OpenAPI спецификация является единственным источником правды для контрактов.
    
    Скрипт может работать:
    - С одним файлом (параметр -ApiSpec)
    - Со всеми файлами в директории (параметр -ApiDirectory)

.PARAMETER ApiSpec
    Путь к одному OpenAPI YAML файлу
    Например: ../API-SWAGGER/api/v1/auth/character-creation.yaml

.PARAMETER ApiDirectory
    Путь к директории с OpenAPI YAML файлами
    Скрипт обработает ВСЕ .yaml и .yml файлы в этой директории
    Например: ../API-SWAGGER/api/v1/

.PARAMETER CleanBefore
    Удалить ли сгенерированные контракты перед генерацией
    По умолчанию: false (файлы просто перезаписываются)

.PARAMETER Layers
    Какие контракты генерировать: All, DTOs, Services
    По умолчанию: All
    
    Примечание: Entities, Repositories, Controllers, ServiceImpl НЕ генерируются
    автоматически - их нужно создавать вручную в src/main/java/

.EXAMPLE
    .\generate-openapi-layers.ps1 -ApiSpec ../API-SWAGGER/api/v1/auth/character-creation.yaml
    Генерирует все слои из одного файла

.EXAMPLE
    .\generate-openapi-layers.ps1 -ApiDirectory ../API-SWAGGER/api/v1/
    Генерирует все слои из ВСЕХ OpenAPI файлов в директории

.EXAMPLE
    .\generate-openapi-layers.ps1 -ApiSpec path/to/api.yaml -Layers Services
    Генерирует только Service Interfaces из указанного файла

.EXAMPLE
    .\generate-openapi-layers.ps1 -ApiDirectory ../API-SWAGGER/api/ -CleanBefore $false
    Генерирует из всех файлов БЕЗ очистки
#>

param(
    [string]$ApiSpec = "",
    [string]$ApiDirectory = "",
    [bool]$CleanBefore = $false,
    [string[]]$Layers = @("All")
)

# Цвета для вывода
$ColorSuccess = "Green"
$ColorInfo = "Cyan"
$ColorWarning = "Yellow"
$ColorError = "Red"

function Write-Step {
    param([string]$Message)
    Write-Host "`n==> $Message" -ForegroundColor $ColorInfo
}

function Write-Success {
    param([string]$Message)
    Write-Host "✓ $Message" -ForegroundColor $ColorSuccess
}

function Write-Failed {
    param([string]$Message)
    Write-Host "✗ $Message" -ForegroundColor $ColorError
}

# Переходим в корень проекта
$ScriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$ProjectRoot = Split-Path -Parent $ScriptDir
Set-Location $ProjectRoot

Write-Host "`n╔════════════════════════════════════════════════════════════╗" -ForegroundColor $ColorInfo
Write-Host "║  OpenAPI Generator - Генерация MVC слоёв                   ║" -ForegroundColor $ColorInfo
Write-Host "╚════════════════════════════════════════════════════════════╝`n" -ForegroundColor $ColorInfo

# Валидация параметров
if ([string]::IsNullOrEmpty($ApiSpec) -and [string]::IsNullOrEmpty($ApiDirectory)) {
    Write-Failed "Необходимо указать либо -ApiSpec (один файл), либо -ApiDirectory (директория с файлами)"
    Write-Host "`nПримеры использования:" -ForegroundColor $ColorWarning
    Write-Host "  .\generate-openapi-layers.ps1 -ApiSpec path/to/api.yaml" -ForegroundColor $ColorWarning
    Write-Host "  .\generate-openapi-layers.ps1 -ApiDirectory path/to/apis/" -ForegroundColor $ColorWarning
    exit 1
}

if (-not [string]::IsNullOrEmpty($ApiSpec) -and -not [string]::IsNullOrEmpty($ApiDirectory)) {
    Write-Failed "Нельзя указать одновременно -ApiSpec и -ApiDirectory. Выберите что-то одно."
    exit 1
}

# Собираем список файлов для обработки
$ApiFiles = @()

if (-not [string]::IsNullOrEmpty($ApiSpec)) {
    # Режим одного файла
    if (-not (Test-Path $ApiSpec)) {
        Write-Failed "OpenAPI файл не найден: $ApiSpec"
        exit 1
    }
    $ApiFiles += (Resolve-Path $ApiSpec).Path
    Write-Host "📄 Режим: ОДИН ФАЙЛ" -ForegroundColor $ColorInfo
    Write-Host "   Файл: $ApiSpec`n" -ForegroundColor $ColorInfo
}
elseif (-not [string]::IsNullOrEmpty($ApiDirectory)) {
    # Режим директории
    if (-not (Test-Path $ApiDirectory)) {
        Write-Failed "Директория не найдена: $ApiDirectory"
        exit 1
    }
    
    $ApiFiles = Get-ChildItem -Path $ApiDirectory -Filter "*.yaml" -File -Recurse | 
                Select-Object -ExpandProperty FullName
    
    $ymlFiles = Get-ChildItem -Path $ApiDirectory -Filter "*.yml" -File -Recurse | 
                Select-Object -ExpandProperty FullName
    
    $ApiFiles += $ymlFiles
    
    if ($ApiFiles.Count -eq 0) {
        Write-Failed "В директории $ApiDirectory не найдено ни одного .yaml или .yml файла"
        exit 1
    }
    
    Write-Host "📁 Режим: ДИРЕКТОРИЯ" -ForegroundColor $ColorInfo
    Write-Host "   Путь: $ApiDirectory" -ForegroundColor $ColorInfo
    Write-Host "   Найдено файлов: $($ApiFiles.Count)`n" -ForegroundColor $ColorInfo
    
    foreach ($file in $ApiFiles) {
        $relativePath = (Resolve-Path -Relative $file)
        Write-Host "   → $relativePath" -ForegroundColor $ColorInfo
    }
    Write-Host ""
}

Write-Host "🎯 Слои для генерации: $($Layers -join ', ')" -ForegroundColor $ColorInfo

# Очистка предыдущих генераций (обычно не требуется - файлы перезаписываются)
if ($CleanBefore) {
    Write-Step "Очистка предыдущих генераций"
    $pathsToClean = @(
        "src/main/java/com/necpgame/backjava/api",
        "src/main/java/com/necpgame/backjava/model",
        "src/main/java/com/necpgame/backjava/service"
    )
    foreach ($path in $pathsToClean) {
        if (Test-Path $path) {
            # Удаляем только СГЕНЕРИРОВАННЫЕ файлы (с аннотацией @Generated или комментарием)
            Get-ChildItem -Path $path -Filter "*.java" -Recurse | ForEach-Object {
                $content = Get-Content $_.FullName -Raw
                if ($content -match "@Generated|OpenAPI Generator" -and $content -notmatch "ServiceImpl|Controller") {
                    Remove-Item $_.FullName -Force
                    Write-Host "  Удалён: $($_.Name)" -ForegroundColor DarkGray
                }
            }
        }
    }
    Write-Success "Сгенерированные контракты очищены"
}

# Проверяем, какие контракты генерировать
$GenerateAll = $Layers -contains "All"
$GenerateDTOs = $GenerateAll -or ($Layers -contains "DTOs")
$GenerateServices = $GenerateAll -or ($Layers -contains "Services")

# Счетчики для статистики
$TotalFiles = $ApiFiles.Count
$ProcessedFiles = 0
$FailedFiles = 0

# ==============================================================================
# ОБРАБОТКА КАЖДОГО OpenAPI ФАЙЛА
# ==============================================================================
foreach ($ApiFile in $ApiFiles) {
    $ProcessedFiles++
    $FileName = Split-Path -Leaf $ApiFile
    
    Write-Host "`n" -NoNewline
    Write-Host "═══════════════════════════════════════════════════════════════" -ForegroundColor $ColorInfo
    Write-Host "  Файл $ProcessedFiles/$TotalFiles : $FileName" -ForegroundColor $ColorInfo
    Write-Host "═══════════════════════════════════════════════════════════════" -ForegroundColor $ColorInfo
    
    # Общие параметры для текущего файла (без кастомных шаблонов для API)
    $CommonParams = @(
        "generate",
        "-i", $ApiFile,
        "-g", "spring"
    )

    # ==============================================================================
    # 1. Генерация DTOs и API Interfaces (контракты REST API)
    # ==============================================================================
    if ($GenerateDTOs) {
        Write-Step "1/2 Генерация DTOs и API Interfaces"
        
        $DtosParams = $CommonParams + @(
            "-o", "target/generated-openapi-temp",
            "--api-package", "com.necpgame.backjava.api",
            "--model-package", "com.necpgame.backjava.model",
            "--invoker-package", "com.necpgame.backjava.invoker",
            "-p", "interfaceOnly=true,delegatePattern=false,useSpringBoot3=true,useJakartaEe=true,useBeanValidation=true,hideGenerationTimestamp=true,sourceFolder=."
        )
        
        $result = npx --yes @openapitools/openapi-generator-cli @DtosParams 2>&1
        if ($LASTEXITCODE -eq 0) {
            # Копируем только нужные директории в src/main/java/
            $tempSrc = "target/generated-openapi-temp/com"
            $targetSrc = "src/main/java/com"
            if (Test-Path $tempSrc) {
                Copy-Item -Path $tempSrc -Destination "src/main/java/" -Recurse -Force
                Write-Success "DTOs и API Interfaces сгенерированы и скопированы в src/"
            } else {
                Write-Failed "Не найдена директория $tempSrc после генерации"
                $FailedFiles++
                continue
            }
        } else {
            Write-Failed "Ошибка генерации DTOs из $FileName"
            Write-Host $result -ForegroundColor $ColorError
            $FailedFiles++
            continue
        }
    }

    # ==============================================================================
    # 2. Генерация Service интерфейсов (контракты бизнес-логики)
    # ==============================================================================
    if ($GenerateServices) {
        Write-Step "2/2 Генерация Service интерфейсов"
        
        $ServicesParams = @(
            "generate",
            "-i", $ApiFile,
            "-g", "spring",
            "-t", "templates",
            "-o", "target/generated-services-temp",
            "--api-package", "com.necpgame.backjava.service",
            "--model-package", "com.necpgame.backjava.model",
            "--api-name-suffix", "Service",
            "-p", "interfaceOnly=true,generateApis=true,generateModels=false,useSpringBoot3=true,useJakartaEe=true,hideGenerationTimestamp=true,sourceFolder=."
        )
        
        $result = npx --yes @openapitools/openapi-generator-cli @ServicesParams 2>&1
        if ($LASTEXITCODE -eq 0) {
            # Копируем только service директорию в src/main/java/
            $tempSrc = "target/generated-services-temp/com"
            if (Test-Path $tempSrc) {
                Copy-Item -Path $tempSrc -Destination "src/main/java/" -Recurse -Force
                Write-Success "Service интерфейсы сгенерированы и скопированы в src/"
            } else {
                Write-Failed "Не найдена директория $tempSrc после генерации"
                $FailedFiles++
                continue
            }
        } else {
            Write-Failed "Ошибка генерации Services из $FileName"
            Write-Host $result -ForegroundColor $ColorError
            $FailedFiles++
            continue
        }
    }

    Write-Success "Контракты из $FileName сгенерированы успешно"
}

# Конец цикла по файлам

# ==============================================================================
# Итоги
# ==============================================================================
Write-Host "`n"
Write-Host "═══════════════════════════════════════════════════════════════" -ForegroundColor $ColorInfo

$SuccessfulFiles = $ProcessedFiles - $FailedFiles

if ($FailedFiles -eq 0) {
    Write-Host "`n╔════════════════════════════════════════════════════════════╗" -ForegroundColor $ColorSuccess
    Write-Host "║  ✓ Генерация завершена успешно!                           ║" -ForegroundColor $ColorSuccess
    Write-Host "╚════════════════════════════════════════════════════════════╝`n" -ForegroundColor $ColorSuccess
} else {
    Write-Host "`n╔════════════════════════════════════════════════════════════╗" -ForegroundColor $ColorWarning
    Write-Host "║  ⚠ Генерация завершена с ошибками                         ║" -ForegroundColor $ColorWarning
    Write-Host "╚════════════════════════════════════════════════════════════╝`n" -ForegroundColor $ColorWarning
}

Write-Host "📊 Статистика:" -ForegroundColor $ColorInfo
Write-Host "   Всего файлов: $TotalFiles" -ForegroundColor $ColorInfo
Write-Host "   Успешно: $SuccessfulFiles" -ForegroundColor ($SuccessfulFiles -eq $TotalFiles ? $ColorSuccess : $ColorWarning)
if ($FailedFiles -gt 0) {
    Write-Host "   Ошибок: $FailedFiles" -ForegroundColor $ColorError
}

Write-Host "`n📁 Сгенерированные контракты находятся в:" -ForegroundColor $ColorInfo
if ($GenerateDTOs) { 
    Write-Host "   → src/main/java/com/necpgame/backjava/api/      (API Interfaces)" -ForegroundColor $ColorInfo 
    Write-Host "   → src/main/java/com/necpgame/backjava/model/    (DTOs)" -ForegroundColor $ColorInfo 
}
if ($GenerateServices) { 
    Write-Host "   → src/main/java/com/necpgame/backjava/service/  (Service Interfaces)" -ForegroundColor $ColorInfo 
}

Write-Host "`n💡 Следующие шаги:" -ForegroundColor $ColorInfo
Write-Host "   1. Проверьте сгенерированные контракты в src/main/java/" -ForegroundColor $ColorInfo
Write-Host "   2. Создайте ВРУЧНУЮ реализацию в src/main/java/:" -ForegroundColor $ColorInfo
Write-Host "      • entity/        - JPA Entities с relationships и indexes" -ForegroundColor $ColorInfo
Write-Host "      • repository/    - Spring Data Repositories с custom queries" -ForegroundColor $ColorInfo
Write-Host "      • controller/    - REST Controllers с бизнес-логикой" -ForegroundColor $ColorInfo
Write-Host "      • service/impl/  - Service implementations с бизнес-логикой" -ForegroundColor $ColorInfo
Write-Host "   3. Запустите Maven build: mvn clean compile" -ForegroundColor $ColorInfo
Write-Host "   4. Контракты НЕ будут удалены при 'mvn clean' - они в src/" -ForegroundColor $ColorSuccess
Write-Host ""

# Выход с соответствующим кодом
exit $FailedFiles

