# Replace javax with jakarta in generated files
$files = Get-ChildItem -Path "src\main\java\com\necpgame\backjava" -Filter "*.java" -Recurse | 
    Where-Object { $_.FullName -match "(\\model\\|\\api\\)" }

$fixedCount = 0
foreach ($file in $files) {
    try {
        $content = [System.IO.File]::ReadAllText($file.FullName)
        $originalContent = $content
        
        # Replace javax with jakarta
        $content = $content -replace 'import javax\.validation', 'import jakarta.validation'
        $content = $content -replace 'import javax\.annotation', 'import jakarta.annotation'
        $content = $content -replace 'package javax\.validation', 'package jakarta.validation'
        $content = $content -replace 'package javax\.annotation', 'package jakarta.annotation'
        
        if ($content -ne $originalContent) {
            $Utf8NoBomEncoding = New-Object System.Text.UTF8Encoding $False
            [System.IO.File]::WriteAllText($file.FullName, $content, $Utf8NoBomEncoding)
            $fixedCount++
            Write-Host "Fixed: $($file.Name)"
        }
    }
    catch {
        Write-Host "Error: $($file.Name)" -ForegroundColor Red
    }
}

Write-Host "`n✅ Fixed $fixedCount files (javax → jakarta)" -ForegroundColor Green

