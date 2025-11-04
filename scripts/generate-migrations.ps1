# Скрипт для генерации Flyway миграций из JPA Entities
# Используется для автоматической генерации SQL миграций из Entity классов

$ErrorActionPreference = "Stop"

$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$projectRoot = Split-Path -Parent $scriptDir
$entitiesDir = Join-Path $projectRoot "target\generated-sources\entities\src\main\java\com\necpgame\backjava\entity"
$migrationsDir = Join-Path $projectRoot "src\main\resources\db\migration"

# Создаем директорию для миграций если её нет
New-Item -ItemType Directory -Force -Path $migrationsDir | Out-Null

Write-Host "=== Генерация Flyway миграций из JPA Entities ===" -ForegroundColor Cyan
Write-Host "Entities: $entitiesDir"
Write-Host "Migrations: $migrationsDir"
Write-Host ""

# Маппинг Java типов в SQL типы
function Map-JavaTypeToSql {
    param([string]$javaType)
    
    switch ($javaType) {
        "UUID" { return "UUID" }
        "String" { return "VARCHAR(255)" }
        "Integer" { return "INTEGER" }
        "Long" { return "BIGINT" }
        "Boolean" { return "BOOLEAN" }
        "OffsetDateTime" { return "TIMESTAMP" }
        "LocalDateTime" { return "TIMESTAMP" }
        "LocalDate" { return "DATE" }
        "LocalTime" { return "TIME" }
        "BigDecimal" { return "DECIMAL(19,2)" }
        default { return "VARCHAR(255)" }
    }
}

# Функция для преобразования camelCase в snake_case
function ConvertTo-SnakeCase {
    param([string]$text)
    
    $result = $text -creplace '([a-z])([A-Z])', '$1_$2'
    return $result.ToLower()
}

# Функция для определения, является ли поле ID
function Test-IsIdField {
    param([string]$fieldName)
    
    return $fieldName -eq "id"
}

# Функция для определения, является ли поле обязательным
function Test-IsRequired {
    param([string]$fieldContent, [string]$fieldName)
    
    # Проверяем наличие @NotNull или requiredMode = Schema.RequiredMode.REQUIRED
    if ($fieldContent -match "@NotNull" -or $fieldContent -match "requiredMode = Schema.RequiredMode.REQUIRED") {
        return $true
    }
    
    # Некоторые поля по умолчанию обязательны
    if ($fieldName -eq "id" -or $fieldName -eq "createdAt" -or $fieldName -eq "isActive") {
        return $true
    }
    
    return $false
}

# Функция для определения, является ли поле уникальным
function Test-IsUnique {
    param([string]$fieldContent)
    
    # Проверяем наличие @Email (для email полей) или других уникальных ограничений
    if ($fieldContent -match "@Email" -or $fieldContent -match "unique\s*=\s*true") {
        return $true
    }
    
    return $false
}

# Получаем все Entity файлы
$entityFiles = Get-ChildItem -Path $entitiesDir -Filter "*.java" -ErrorAction SilentlyContinue | Where-Object { $_.Name -notmatch "Entity\.java$" -and $_.Name -notmatch "Error" }

if (-not $entityFiles) {
    Write-Host "⚠️  Entity файлы не найдены в $entitiesDir" -ForegroundColor Yellow
    Write-Host "Сначала запустите: mvn clean generate-sources" -ForegroundColor Yellow
    exit 0
}

# Версия для миграций (начинаем с 1)
$version = 1

foreach ($entityFile in $entityFiles) {
    $entityContent = Get-Content $entityFile.FullName -Raw
    $className = [System.IO.Path]::GetFileNameWithoutExtension($entityFile.Name)
    
    # Пропускаем вспомогательные классы
    if ($className -match "Error|Details|Inner|Subclass|StartingResources|Summary|Appearance") {
        continue
    }
    
    Write-Host "Обработка: $className" -ForegroundColor Green
    
    # Извлекаем имя таблицы (snake_case от имени класса)
    $tableName = ConvertTo-SnakeCase $className
    if (-not $tableName.EndsWith("s")) {
        $tableName = $tableName + "s"
    }
    
    # Извлекаем поля из Entity
    $fields = @()
    # Используем простой паттерн для поиска объявлений полей
    $fieldPattern = "private\s+(\w+(?:<\w+>)?)\s+(\w+)(?:\s*=\s*[^;]+)?;"
    $fieldMatches = [regex]::Matches($entityContent, $fieldPattern)
    
    Write-Host "  Найдено совпадений: $($fieldMatches.Count)" -ForegroundColor Gray
    
    foreach ($match in $fieldMatches) {
        $javaType = $match.Groups[1].Value
        $fieldName = $match.Groups[2].Value
        
        Write-Host "    Обработка поля: $javaType $fieldName" -ForegroundColor Gray
        
        # Пропускаем только служебные поля
        if ($fieldName -eq "toIndentedString") {
            Write-Host "      Пропущено (служебное поле)" -ForegroundColor Gray
            continue
        }
        
        # Пропускаем если это не поле (может быть метод)
        $matchLine = $entityContent.Substring($match.Index, [Math]::Min(200, $entityContent.Length - $match.Index))
        if ($matchLine -match "\(") {
            continue
        }
    
        # Извлекаем контекст поля для проверки аннотаций (ищем геттер/сеттер)
        $getterPattern = "public\s+$javaType\s+get" + [char]::ToUpper($fieldName[0]) + $fieldName.Substring(1) + "\s*\("
        $fieldContext = ""
        if ($entityContent -match $getterPattern) {
            $getterStart = $entityContent.IndexOf($matches[0])
            $fieldContext = $entityContent.Substring([Math]::Max(0, $getterStart - 200), [Math]::Min(400, $entityContent.Length - $getterStart + 200))
        } else {
            # Если геттер не найден, используем контекст вокруг объявления поля
            $contextStart = [Math]::Max(0, $match.Index - 200)
            $contextEnd = [Math]::Min($entityContent.Length, $match.Index + 200)
            $fieldContext = $entityContent.Substring($contextStart, $contextEnd - $contextStart)
        }
        
        $sqlType = Map-JavaTypeToSql $javaType
        $columnName = ConvertTo-SnakeCase $fieldName
        $isId = Test-IsIdField $fieldName
        $isRequired = Test-IsRequired $fieldContext $fieldName
        $isUnique = Test-IsUnique $fieldContext
        
        # Специальная обработка для email
        if ($fieldName -eq "email") {
            $isUnique = $true
        }
        
        $fields += @{
            Name = $fieldName
            ColumnName = $columnName
            JavaType = $javaType
            SqlType = $sqlType
            IsId = $isId
            IsRequired = $isRequired
            IsUnique = $isUnique
        }
    }
    
    Write-Host "  Всего полей после фильтрации: $($fields.Count)" -ForegroundColor Gray
    
    if ($fields.Count -eq 0) {
        Write-Host "  ⚠️  Поля не найдены, пропускаем" -ForegroundColor Yellow
        continue
    }
    
    # Генерируем SQL миграцию
    $migrationFileName = "V{0:D3}__Create_{1}.sql" -f $version, $className
    $migrationPath = Join-Path $migrationsDir $migrationFileName
    
    $sql = "-- Migration для $className`n"
    $sql += "-- Generated from JPA Entity: $className.java`n"
    $sql += "-- Auto-generated on $(Get-Date -Format 'yyyy-MM-dd HH:mm:ss')`n`n"
    $sql += "CREATE TABLE IF NOT EXISTS $tableName (`n"
    
    $columns = @()
    foreach ($field in $fields) {
        $columnDef = "    "
        
        if ($field.IsId) {
            $columnDef += "$($field.ColumnName) UUID PRIMARY KEY DEFAULT gen_random_uuid()"
        } else {
            $columnDef += "$($field.ColumnName) $($field.SqlType)"
            
            if ($field.IsRequired) {
                $columnDef += " NOT NULL"
            }
            
            if ($field.IsUnique) {
                $columnDef += " UNIQUE"
            }
        }
        
        $columns += $columnDef
    }
    
    # Добавляем стандартные поля created_at и updated_at если их нет
    $hasCreatedAt = $fields | Where-Object { $_.ColumnName -eq "created_at" }
    $hasUpdatedAt = $fields | Where-Object { $_.ColumnName -eq "updated_at" }
    
    if (-not $hasCreatedAt) {
        $columns += "    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP"
    }
    
    if (-not $hasUpdatedAt) {
        $columns += "    updated_at TIMESTAMP"
    }
    
    $sql += ($columns -join ",`n") + "`n"
    $sql += ");`n`n"
    
    # Добавляем индексы для уникальных полей
    foreach ($field in $fields) {
        if ($field.IsUnique -and -not $field.IsId) {
            $indexName = "idx_${tableName}_$($field.ColumnName)"
            $sql += "CREATE UNIQUE INDEX IF NOT EXISTS $indexName ON $tableName($($field.ColumnName));`n"
        }
    }
    
    # Сохраняем миграцию
    $sql | Out-File -FilePath $migrationPath -Encoding UTF8 -NoNewline
    
    Write-Host "  ✅ Создана миграция: $migrationFileName" -ForegroundColor Green
    $version++
}

Write-Host "`n=== Генерация завершена ===" -ForegroundColor Green
Write-Host "Миграции созданы в: $migrationsDir"
Write-Host "Всего создано миграций: $($version - 1)"

