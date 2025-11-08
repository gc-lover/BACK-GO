#!/usr/bin/env pwsh
param(
    [string]$ApiSpec = "",
    [string]$ApiDirectory = "",
    [switch]$Validate,
    [switch]$DryRun,
    [string]$Layers = "All"
)

$ErrorActionPreference = "Stop"

$ScriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$ProjectRoot = Split-Path -Parent $ScriptDir
Set-Location $ProjectRoot

function Resolve-PathOrThrow {
    param([string]$PathValue, [string]$ParameterName)
    if ([string]::IsNullOrEmpty($PathValue)) {
        return ""
    }
    if (-not (Test-Path $PathValue)) {
        throw "Путь не найден для ${ParameterName}: $PathValue"
    }
    return (Resolve-Path $PathValue).Path
}

$resolvedSpec = Resolve-PathOrThrow -PathValue $ApiSpec -ParameterName "-ApiSpec"
$resolvedDirectory = Resolve-PathOrThrow -PathValue $ApiDirectory -ParameterName "-ApiDirectory"

if ([string]::IsNullOrEmpty($resolvedSpec) -and [string]::IsNullOrEmpty($resolvedDirectory)) {
    throw "Укажите -ApiSpec или -ApiDirectory."
}

function Collect-ApiFiles {
    param([string]$Spec, [string]$Directory)
    if (-not [string]::IsNullOrEmpty($Spec)) {
        return @($Spec)
    }
    $yaml = Get-ChildItem -Path $Directory -Filter "*.yaml" -File -Recurse
    $yml = Get-ChildItem -Path $Directory -Filter "*.yml" -File -Recurse
    return ($yaml + $yml | Sort-Object FullName | ForEach-Object { $_.FullName })
}

$ApiFiles = Collect-ApiFiles -Spec $resolvedSpec -Directory $resolvedDirectory
if ($ApiFiles.Count -eq 0) {
    throw "OpenAPI файлы не найдены."
}

$Microservices = @{
    "auth-service" = @{
        package = "com.necpgame.authservice"
        sourceDir = Join-Path $ProjectRoot "microservices/auth-service/src/main/java"
    }
    "character-service" = @{
        package = "com.necpgame.characterservice"
        sourceDir = Join-Path $ProjectRoot "microservices/character-service/src/main/java"
    }
    "gameplay-service" = @{
        package = "com.necpgame.gameplayservice"
        sourceDir = Join-Path $ProjectRoot "microservices/gameplay-service/src/main/java"
    }
    "social-service" = @{
        package = "com.necpgame.socialservice"
        sourceDir = Join-Path $ProjectRoot "microservices/social-service/src/main/java"
    }
    "economy-service" = @{
        package = "com.necpgame.economyservice"
        sourceDir = Join-Path $ProjectRoot "microservices/economy-service/src/main/java"
    }
    "world-service" = @{
        package = "com.necpgame.worldservice"
        sourceDir = Join-Path $ProjectRoot "microservices/world-service/src/main/java"
    }
}

function Detect-MicroserviceFromPath {
    param([string]$Path)
    $normalized = $Path.Replace("\", "/").ToLowerInvariant()
    switch -Wildcard ($normalized) {
        "*/api/v1/auth/*" { return "auth-service" }
        "*/api/v1/characters/*" { return "character-service" }
        "*/api/v1/players/*" { return "character-service" }
        "*/api/v1/gameplay/*" { return "gameplay-service" }
        "*/api/v1/social/*" { return "social-service" }
        "*/api/v1/economy/*" { return "economy-service" }
        "*/api/v1/inventory/*" { return "economy-service" }
        "*/api/v1/trade/*" { return "economy-service" }
        "*/api/v1/world/*" { return "world-service" }
        "*/api/v1/locations/*" { return "world-service" }
        default { return "" }
    }
}

function Get-LineIndent {
    param([string]$Line)
    return ([regex]::Match($Line, "^\s*")).Value.Length
}

function Parse-MicroserviceBlock {
    param([string[]]$Lines)
    $result = @{}
    for ($i = 0; $i -lt $Lines.Length; $i++) {
        if ($Lines[$i] -match "^\s*x-microservice\s*:\s*$") {
            $baseIndent = Get-LineIndent -Line $Lines[$i]
            for ($j = $i + 1; $j -lt $Lines.Length; $j++) {
                $line = $Lines[$j]
                if ($line.Trim().Length -eq 0) {
                    continue
                }
                $indent = Get-LineIndent -Line $line
                if ($indent -le $baseIndent) {
                    break
                }
                if ($line -match "^\s*([A-Za-z0-9\-_]+)\s*:\s*(.+)$") {
                    $key = $matches[1].Trim()
                    $value = $matches[2].Trim()
                    if ($value.StartsWith('"') -and $value.EndsWith('"')) {
                        $value = $value.Trim('"')
                    }
                    $result[$key] = $value
                }
            }
            break
        }
    }
    return $result
}

function Read-MicroserviceMetadata {
    param([string]$FilePath)

    $metadata = @{
        name = ""
        package = ""
        autodetected = $false
    }

    try {
        $lines = Get-Content -Path $FilePath
        $block = Parse-MicroserviceBlock -Lines $lines
        if ($block.ContainsKey("name")) {
            $metadata.name = $block["name"]
        }
        if ($block.ContainsKey("package")) {
            $metadata.package = $block["package"]
        }
    } catch {
        Write-Warning "Не удалось прочитать YAML: $FilePath. Ошибка: $($_.Exception.Message)"
    }

    if ([string]::IsNullOrEmpty($metadata.name)) {
        $detected = Detect-MicroserviceFromPath -Path $FilePath
        if ([string]::IsNullOrEmpty($detected)) {
            throw "Не удалось определить микросервис для файла: $FilePath. Добавьте info.x-microservice.name."
        }
        $metadata.name = $detected
        $metadata.autodetected = $true
    }

    if ([string]::IsNullOrEmpty($metadata.package)) {
        if ($Microservices.ContainsKey($metadata.name)) {
            $metadata.package = $Microservices[$metadata.name].package
        } else {
            $suffix = ($metadata.name -replace "-", "")
            $metadata.package = "com.necpgame.$suffix"
        }
    }

    return $metadata
}

function Ensure-DirectoryExists {
    param([string]$PathValue)
    if (-not (Test-Path $PathValue)) {
        New-Item -ItemType Directory -Path $PathValue -Force | Out-Null
    }
}

function Run-Generator {
    param(
        [string]$InputFile,
        [string]$OutputDir,
        [string]$ApiPackage,
        [string]$ModelPackage,
        [string]$InvokerPackage,
        [string]$TemplateDir = "",
        [string]$AdditionalProperties = "",
        [string[]]$ExtraArgs = @()
    )

    Ensure-DirectoryExists -PathValue $OutputDir

    $arguments = @(
        "generate",
        "-i", $InputFile,
        "-g", "spring",
        "-o", $OutputDir,
        "--api-package", $ApiPackage,
        "--model-package", $ModelPackage,
        "--invoker-package", $InvokerPackage
    )

    if (-not [string]::IsNullOrEmpty($TemplateDir)) {
        $arguments += @("-t", $TemplateDir)
    }

    if ($ExtraArgs.Count -gt 0) {
        $arguments += $ExtraArgs
    }

    if (-not [string]::IsNullOrEmpty($AdditionalProperties)) {
        $arguments += @("-p", $AdditionalProperties)
    }

    Write-Host "    npx openapi-generator-cli $($arguments -join ' ')"
    $generatorOutput = & npx --yes @openapitools/openapi-generator-cli @arguments 2>&1
    if ($LASTEXITCODE -ne 0) {
        $errorMessage = "Генерация завершилась с ошибкой для файла $InputFile"
        if ($generatorOutput) {
            $errorMessage += "`n$($generatorOutput -join "`n")"
        }
        throw $errorMessage
    }
}

function Copy-GeneratedContent {
    param([string]$SourceRoot, [string]$TargetRoot)
    $sourceCom = Join-Path $SourceRoot "com"
    if (-not (Test-Path $sourceCom)) {
        throw "Не найдена директория $sourceCom после генерации."
    }
    Ensure-DirectoryExists -PathValue $TargetRoot
    $robocopyArgs = @($sourceCom, $TargetRoot, "/E", "/XC", "/XN", "/XO", "/NJH", "/NJS", "/NFL", "/NDL")
    $null = robocopy @robocopyArgs
    if ($LASTEXITCODE -gt 8) {
        throw "Ошибка копирования файлов из $sourceCom в $TargetRoot (robocopy code $LASTEXITCODE)."
    }
}

if ($Validate) {
    $validationScript = Join-Path (Split-Path $ProjectRoot -Parent) "API-SWAGGER/scripts/validate-openapi.ps1"
    if (-not (Test-Path $validationScript)) {
        throw "Скрипт валидации не найден: $validationScript"
    }
    $validationArgs = @("-NoProfile", "-File", $validationScript)
    if (-not [string]::IsNullOrEmpty($resolvedSpec)) {
        $validationArgs += @("-ApiSpec", $resolvedSpec)
    } else {
        $validationArgs += @("-ApiDirectory", $resolvedDirectory)
    }
    Write-Host "Запуск валидации OpenAPI..."
    pwsh @validationArgs
    if ($LASTEXITCODE -ne 0) {
        throw "Валидация OpenAPI завершилась с ошибками."
    }
}

Write-Host "Всего файлов для генерации: $($ApiFiles.Count)"

$Tasks = foreach ($file in $ApiFiles) {
    [ordered]@{
        FilePath = $file
        Metadata = Read-MicroserviceMetadata -FilePath $file
    }
}

$TempRoot = Join-Path $ProjectRoot "target/openapi-microservices"
Ensure-DirectoryExists -PathValue $TempRoot

$Failures = @()

foreach ($task in $Tasks) {
    $filePath = $task.FilePath
    $metadata = $task.Metadata
    $relative = [IO.Path]::GetRelativePath($ProjectRoot, $filePath)
    Write-Host "`n═══════════════════════════════════════════════════════════════"
    Write-Host "Файл: $relative"
    Write-Host "Микросервис: $($metadata.name)"
    if ($metadata.autodetected) {
        Write-Host "⚠ Автоопределение микросервиса (добавьте info.x-microservice для точности)" -ForegroundColor Yellow
    }

    $javaPackageRoot = $metadata.package
    $apiPackage = "$javaPackageRoot.api"
    $modelPackage = "$javaPackageRoot.model"
    $servicePackage = "$javaPackageRoot.service"
    $invokerPackage = "$javaPackageRoot.invoker"

    if (-not $Microservices.ContainsKey($metadata.name)) {
        Write-Host "⚠ Микросервис '$($metadata.name)' не найден в конфигурации!" -ForegroundColor Yellow
        return
    }
    
    $destinationRoot = $Microservices[$metadata.name].sourceDir

    Ensure-DirectoryExists -PathValue $destinationRoot

    $fileTempRoot = Join-Path $TempRoot ([Guid]::NewGuid().ToString())
    if (-not $DryRun) {
        try {
            if ($Layers -eq "All" -or $Layers -match "DTOs") {
                Write-Host "  → Генерация DTO и API интерфейсов"
                Run-Generator -InputFile $filePath `
                    -OutputDir (Join-Path $fileTempRoot "contracts") `
                    -ApiPackage $apiPackage `
                    -ModelPackage $modelPackage `
                    -InvokerPackage $invokerPackage `
                    -AdditionalProperties "interfaceOnly=true,delegatePattern=false,useSpringBoot3=true,useJakartaEe=true,useBeanValidation=true,hideGenerationTimestamp=true,sourceFolder=."

                Copy-GeneratedContent -SourceRoot (Join-Path $fileTempRoot "contracts") -TargetRoot $destinationRoot
            }

            if ($Layers -eq "All" -or $Layers -match "Services") {
                Write-Host "  → Генерация Service интерфейсов"
                Run-Generator -InputFile $filePath `
                    -OutputDir (Join-Path $fileTempRoot "services") `
                    -ApiPackage $servicePackage `
                    -ModelPackage $modelPackage `
                    -InvokerPackage $invokerPackage `
                    -TemplateDir (Join-Path $ProjectRoot "templates") `
                    -AdditionalProperties "interfaceOnly=true,generateApis=true,generateModels=false,useSpringBoot3=true,useJakartaEe=true,hideGenerationTimestamp=true,sourceFolder=." `
                    -ExtraArgs @("--api-name-suffix", "Service")

                Copy-GeneratedContent -SourceRoot (Join-Path $fileTempRoot "services") -TargetRoot $destinationRoot
            }
        } catch {
            $Failures += [ordered]@{
                file = $relative
                error = $_.Exception.Message
            }
            Write-Host "✗ Ошибка: $($_.Exception.Message)" -ForegroundColor Red
        } finally {
            if (Test-Path $fileTempRoot) {
                Remove-Item -Path $fileTempRoot -Recurse -Force
            }
        }
    } else {
        Write-Host "  (DryRun) Пропуск генерации для $relative"
    }
}

Write-Host "`n═══════════════════════════════════════════════════════════════"
Write-Host "Генерация завершена."

if ($Failures.Count -gt 0) {
    Write-Host "Ошибки:" -ForegroundColor Red
    foreach ($failure in $Failures) {
        Write-Host "  - $($failure.file): $($failure.error)" -ForegroundColor Red
    }
    exit 1
}

exit 0

