#!/usr/bin/env pwsh
<#
.SYNOPSIS
    Генерация всех MVC слоёв из OpenAPI спецификации через OpenAPI Generator CLI

.DESCRIPTION
    Скрипт генерирует все слои приложения из OpenAPI спецификации:
    - DTOs и API интерфейсы
    - JPA Entities
    - Spring Data Repositories
    - Service интерфейсы
    - REST Controllers
    
    OpenAPI спецификация является единственным источником правды для генерации.
    
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
    Удалить ли target/generated-sources перед генерацией
    По умолчанию: true

.PARAMETER Layers
    Какие слои генерировать: All, DTOs, Entities, Repositories, Services, Controllers
    По умолчанию: All

.EXAMPLE
    .\generate-openapi-layers.ps1 -ApiSpec ../API-SWAGGER/api/v1/auth/character-creation.yaml
    Генерирует все слои из одного файла

.EXAMPLE
    .\generate-openapi-layers.ps1 -ApiDirectory ../API-SWAGGER/api/v1/
    Генерирует все слои из ВСЕХ OpenAPI файлов в директории

.EXAMPLE
    .\generate-openapi-layers.ps1 -ApiSpec path/to/api.yaml -Layers Controllers,Services
    Генерирует только Controllers и Services из указанного файла

.EXAMPLE
    .\generate-openapi-layers.ps1 -ApiDirectory ../API-SWAGGER/api/ -CleanBefore $false
    Генерирует из всех файлов БЕЗ очистки
#>

param(
    [string]$ApiSpec = "",
    [string]$ApiDirectory = "",
    [bool]$CleanBefore = $true,
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

# Очистка предыдущих генераций
if ($CleanBefore) {
    Write-Step "Очистка предыдущих генераций"
    if (Test-Path "target/generated-sources") {
        Remove-Item -Path "target/generated-sources" -Recurse -Force
        Write-Success "Директория target/generated-sources удалена"
    }
}

# Проверяем, какие слои генерировать
$GenerateAll = $Layers -contains "All"
$GenerateDTOs = $GenerateAll -or ($Layers -contains "DTOs")
$GenerateEntities = $GenerateAll -or ($Layers -contains "Entities")
$GenerateRepositories = $GenerateAll -or ($Layers -contains "Repositories")
$GenerateServices = $GenerateAll -or ($Layers -contains "Services")
$GenerateControllers = $GenerateAll -or ($Layers -contains "Controllers")

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
    
    # Общие параметры для текущего файла
    $CommonParams = @(
        "generate",
        "-i", $ApiFile,
        "-g", "spring",
        "-t", "templates"
    )

    # ==============================================================================
    # 1. Генерация DTOs и API Interfaces
    # ==============================================================================
    if ($GenerateDTOs) {
        Write-Step "1/5 Генерация DTOs и API Interfaces"
        
        $DtosParams = $CommonParams + @(
            "-o", "target/generated-sources/openapi",
            "--api-package", "com.necpgame.backjava.api",
            "--model-package", "com.necpgame.backjava.model",
            "--invoker-package", "com.necpgame.backjava.invoker",
            "-p", "interfaceOnly=true,useSpringBoot3=true,useJakartaEe=true,openApiNullable=false,useBeanValidation=true"
        )
        
        $result = npx --yes @openapitools/openapi-generator-cli @DtosParams 2>&1
        if ($LASTEXITCODE -eq 0) {
            Write-Success "DTOs и API Interfaces сгенерированы"
        } else {
            Write-Failed "Ошибка генерации DTOs из $FileName"
            Write-Host $result -ForegroundColor $ColorError
            $FailedFiles++
            continue
        }
    }

    # ==============================================================================
    # 2. Генерация JPA Entities
    # ==============================================================================
    if ($GenerateEntities) {
        Write-Step "2/5 Генерация JPA Entities"
        
        $EntitiesParams = $CommonParams + @(
            "-o", "target/generated-sources/entities",
            "--model-package", "com.necpgame.backjava.entity",
            "-p", "generateApis=false,generateModels=true,useSpringBoot3=true,useJakartaEe=true,modelTemplateFiles=model.mustache=Entity.java"
        )
        
        $result = npx --yes @openapitools/openapi-generator-cli @EntitiesParams 2>&1
        if ($LASTEXITCODE -eq 0) {
            Write-Success "JPA Entities сгенерированы"
        } else {
            Write-Failed "Ошибка генерации Entities из $FileName"
            Write-Host $result -ForegroundColor $ColorError
            $FailedFiles++
            continue
        }
    }

    # ==============================================================================
    # 3. Генерация Spring Data Repositories
    # ==============================================================================
    if ($GenerateRepositories) {
        Write-Step "3/5 Генерация Spring Data Repositories"
        
        $RepositoriesParams = $CommonParams + @(
            "-o", "target/generated-sources/repositories",
            "--model-package", "com.necpgame.backjava.repository",
            "-p", "generateApis=false,generateModels=true,useSpringBoot3=true,useJakartaEe=true,modelTemplateFiles=repositoryModel.mustache=Repository.java"
        )
        
        $result = npx --yes @openapitools/openapi-generator-cli @RepositoriesParams 2>&1
        if ($LASTEXITCODE -eq 0) {
            Write-Success "Repositories сгенерированы"
        } else {
            Write-Failed "Ошибка генерации Repositories из $FileName"
            Write-Host $result -ForegroundColor $ColorError
            $FailedFiles++
            continue
        }
    }

    # ==============================================================================
    # 4. Генерация Service интерфейсов
    # ==============================================================================
    if ($GenerateServices) {
        Write-Step "4/5 Генерация Service интерфейсов"
        
        $ServicesParams = $CommonParams + @(
            "-o", "target/generated-sources/services",
            "--api-package", "com.necpgame.backjava.service",
            "--model-package", "com.necpgame.backjava.model",
            "--api-name-suffix", "Service",
            "-p", "interfaceOnly=true,generateApis=true,generateModels=false,useSpringBoot3=true,useJakartaEe=true"
        )
        
        $result = npx --yes @openapitools/openapi-generator-cli @ServicesParams 2>&1
        if ($LASTEXITCODE -eq 0) {
            Write-Success "Service интерфейсы сгенерированы"
        } else {
            Write-Failed "Ошибка генерации Services из $FileName"
            Write-Host $result -ForegroundColor $ColorError
            $FailedFiles++
            continue
        }
    }

    # ==============================================================================
    # 5. Генерация REST Controllers
    # ==============================================================================
    if ($GenerateControllers) {
        Write-Step "5/5 Генерация REST Controllers"
        
        $ControllersParams = $CommonParams + @(
            "-o", "target/generated-sources/controllers",
            "--api-package", "com.necpgame.backjava.controller",
            "--model-package", "com.necpgame.backjava.model",
            "-p", "interfaceOnly=false,generateApis=true,generateModels=false,useSpringBoot3=true,useJakartaEe=true,delegatePattern=false"
        )
        
        $result = npx --yes @openapitools/openapi-generator-cli @ControllersParams 2>&1
        if ($LASTEXITCODE -eq 0) {
            Write-Success "Controllers сгенерированы"
        } else {
            Write-Failed "Ошибка генерации Controllers из $FileName"
            Write-Host $result -ForegroundColor $ColorError
            $FailedFiles++
            continue
        }
    }
    
    Write-Success "Файл $FileName обработан успешно"
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

Write-Host "`n📁 Сгенерированные файлы находятся в:" -ForegroundColor $ColorInfo
if ($GenerateDTOs) { Write-Host "   → target/generated-sources/openapi/" -ForegroundColor $ColorInfo }
if ($GenerateEntities) { Write-Host "   → target/generated-sources/entities/" -ForegroundColor $ColorInfo }
if ($GenerateRepositories) { Write-Host "   → target/generated-sources/repositories/" -ForegroundColor $ColorInfo }
if ($GenerateServices) { Write-Host "   → target/generated-sources/services/" -ForegroundColor $ColorInfo }
if ($GenerateControllers) { Write-Host "   → target/generated-sources/controllers/" -ForegroundColor $ColorInfo }

Write-Host "`n💡 Следующие шаги:" -ForegroundColor $ColorInfo
Write-Host "   1. Проверьте сгенерированные файлы" -ForegroundColor $ColorInfo
Write-Host "   2. Запустите Maven build: mvn compile" -ForegroundColor $ColorInfo
Write-Host "   3. Создайте ServiceImpl классы вручную (если нужно)" -ForegroundColor $ColorInfo
Write-Host ""

# Выход с соответствующим кодом
exit $FailedFiles

