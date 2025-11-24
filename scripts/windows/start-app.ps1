# Tourist Luggage App - Windows Startup Script (PowerShell)
# Requires PowerShell 5.1 or higher

param(
    [switch]$SkipChecks = $false
)

# Colors
$Colors = @{
    Info = "Cyan"
    Success = "Green"
    Warning = "Yellow"
    Error = "Red"
}

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

function Test-Command($Command) {
    try {
        if (Get-Command $Command -ErrorAction Stop) {
            return $true
        }
    }
    catch {
        return $false
    }
}

function Test-Port($Port) {
    $connections = Get-NetTCPConnection -LocalPort $Port -ErrorAction SilentlyContinue
    return $connections.Count -gt 0
}

function Wait-ForService($Url, $MaxAttempts = 30) {
    Write-ColorOutput "ℹ️  Waiting for service at $Url..." $Colors.Info
    
    for ($i = 0; $i -lt $MaxAttempts; $i++) {
        try {
            $response = Invoke-WebRequest -Uri $Url -UseBasicParsing -TimeoutSec 2 -ErrorAction Stop
            Write-Host ""
            return $true
        }
        catch {
            Start-Sleep -Seconds 1
            Write-Host "." -NoNewline
        }
    }
    
    Write-Host ""
    return $false
}

# Main script
Write-Header "Tourist Luggage App - Startup Script"

# Get project directories (go up two levels from scripts/windows/)
$ProjectRoot = Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path)
$BackendDir = Join-Path $ProjectRoot "luggage-backend\luggage-backend"
$FrontendDir = Join-Path $ProjectRoot "luggage-frontend"

if (-not $SkipChecks) {
    Write-Header "Step 1: Checking Prerequisites"
    
    # Check Docker
    if (-not (Test-Command "docker")) {
        Write-ColorOutput "❌ Docker is not installed or not in PATH" $Colors.Error
        Write-ColorOutput "   Please install Docker Desktop for Windows" $Colors.Error
        exit 1
    }
    Write-ColorOutput "✅ Docker found" $Colors.Success
    
    # Check Maven
    if (-not (Test-Command "mvn")) {
        Write-ColorOutput "❌ Maven is not installed or not in PATH" $Colors.Error
        Write-ColorOutput "   Please install Maven 3.6+" $Colors.Error
        exit 1
    }
    $mvnVersion = & mvn -v | Select-String "Apache Maven" | Out-String
    Write-ColorOutput "✅ Maven found $($mvnVersion.Trim())" $Colors.Success
    
    # Check Node.js
    if (-not (Test-Command "node")) {
        Write-ColorOutput "❌ Node.js is not installed or not in PATH" $Colors.Error
        Write-ColorOutput "   Please install Node.js 18+" $Colors.Error
        exit 1
    }
    $nodeVersion = & node -v
    Write-ColorOutput "✅ Node.js found ($nodeVersion)" $Colors.Success
    
    # Check npm
    if (-not (Test-Command "npm")) {
        Write-ColorOutput "❌ npm is not installed" $Colors.Error
        exit 1
    }
    $npmVersion = & npm -v
    Write-ColorOutput "✅ npm found ($npmVersion)" $Colors.Success
    
    # Check Java
    if (-not (Test-Command "java")) {
        Write-ColorOutput "❌ Java is not installed or not in PATH" $Colors.Error
        Write-ColorOutput "   Please install Java 21" $Colors.Error
        exit 1
    }
    $javaVersion = & java -version 2>&1 | Select-String "version" | Out-String
    Write-ColorOutput "✅ Java found $($javaVersion.Trim())" $Colors.Success
    
    Write-Header "Step 2: Checking Port Availability"
    
    if (Test-Port 8081) {
        Write-ColorOutput "❌ Port 8081 is already in use" $Colors.Error
        Write-ColorOutput "   Run: Get-NetTCPConnection -LocalPort 8081" $Colors.Info
        exit 1
    }
    Write-ColorOutput "✅ Port 8081 is available (Backend API)" $Colors.Success
    
    if (Test-Port 5432) {
        Write-ColorOutput "⚠️  Port 5432 is in use (checking if it's our PostgreSQL container...)" $Colors.Warning
        $pgContainer = docker ps | Select-String "luggage-postgres"
        if ($pgContainer) {
            Write-ColorOutput "✅ PostgreSQL container is already running" $Colors.Success
        }
        else {
            Write-ColorOutput "❌ Port 5432 is in use by another process" $Colors.Error
            exit 1
        }
    }
}

Write-Header "Step 3: Starting PostgreSQL Database"

Push-Location $BackendDir

$pgRunning = docker ps | Select-String "luggage-postgres"
if (-not $pgRunning) {
    Write-ColorOutput "ℹ️  Starting PostgreSQL container..." $Colors.Info
    docker-compose up -d
    
    Write-ColorOutput "ℹ️  Waiting for PostgreSQL to be ready..." $Colors.Info
    Start-Sleep -Seconds 5
    
    $pgReady = docker exec luggage-postgres pg_isready -U luggo 2>$null
    if ($LASTEXITCODE -eq 0) {
        Write-ColorOutput "✅ PostgreSQL is ready" $Colors.Success
    }
    else {
        Write-ColorOutput "❌ PostgreSQL failed to start properly" $Colors.Error
        Pop-Location
        exit 1
    }
}
else {
    Write-ColorOutput "✅ PostgreSQL container is already running" $Colors.Success
}

Write-Header "Step 4: Preparing Backend"

$jarPath = Join-Path $BackendDir "target\luggage-backend-0.0.1-SNAPSHOT.jar"
if (-not (Test-Path $jarPath)) {
    Write-ColorOutput "ℹ️  Backend JAR not found. Building with Maven..." $Colors.Info
    mvn clean package -DskipTests
    if ($LASTEXITCODE -eq 0) {
        Write-ColorOutput "✅ Backend built successfully" $Colors.Success
    }
    else {
        Write-ColorOutput "❌ Backend build failed" $Colors.Error
        Pop-Location
        exit 1
    }
}
else {
    Write-ColorOutput "ℹ️  Using existing backend JAR" $Colors.Info
}

Write-Header "Step 5: Starting Backend Server"

Write-ColorOutput "ℹ️  Starting Spring Boot backend on port 8081..." $Colors.Info

# Start backend in new PowerShell window
$backendScript = "cd '$BackendDir'; mvn spring-boot:run"
Start-Process powershell -ArgumentList "-NoExit", "-Command", $backendScript

Write-ColorOutput "ℹ️  Backend starting in new window..." $Colors.Info

# Wait for backend
if (Wait-ForService "http://localhost:8081/api/locations") {
    Write-ColorOutput "✅ Backend is ready and responding" $Colors.Success
}
else {
    Write-ColorOutput "❌ Backend failed to start" $Colors.Error
    Pop-Location
    exit 1
}

Pop-Location

Write-Header "Step 6: Preparing Frontend"

Push-Location $FrontendDir

if (-not (Test-Path "node_modules")) {
    Write-ColorOutput "ℹ️  Installing frontend dependencies..." $Colors.Info
    npm install
    Write-ColorOutput "✅ Dependencies installed" $Colors.Success
}
else {
    Write-ColorOutput "ℹ️  Frontend dependencies already installed" $Colors.Info
}

Write-Header "Step 7: Starting Frontend Development Server"

Write-ColorOutput "ℹ️  Starting Expo development server..." $Colors.Info
Write-Host ""
Write-ColorOutput "Choose platform to launch:" $Colors.Warning
Write-Host "  1) Web Browser"
Write-Host "  2) Android Emulator"
Write-Host "  3) Just start server (scan QR code manually)"
Write-Host ""
$platformChoice = Read-Host "Enter choice [1-3]"

switch ($platformChoice) {
    "1" {
        Write-ColorOutput "ℹ️  Starting Web Browser..." $Colors.Info
        Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$FrontendDir'; npx expo start --web"
    }
    "2" {
        Write-ColorOutput "ℹ️  Starting Android Emulator..." $Colors.Info
        Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$FrontendDir'; npx expo start --android"
    }
    "3" {
        Write-ColorOutput "ℹ️  Starting Expo server only..." $Colors.Info
        Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$FrontendDir'; npx expo start"
    }
    default {
        Write-ColorOutput "⚠️  Invalid choice. Starting server only..." $Colors.Warning
        Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$FrontendDir'; npx expo start"
    }
}

Pop-Location

Write-Header "🎉 Application Started Successfully!"

Write-Host "✅ PostgreSQL Database: " -ForegroundColor Green -NoNewline
Write-Host "Running (port 5432)"

Write-Host "✅ Backend API: " -ForegroundColor Green -NoNewline
Write-Host "http://localhost:8081"

Write-Host "✅ Frontend: " -ForegroundColor Green -NoNewline
Write-Host "Running on Expo"

Write-Host ""
Write-ColorOutput "📝 Useful Commands:" $Colors.Info
Write-Host "   Test backend: Invoke-WebRequest http://localhost:8081/api/locations"
Write-Host "   Stop services: .\stop-app.ps1"
Write-Host "   Database CLI: docker exec -it luggage-postgres psql -U luggo -d luggage-backend"
Write-Host ""
Write-ColorOutput "Check the opened PowerShell windows for logs and QR code" $Colors.Warning
Write-Host ""
Write-Host "Press any key to exit this window (services will keep running)..."
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")
