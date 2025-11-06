# Copy all 5 new APIs DTOs and API Interfaces
$Utf8NoBomEncoding = New-Object System.Text.UTF8Encoding $False
$targetModelDir = "src\main\java\com\necpgame\backjava\model"
$targetApiDir = "src\main\java\com\necpgame\backjava\api"

# Function to copy files
function Copy-GeneratedFiles {
    param(
        [string]$SourceDir,
        [string]$ApiName
    )
    
    Write-Host "`n=== $ApiName ===" -ForegroundColor Cyan
    
    # Copy DTOs (excluding Error models)
    $modelFiles = Get-ChildItem -Path "$SourceDir\src\main\java\org\openapitools\model" -Filter "*.java" -ErrorAction SilentlyContinue |
        Where-Object { $_.Name -notmatch "^Error" }
    
    $dtoCount = 0
    foreach ($file in $modelFiles) {
        $content = [System.IO.File]::ReadAllText($file.FullName)
        
        # Fix package
        $content = $content -replace 'package org\.openapitools\.model;', 'package com.necpgame.backjava.model;'
        $content = $content -replace 'import org\.openapitools\.model\.', 'import com.necpgame.backjava.model.'
        
        # Fix javax -> jakarta
        $content = $content -replace 'import javax\.validation', 'import jakarta.validation'
        $content = $content -replace 'import javax\.annotation', 'import jakarta.annotation'
        
        # Write without BOM
        $targetPath = Join-Path $targetModelDir $file.Name
        [System.IO.File]::WriteAllText($targetPath, $content, $Utf8NoBomEncoding)
        $dtoCount++
    }
    Write-Host "  DTOs: $dtoCount" -ForegroundColor Green
    
    # Copy API Interface
    $apiFiles = Get-ChildItem -Path "$SourceDir\src\main\java\org\openapitools\api" -Filter "*Api.java" -ErrorAction SilentlyContinue |
        Where-Object { $_.Name -ne "ApiUtil.java" }
    
    foreach ($file in $apiFiles) {
        $content = [System.IO.File]::ReadAllText($file.FullName)
        
        # Fix package
        $content = $content -replace 'package org\.openapitools\.api;', 'package com.necpgame.backjava.api;'
        $content = $content -replace 'import org\.openapitools\.model\.', 'import com.necpgame.backjava.model.'
        $content = $content -replace 'import org\.openapitools\.api\.', 'import com.necpgame.backjava.api.'
        
        # Fix javax -> jakarta
        $content = $content -replace 'import javax\.validation', 'import jakarta.validation'
        $content = $content -replace 'import javax\.annotation', 'import jakarta.annotation'
        
        # RENAME to avoid conflicts
        $newName = $file.Name
        if ($file.Name -eq "CharactersApi.java") {
            $newName = "CharactersStatusApi.java"
            $content = $content -replace 'public interface CharactersApi', 'public interface CharactersStatusApi'
        } elseif ($file.Name -eq "LocationsApi.java") {
            # Check if it's gameplay locations (has travel/actions) or reference locations (cities only)
            if ($content -match "travelToLocation|getLocationActions") {
                $newName = "GameplayLocationsApi.java"
                $content = $content -replace 'public interface LocationsApi', 'public interface GameplayLocationsApi'
            }
        }
        
        # Write without BOM
        $targetPath = Join-Path $targetApiDir $newName
        [System.IO.File]::WriteAllText($targetPath, $content, $Utf8NoBomEncoding)
        Write-Host "  API: $newName" -ForegroundColor Green
    }
}

# Copy all 5 APIs
Copy-GeneratedFiles -SourceDir "target\generated-sources\openapi" -ApiName "Characters Status API"
Copy-GeneratedFiles -SourceDir "target\generated-sources\openapi-combat" -ApiName "Combat API"
Copy-GeneratedFiles -SourceDir "target\generated-sources\openapi-locations" -ApiName "Locations API"
Copy-GeneratedFiles -SourceDir "target\generated-sources\openapi-trading" -ApiName "Trading API"
Copy-GeneratedFiles -SourceDir "target\generated-sources\openapi-events" -ApiName "Random Events API"

Write-Host "`n✅ Done! Copied all 5 APIs" -ForegroundColor Green

