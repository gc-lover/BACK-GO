# Copy and fix package for Quests DTOs
$sourceDir = "target\generated-sources\openapi\src\main\java\org\openapitools"
$targetModelDir = "src\main\java\com\necpgame\backjava\model"
$targetApiDir = "src\main\java\com\necpgame\backjava\api"

# DTOs to copy (excluding Error models - already exist)
$dtos = @(
    "AbandonQuest200Response.java",
    "AbandonQuestRequest.java",
    "AcceptQuest200Response.java",
    "AcceptQuestRequest.java",
    "CompleteQuest200Response.java",
    "CompleteQuestRequest.java",
    "GetActiveQuests200Response.java",
    "GetAvailableQuests200Response.java",
    "GetQuestObjectives200Response.java",
    "Quest.java",
    "QuestObjective.java",
    "QuestProgress.java",
    "QuestRequirements.java",
    "QuestRewards.java",
    "QuestRewardsItemsInner.java"
)

# Copy DTOs
foreach ($dto in $dtos) {
    $sourcePath = Join-Path "$sourceDir\model" $dto
    $targetPath = Join-Path $targetModelDir $dto
    
    if (Test-Path $sourcePath) {
        $content = Get-Content $sourcePath -Raw -Encoding UTF8
        $content = $content -replace 'package org\.openapitools\.model;', 'package com.necpgame.backjava.model;'
        $content = $content -replace 'import org\.openapitools\.model\.', 'import com.necpgame.backjava.model.'
        Set-Content -Path $targetPath -Value $content -Encoding UTF8
        Write-Host "Copied: $dto"
    }
}

# Copy API Interface
$apiFile = "QuestsApi.java"
$sourcePath = Join-Path "$sourceDir\api" $apiFile
$targetPath = Join-Path $targetApiDir $apiFile

if (Test-Path $sourcePath) {
    $content = Get-Content $sourcePath -Raw -Encoding UTF8
    $content = $content -replace 'package org\.openapitools\.api;', 'package com.necpgame.backjava.api;'
    $content = $content -replace 'import org\.openapitools\.model\.', 'import com.necpgame.backjava.model.'
    $content = $content -replace 'import org\.openapitools\.api\.', 'import com.necpgame.backjava.api.'
    Set-Content -Path $targetPath -Value $content -Encoding UTF8
    Write-Host "Copied: $apiFile"
}

Write-Host "`nDone! Copied 15 DTOs + 1 API Interface"

