# Tourist Luggage App - Windows Stop Script (PowerShell)

# Colors
function Write-ColorOutput($Message, $Color) {
    Write-Host $Message -ForegroundColor $Color
}

function Write-Header($Message) {
    Write-Host ""
    Write-Host "═══════════════════════════════════════════════════" -ForegroundColor Cyan
    Write-Host "🧳  $Message" -ForegroundColor Cyan
    Write-Host "═══════════════════════════════════════════════════" -ForegroundColor Cyan
    Write-Host ""
}

Write-Header "Stopping Tourist Luggage App"

# Get project root (go up two levels from scripts/windows/)
$ProjectRoot = Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path)
$BackendDir = Join-Path $ProjectRoot "luggage-backend\luggage-backend"

# Stop backend (find and close PowerShell windows or kill Java processes)
Write-ColorOutput "ℹ️  Stopping backend server..." "Cyan"

# Option 1: Close windows by title
$backendWindows = Get-Process | Where-Object { $_.MainWindowTitle -like "*Luggage Backend*" }
if ($backendWindows) {
    $backendWindows | ForEach-Object { Stop-Process -Id $_.Id -Force }
    Write-ColorOutput "✅ Backend stopped" "Green"
}
else {
    # Option 2: Kill Maven/Java processes
    $javaProcesses = Get-Process -Name "java" -ErrorAction SilentlyContinue | 
                     Where-Object { $_.CommandLine -like "*spring-boot*" }
    if ($javaProcesses) {
        $javaProcesses | Stop-Process -Force
        Write-ColorOutput "✅ Backend processes stopped" "Green"
    }
    else {
        Write-ColorOutput "ℹ️  No backend process found" "Cyan"
    }
}

# Stop frontend
Write-ColorOutput "ℹ️  Stopping frontend server..." "Cyan"

$frontendWindows = Get-Process | Where-Object { $_.MainWindowTitle -like "*Luggage Frontend*" }
if ($frontendWindows) {
    $frontendWindows | ForEach-Object { Stop-Process -Id $_.Id -Force }
    Write-ColorOutput "✅ Frontend stopped" "Green"
}
else {
    # Kill Expo/Metro processes
    Get-Process -Name "node" -ErrorAction SilentlyContinue | 
        Where-Object { $_.CommandLine -like "*expo*" -or $_.CommandLine -like "*metro*" } |
        Stop-Process -Force -ErrorAction SilentlyContinue
    Write-ColorOutput "ℹ️  No frontend process found" "Cyan"
}

# Ask about database
Write-Host ""
$stopDb = Read-Host "Do you want to stop the PostgreSQL database? (y/N)"

if ($stopDb -eq "y" -or $stopDb -eq "Y") {
    Push-Location $BackendDir
    
    Write-ColorOutput "ℹ️  Stopping PostgreSQL container..." "Cyan"
    docker-compose stop
    Write-ColorOutput "✅ PostgreSQL stopped" "Green"
    
    Write-Host ""
    $removeDb = Read-Host "Do you want to remove the database container and data? (y/N)"
    
    if ($removeDb -eq "y" -or $removeDb -eq "Y") {
        Write-ColorOutput "⚠️  Removing database container and volumes..." "Yellow"
        docker-compose down -v
        Write-ColorOutput "✅ Database removed (all data deleted)" "Green"
    }
    
    Pop-Location
}
else {
    Write-ColorOutput "ℹ️  PostgreSQL container left running" "Cyan"
}

Write-Header "🛑 Services Stopped"

# Show status
$pgRunning = docker ps | Select-String "luggage-postgres"
if ($pgRunning) {
    Write-Host "✅ PostgreSQL: " -ForegroundColor Green -NoNewline
    Write-Host "Still running"
}
else {
    Write-Host "⏹  PostgreSQL: " -ForegroundColor Yellow -NoNewline
    Write-Host "Stopped"
}

# Check if anything is still using port 8081
$portInUse = Get-NetTCPConnection -LocalPort 8081 -ErrorAction SilentlyContinue
if ($portInUse) {
    Write-ColorOutput "⚠️  Warning: Something is still using port 8081" "Yellow"
}
else {
    Write-Host "✅ Backend: " -ForegroundColor Green -NoNewline
    Write-Host "Stopped"
}

Write-Host ""
Write-ColorOutput "✅ Cleanup complete!" "Green"
Write-Host ""
Write-Host "Press any key to exit..."
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")
