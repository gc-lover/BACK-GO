# Скрипт для генерации репозиториев на основе сгенерированных entities
$entitiesDir = "$PSScriptRoot\..\target\generated-sources\entities\src\main\java\com\necpgame\backjava\entity"
$repositoriesDir = "$PSScriptRoot\..\target\generated-sources\repositories\src\main\java\com\necpgame\backjava\repository"

# Создаем директорию для репозиториев
New-Item -ItemType Directory -Force -Path $repositoriesDir | Out-Null

# Список Entity классов, для которых нужно генерировать репозитории
$entityClasses = @("Account", "Character", "CharacterClass", "CharacterOrigin", "City", "Faction")

foreach ($entityClass in $entityClasses) {
    $entityFile = Join-Path $entitiesDir "$entityClass.java"
    if (Test-Path $entityFile) {
        $repositoryFile = Join-Path $repositoriesDir "${entityClass}Repository.java"
        
        $repositoryContent = @"
package com.necpgame.backjava.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.necpgame.backjava.entity.$entityClass;
import java.util.Optional;
import java.util.UUID;
import java.util.List;

/**
 * Repository для $entityClass
 */
@Repository
public interface ${entityClass}Repository extends JpaRepository<$entityClass, UUID> {
    // Методы для поиска по email (если есть поле email)
    $(
        if ($entityClass -eq "Account") {
            "`n    Optional<$entityClass> findByEmail(String email);`n    boolean existsByEmail(String email);"
        }
    )
}
"@
        
        Set-Content -Path $repositoryFile -Value $repositoryContent
        Write-Host "Generated repository: $repositoryFile"
    }
}

Write-Host "Repository generation completed!"
