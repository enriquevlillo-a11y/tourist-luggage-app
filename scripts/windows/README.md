# Windows Startup Scripts

These scripts work on Windows 10/11 with PowerShell or Command Prompt.

## 🚀 Available Scripts

### PowerShell Scripts (Recommended)

#### 1. `start-app.ps1`
Full-featured startup script with colored output and comprehensive checks.

**Usage:**
```powershell
# First time only - allow script execution
Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser

# Start the app (right-click PowerShell, run as Administrator recommended)
.\start-app.ps1
```

#### 2. `stop-app.ps1`
Stops all services gracefully.

**Usage:**
```powershell
.\stop-app.ps1
```

### Batch Scripts (Alternative)

#### 3. `start-app.bat`
Simple batch file version for Command Prompt.

**Usage:**
```batch
REM Double-click or run from cmd:
start-app.bat
```

#### 4. `stop-app.bat`
Stops all services.

**Usage:**
```batch
stop-app.bat
```

## 📋 Prerequisites

- Java 21
- Maven 3.6+
- Node.js 18+
- npm 9+
- Docker Desktop for Windows

**Quick Install (Using Chocolatey):**
```powershell
# Install Chocolatey first: https://chocolatey.org/install
choco install openjdk21 maven nodejs docker-desktop
```

**Manual Downloads:**
- [Java 21](https://adoptium.net/)
- [Maven](https://maven.apache.org/download.cgi)
- [Node.js](https://nodejs.org/)
- [Docker Desktop](https://www.docker.com/products/docker-desktop)

## 🔧 What Gets Started

1. PostgreSQL Database (Docker container on port 5432)
2. Spring Boot Backend API (port 8081)
3. Expo Frontend Development Server

Each service opens in a separate window so you can monitor logs.

## 📝 Important Notes

### PowerShell Execution Policy

If you get an error about execution policies:

```powershell
# Option 1: Allow for current user (recommended)
Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser

# Option 2: Bypass for single script
PowerShell -ExecutionPolicy Bypass -File .\start-app.ps1
```

### Windows Defender / Antivirus

The first time you run these scripts, Windows Defender might scan them. This is normal - just allow the scripts to run.

### Path Separators

Windows uses backslashes (`\`) in paths. The scripts handle this automatically.

### Running as Administrator

While not strictly required, running PowerShell as Administrator provides:
- Better Docker integration
- Cleaner process management
- Fewer permission prompts

## 💡 Tips

**Create Desktop Shortcuts:**

1. Right-click on Desktop → New → Shortcut
2. For location, enter:
   ```
   powershell.exe -ExecutionPolicy Bypass -File "C:\path\to\tourist-luggage-app\scripts\windows\start-app.ps1"
   ```
3. Name it "Start Luggage App"
4. Repeat for stop script

**Quick Access:**

Add the scripts folder to your PATH or create aliases:

```powershell
# Add to PowerShell profile ($PROFILE)
function Start-LuggageApp {
    & "C:\path\to\tourist-luggage-app\scripts\windows\start-app.ps1"
}

function Stop-LuggageApp {
    & "C:\path\to\tourist-luggage-app\scripts\windows\stop-app.ps1"
}
```

## 🐛 Troubleshooting

**Port already in use:**
```powershell
# Find what's using port 8081
Get-NetTCPConnection -LocalPort 8081

# Kill process by PID
Stop-Process -Id <PID> -Force
```

**Docker not starting:**
```powershell
# Make sure Docker Desktop is running
Get-Process "Docker Desktop"

# Or start it from Start menu
```

**Scripts won't run (execution policy):**
```powershell
# Check current policy
Get-ExecutionPolicy

# Set to RemoteSigned
Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser
```
