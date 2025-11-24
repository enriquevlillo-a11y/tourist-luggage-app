
# 🚀 Startup Scripts for Tourist Luggage App

This directory contains convenient scripts to start and stop the entire application with a single command.

**Available for both macOS/Linux and Windows!**

## � Directory Structure

```
scripts/
├── README.md              # This file
├── unix/                  # macOS, Linux, Unix-like systems
│   ├── README.md          # Unix-specific instructions
│   ├── start-app.sh       # Full startup script
│   ├── stop-app.sh        # Stop script
│   └── dev-start.sh       # Quick dev start
└── windows/               # Windows 10/11
    ├── README.md          # Windows-specific instructions
    ├── start-app.ps1      # PowerShell startup (recommended)
    ├── start-app.bat      # Batch startup
    ├── stop-app.ps1       # PowerShell stop
    └── stop-app.bat       # Batch stop
```

## 🎯 Quick Start by Platform

### macOS / Linux / Unix

```bash
cd scripts/unix

# First time only - make scripts executable
chmod +x start-app.sh stop-app.sh dev-start.sh

# Start the application
./start-app.sh

# When done
./stop-app.sh
```

**See [unix/README.md](unix/README.md) for detailed instructions.**

### Windows (PowerShell)

```powershell
cd scripts\windows

# First time only - allow script execution
Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser

# Start the application
.\start-app.ps1

# When done
.\stop-app.ps1
```

**See [windows/README.md](windows/README.md) for detailed instructions.**

### Windows (Command Prompt)

```batch
cd scripts\windows
start-app.bat
```

## 🎯 Which Script Should I Use?

### macOS/Linux:
- **First time user?** → `unix/start-app.sh`
- **Experienced developer?** → `unix/dev-start.sh`
- **Need to stop?** → `unix/stop-app.sh`

### Windows:
- **Have PowerShell?** → `windows/start-app.ps1` (recommended - better colors and features)
- **Only Command Prompt?** → `windows/start-app.bat`
- **Need to stop?** → `windows/stop-app.ps1` or `windows/stop-app.bat`

---

## 📋 Prerequisites

Before running any script, ensure you have:

- ✅ **Java 21** - `java -version`
- ✅ **Maven 3.6+** - `mvn -v`
- ✅ **Node.js 18+** - `node -v`
- ✅ **npm 9+** - `npm -v`
- ✅ **Docker Desktop** - `docker --version`

### macOS Quick Install
```bash
brew install openjdk@21 maven node docker
```

### Windows Quick Install
**Using Chocolatey (recommended):**
```powershell
# Install Chocolatey first from: https://chocolatey.org/install

choco install openjdk21 maven nodejs docker-desktop
```

**Or download manually:**
- Java 21: https://adoptium.net/
- Maven: https://maven.apache.org/download.cgi
- Node.js: https://nodejs.org/
- Docker Desktop: https://www.docker.com/products/docker-desktop

---

## 🔧 What Each Script Starts

### Services Started:
1. **PostgreSQL Database**
   - Port: 5432
   - Container: `luggage-postgres`
   - Credentials: luggo/luggo

2. **Spring Boot Backend**
   - Port: 8081
   - URL: http://localhost:8081
   - Logs: `app.log`

3. **Expo Frontend**
   - Development server
   - Platform: iOS/Android/Web (your choice)

---

## 📊 Monitoring

### View Backend Logs
```bash
tail -f app.log
```

### Check Service Status
```bash
# Backend
curl http://localhost:8081/api/locations

# Database
docker ps | grep luggage-postgres

# Ports
lsof -i :8081  # Backend
lsof -i :5432  # Database
```

### Database Access
```bash
# Connect to database
docker exec -it luggage-postgres psql -U luggo -d luggage-backend

# Common queries
\dt                # List tables
SELECT COUNT(*) FROM users;
SELECT COUNT(*) FROM locations;
SELECT COUNT(*) FROM bookings;
```

---

## 🐛 Troubleshooting

### Port Conflicts

**Backend port 8081 in use:**
```bash
lsof -i :8081
kill -9 <PID>
```

**Database port 5432 in use:**
```bash
# Stop local PostgreSQL
brew services stop postgresql@14

# Or kill the process
lsof -i :5432
kill -9 <PID>
```

### Services Won't Start

**Clean restart:**
```bash
# Stop everything
./stop-app.sh

# Remove database (will delete all data!)
cd luggage-backend/luggage-backend
docker-compose down -v
docker-compose up -d

# Clear caches
cd ../../luggage-frontend
rm -rf node_modules .expo
npm install

# Start fresh
cd ../..
./start-app.sh
```

### Database Issues

**Reset database:**
```bash
cd luggage-backend/luggage-backend

# Stop and remove (deletes data!)
docker-compose down -v

# Start fresh
docker-compose up -d

# Flyway will recreate schema on next backend start
```

---

## 🎨 Platform Selection

When running `start-app.sh`, you'll be prompted to choose a platform:

### Option 1: iOS Simulator (macOS only)
- Opens iOS Simulator automatically
- Best for iOS development
- **Requirement**: Xcode installed

### Option 2: Android Emulator
- Opens Android Emulator automatically
- Best for Android development
- **Requirement**: Android Studio with emulator configured

### Option 3: Web Browser
- Opens in your default browser
- Good for quick testing
- Limited functionality compared to mobile

### Option 4: Manual (QR Code)
- Starts server only
- Scan QR code with Expo Go app
- Best for testing on physical device

---

## 📝 Logs and Output

### Backend Logs
Located at: `app.log`

```bash
# View in real-time
tail -f app.log

# Search for errors
grep ERROR app.log

# View last 100 lines
tail -n 100 app.log
```

### Frontend Logs
Displayed in the terminal where you started the script.

### Database Logs
```bash
docker-compose logs -f postgres
```

---

## 🔄 Development Workflow

### Typical Day:

```bash
# Morning: Start everything
./start-app.sh

# Make changes to code...
# Backend: Auto-reloads with spring-boot-devtools
# Frontend: Auto-reloads with Metro bundler

# Lunch break: Keep running or stop
./stop-app.sh  # Optional

# Afternoon: Continue or restart
./start-app.sh

# End of day: Stop everything
./stop-app.sh
```

### Working on Backend Only:

```bash
# Start just database and backend
cd luggage-backend/luggage-backend
docker-compose up -d
mvn spring-boot:run
```

### Working on Frontend Only:

```bash
# Assumes backend is running at localhost:8081
cd luggage-frontend
npx expo start
```

---

## 🚨 Important Notes

1. **First Run**: The first time you run `start-app.sh`, it will:
   - Build the backend (takes a few minutes)
   - Install frontend dependencies (takes a few minutes)
   - Create database schema

2. **Subsequent Runs**: Much faster (under 30 seconds)

3. **Data Persistence**: Database data persists between runs unless you explicitly remove the container with `-v` flag

4. **Background Processes**: Scripts run services in background. Always use `stop-app.sh` or Ctrl+C to stop properly

5. **Logs**: Backend logs are saved to `app.log` for troubleshooting

---

## 🔐 Security Note

These scripts use default development credentials:
- Database: `luggo/luggo`
- JWT Secret: Auto-generated

**⚠️ Never use these scripts in production!** They are for local development only.

---

## 📚 Additional Resources

- **Setup Guide PDF**: `setup_guide.pdf`
- **Quick Reference**: `.gemini/antigravity/brain/*/quick_reference.md`
- **Architecture Diagrams**: `.gemini/antigravity/brain/*/architecture_diagram.md`

---

## 💡 Pro Tips

1. **Alias for convenience:**
   ```bash
   # Add to ~/.zshrc or ~/.bashrc
   alias luggage-start="cd ~/path/to/tourist-luggage-app && ./start-app.sh"
   alias luggage-stop="cd ~/path/to/tourist-luggage-app && ./stop-app.sh"
   ```

2. **Keep terminal organized:**
   - Use separate terminal tabs for different services
   - Or use tmux/screen for session management

3. **Monitor with watch:**
   ```bash
   watch -n 2 'docker ps && echo && lsof -i :8081 && lsof -i :5432'
   ```

---

**Created**: 2024-11-24  
**Last Updated**: 2024-11-24  
**Version**: 1.0.0
