# Fix BOM (Byte Order Mark) in generated files
$files = @(
    "src\main\java\com\necpgame\backjava\model\GetActiveQuests200Response.java",
    "src\main\java\com\necpgame\backjava\model\InventoryItem.java",
    "src\main\java\com\necpgame\backjava\model\NPC.java",
    "src\main\java\com\necpgame\backjava\model\UseItem200Response.java",
    "src\main\java\com\necpgame\backjava\api\NpcsApi.java"
)

foreach ($file in $files) {
    if (Test-Path $file) {
        # Read with BOM
        $content = Get-Content $file -Raw
        # Write without BOM
        $Utf8NoBomEncoding = New-Object System.Text.UTF8Encoding $False
        [System.IO.File]::WriteAllLines((Join-Path $PSScriptRoot $file), $content, $Utf8NoBomEncoding)
        Write-Host "Fixed: $file"
    }
}

Write-Host "`nDone! Fixed BOM in 5 files"

