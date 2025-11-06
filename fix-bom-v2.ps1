# Fix BOM - read as bytes and remove BOM
$files = Get-ChildItem -Path "src\main\java\com\necpgame\backjava" -Filter "*.java" -Recurse

$fixedCount = 0
foreach ($file in $files) {
    try {
        # Read file as bytes
        $bytes = [System.IO.File]::ReadAllBytes($file.FullName)
        
        # Check for UTF-8 BOM (EF BB BF)
        if ($bytes.Length -ge 3 -and $bytes[0] -eq 0xEF -and $bytes[1] -eq 0xBB -and $bytes[2] -eq 0xBF) {
            # Remove first 3 bytes (BOM) and write back
            $newBytes = $bytes[3..($bytes.Length-1)]
            [System.IO.File]::WriteAllBytes($file.FullName, $newBytes)
            $fixedCount++
            Write-Host "Fixed BOM: $($file.Name)"
        }
    }
    catch {
        Write-Host "Error: $($file.Name)" -ForegroundColor Red
    }
}

Write-Host "`n✅ Fixed $fixedCount files" -ForegroundColor Green

