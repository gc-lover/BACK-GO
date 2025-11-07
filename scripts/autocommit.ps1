# Autocommit script for agents
# Usage: .\autocommit.ps1 [commit message]

param(
    [string]$CommitMessage = "Auto commit: agent updates"
)

$RepoRoot = git rev-parse --show-toplevel 2>$null
if (-not $RepoRoot) {
    Write-Host "Error: no git repository detected" -ForegroundColor Red
    exit 1
}

Set-Location $RepoRoot

$Status = git status --porcelain
if (-not $Status) {
    Write-Host "Nothing to commit" -ForegroundColor Yellow
    exit 0
}

Write-Host "Staging changes..." -ForegroundColor Cyan
git add -A

if ($CommitMessage -eq "Auto commit: agent updates") {
    $ChangedFiles = git diff --cached --name-only
    if ($ChangedFiles) {
        $Action = "Update"
        if ($ChangedFiles | Where-Object { $_ -match "\.md$" }) {
            $Action = "Docs"
        } elseif ($ChangedFiles | Where-Object { $_ -match "\.(yaml|yml)$" }) {
            $Action = "API"
        } elseif ($ChangedFiles | Where-Object { $_ -match "\.(java|js|ts|py)$" }) {
            $Action = "Code"
        } elseif ($ChangedFiles | Where-Object { $_ -match "rules\.mdc$" }) {
            $Action = "Rules"
        }

        $FileCount = ($ChangedFiles | Measure-Object).Count
        $CommitMessage = "${Action}: ${FileCount} files"
    }
}

Write-Host "Creating commit: $CommitMessage" -ForegroundColor Cyan
$CommitResult = git commit -m $CommitMessage 2>&1

if ($LASTEXITCODE -ne 0) {
    Write-Host "Commit failed: $CommitResult" -ForegroundColor Red
    exit 1
}

Write-Host "Commit created" -ForegroundColor Green

Write-Host "Pushing to origin/main..." -ForegroundColor Cyan
$PushResult = git push origin main 2>&1

if ($LASTEXITCODE -ne 0) {
    Write-Host "Push failed: $PushResult" -ForegroundColor Yellow
    Write-Host "Changes committed locally" -ForegroundColor Yellow
} else {
    Write-Host "Push succeeded" -ForegroundColor Green
}

exit 0

