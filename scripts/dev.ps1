param(
  [int]$FrontendPort = 5500
)

$ErrorActionPreference = "Stop"
$root = Split-Path -Parent $PSScriptRoot

Write-Host "Starting backend..."
Start-Process -FilePath "$root\backend\mvnw.cmd" -ArgumentList "spring-boot:run" -WorkingDirectory "$root\backend"

Write-Host "Starting frontend static server on port $FrontendPort..."
Start-Process -FilePath "python" -ArgumentList "-m", "http.server", "$FrontendPort" -WorkingDirectory "$root\frontend"

Write-Host "Frontend: http://localhost:$FrontendPort/index.html"
Write-Host "Backend:  http://localhost:8080"
