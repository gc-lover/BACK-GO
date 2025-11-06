# Copy and fix package for Actions & Inventory DTOs
$sourceDir = "target\generated-sources\openapi\src\main\java\org\openapitools"
$targetModelDir = "src\main\java\com\necpgame\backjava\model"
$targetApiDir = "src\main\java\com\necpgame\backjava\api"

# Actions DTOs (8 DTOs)
$actionsDtos = @(
    "ExploreLocation200Response.java",
    "ExploreLocationRequest.java",
    "HackSystem200Response.java",
    "HackSystemRequest.java",
    "RestAction200Response.java",
    "RestActionRequest.java",
    "UseObject200Response.java",
    "UseObjectRequest.java"
)

# Inventory DTOs (14 DTOs - excluding Error models)
$inventoryDtos = @(
    "DropItem200Response.java",
    "EquipItem200Response.java",
    "EquipRequest.java",
    "EquipmentSlot.java",
    "GetEquipment200Response.java",
    "InventoryItem.java",
    "InventoryItemRequirements.java",
    "InventoryResponse.java",
    "ItemCategory.java",
    "UnequipItem200Response.java",
    "UnequipItemRequest.java",
    "UseItem200Response.java",
    "UseItem200ResponseEffectsInner.java",
    "UseItemRequest.java"
)

$allDtos = $actionsDtos + $inventoryDtos

# Copy all DTOs
$count = 0
foreach ($dto in $allDtos) {
    $sourcePath = Join-Path "$sourceDir\model" $dto
    $targetPath = Join-Path $targetModelDir $dto
    
    if (Test-Path $sourcePath) {
        $content = Get-Content $sourcePath -Raw -Encoding UTF8
        $content = $content -replace 'package org\.openapitools\.model;', 'package com.necpgame.backjava.model;'
        $content = $content -replace 'import org\.openapitools\.model\.', 'import com.necpgame.backjava.model.'
        Set-Content -Path $targetPath -Value $content -Encoding UTF8
        $count++
    }
}
Write-Host "Copied $count DTOs"

# Copy API Interfaces
$apis = @("GameplayApi.java", "InventoryApi.java")
foreach ($api in $apis) {
    $sourcePath = Join-Path "$sourceDir\api" $api
    $targetPath = Join-Path $targetApiDir $api
    
    if (Test-Path $sourcePath) {
        $content = Get-Content $sourcePath -Raw -Encoding UTF8
        $content = $content -replace 'package org\.openapitools\.api;', 'package com.necpgame.backjava.api;'
        $content = $content -replace 'import org\.openapitools\.model\.', 'import com.necpgame.backjava.model.'
        $content = $content -replace 'import org\.openapitools\.api\.', 'import com.necpgame.backjava.api.'
        Set-Content -Path $targetPath -Value $content -Encoding UTF8
        Write-Host "Copied: $api"
    }
}

Write-Host "`nDone! Actions (8 DTOs) + Inventory (14 DTOs) + 2 API Interfaces = 24 files"

