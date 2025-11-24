@echo off
REM Tourist Luggage App - Windows Stop Script

echo ================================================================
echo    Stopping Tourist Luggage App
echo ================================================================
echo.

REM Stop backend (Java/Maven processes)
echo Stopping backend server...
taskkill /F /FI "WINDOWTITLE eq Luggage Backend*" >nul 2>&1
if %errorlevel% equ 0 (
    echo [OK] Backend stopped
) else (
    echo [INFO] No backend window found
)

REM Alternative: Kill all Java processes (use with caution)
REM taskkill /F /IM java.exe >nul 2>&1

REM Stop frontend (Node/Expo processes)
echo Stopping frontend server...
taskkill /F /FI "WINDOWTITLE eq Luggage Frontend*" >nul 2>&1
if %errorlevel% equ 0 (
    echo [OK] Frontend stopped
) else (
    echo [INFO] No frontend window found
)

echo.
set /p "stop_db=Do you want to stop the PostgreSQL database? (y/N): "

if /i "%stop_db%"=="y" (
    cd /d "%~dp0..\..\luggage-backend\luggage-backend"
    echo Stopping PostgreSQL container...
    docker-compose stop
    echo [OK] PostgreSQL stopped
    
    echo.
    set /p "remove_db=Do you want to remove the database container and data? (y/N): "
    
    if /i "%remove_db%"=="y" (
        echo [WARNING] Removing database container and volumes...
        docker-compose down -v
        echo [OK] Database removed (all data deleted)
    )
) else (
    echo [INFO] PostgreSQL container left running
)

echo.
echo ================================================================
echo    Services Stopped
echo ================================================================
echo.

REM Check Docker status
docker ps | find "luggage-postgres" >nul 2>&1
if %errorlevel% equ 0 (
    echo [OK] PostgreSQL: Still running
) else (
    echo [STOPPED] PostgreSQL: Stopped
)

echo.
echo Cleanup complete!
echo.
pause
