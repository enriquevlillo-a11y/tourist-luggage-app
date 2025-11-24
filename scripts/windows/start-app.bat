@echo off
REM Tourist Luggage App - Windows Startup Script (Batch)
REM For better experience, use start-app.ps1 (PowerShell version)

setlocal enabledelayedexpansion

echo ================================================================
echo    Tourist Luggage App - Startup Script (Windows)
echo ================================================================
echo.

REM Get project root directory (go up two levels from scripts\windows)
set "PROJECT_ROOT=%~dp0..\.."
set "BACKEND_DIR=%PROJECT_ROOT%\luggage-backend\luggage-backend"
set "FRONTEND_DIR=%PROJECT_ROOT%\luggage-frontend"

echo Checking prerequisites...
echo.

REM Check Docker
where docker >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] Docker is not installed or not in PATH
    echo Please install Docker Desktop for Windows
    pause
    exit /b 1
)
echo [OK] Docker found

REM Check Maven
where mvn >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] Maven is not installed or not in PATH
    echo Please install Maven 3.6+
    pause
    exit /b 1
)
echo [OK] Maven found

REM Check Node.js
where node >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] Node.js is not installed or not in PATH
    echo Please install Node.js 18+
    pause
    exit /b 1
)
echo [OK] Node.js found

REM Check Java
where java >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] Java is not installed or not in PATH
    echo Please install Java 21
    pause
    exit /b 1
)
echo [OK] Java found

echo.
echo ================================================================
echo    Starting PostgreSQL Database
echo ================================================================
echo.

cd /d "%BACKEND_DIR%"

REM Check if container is running
docker ps | find "luggage-postgres" >nul 2>&1
if %errorlevel% neq 0 (
    echo Starting PostgreSQL container...
    docker-compose up -d
    timeout /t 5 /nobreak >nul
    echo [OK] PostgreSQL started
) else (
    echo [OK] PostgreSQL already running
)

echo.
echo ================================================================
echo    Starting Backend Server
echo ================================================================
echo.

REM Start backend in new window
start "Luggage Backend" cmd /k "cd /d %BACKEND_DIR% && mvn spring-boot:run"
echo [OK] Backend starting on http://localhost:8081
echo     Check the new window for backend logs

echo.
echo Waiting for backend to be ready...
timeout /t 15 /nobreak

echo.
echo ================================================================
echo    Starting Frontend Development Server
echo ================================================================
echo.

cd /d "%FRONTEND_DIR%"

REM Check if node_modules exists
if not exist "node_modules\" (
    echo Installing dependencies...
    call npm install
)

echo.
echo Choose platform to launch:
echo   1) Web Browser
echo   2) Android Emulator
echo   3) Just start server (scan QR code)
echo.
set /p "platform=Enter choice [1-3]: "

if "%platform%"=="1" (
    echo Starting Web Browser...
    start "Luggage Frontend" cmd /k "cd /d %FRONTEND_DIR% && npx expo start --web"
) else if "%platform%"=="2" (
    echo Starting Android Emulator...
    start "Luggage Frontend" cmd /k "cd /d %FRONTEND_DIR% && npx expo start --android"
) else (
    echo Starting Expo server...
    start "Luggage Frontend" cmd /k "cd /d %FRONTEND_DIR% && npx expo start"
)

echo.
echo ================================================================
echo    Application Started Successfully!
echo ================================================================
echo.
echo [OK] PostgreSQL Database: Running (port 5432)
echo [OK] Backend API: http://localhost:8081
echo [OK] Frontend: Running on Expo
echo.
echo Check the opened windows for logs and QR code
echo.
echo To stop all services, run: stop-app.bat
echo Or close the opened terminal windows
echo.
pause
