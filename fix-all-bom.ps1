# Fix BOM in all generated Java files
$Utf8NoBomEncoding = New-Object System.Text.UTF8Encoding $False

# Find all Java files in model and api directories
$files = Get-ChildItem -Path "src\main\java\com\necpgame\backjava" -Filter "*.java" -Recurse | 
    Where-Object { $_.FullName -match "(\\model\\|\\api\\)" }

$fixedCount = 0
foreach ($file in $files) {
    try {
        # Read file content
        $content = Get-Content $file.FullName -Raw -ErrorAction Stop
        
        # Check if file starts with BOM
        if ($content.Length -gt 0 -and $content[0] -eq [char]0xFEFF) {
            # Remove BOM and write back
            $content = $content.TrimStart([char]0xFEFF)
            [System.IO.File]::WriteAllText($file.FullName, $content, $Utf8NoBomEncoding)
            $fixedCount++
            Write-Host "Fixed: $($file.Name)"
        }
    }
    catch {
        Write-Host "Error processing $($file.Name): $_" -ForegroundColor Red
    }
}

Write-Host "`nDone! Fixed $fixedCount files with BOM" -ForegroundColor Green

