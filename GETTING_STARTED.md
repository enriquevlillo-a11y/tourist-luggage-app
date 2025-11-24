# 🧳 Tourist Luggage App - Getting Started

Welcome! This guide will help you get the Tourist Luggage App running on your machine.

## 📁 Project Structure

```
tourist-luggage-app/
├── luggage-backend/          # Spring Boot backend
├── luggage-frontend/         # React Native (Expo) frontend
├── scripts/                  # Startup scripts
│   ├── unix/                # macOS, Linux scripts
│   └── windows/             # Windows scripts
├── setup_guide.pdf          # Complete setup documentation
└── README.md                # This file
```

## 🚀 Quick Start

### Step 1: Choose Your Platform

**macOS / Linux / Unix:**
```bash
cd scripts/unix
chmod +x *.sh
./start-app.sh
```

**Windows (PowerShell):**
```powershell
cd scripts\windows
Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser
.\start-app.ps1
```

**Windows (Command Prompt):**
```batch
cd scripts\windows
start-app.bat
```

### Step 2: Select Platform

When prompted, choose:
- **1** for iOS Simulator (macOS only)
- **2** for Android Emulator
- **3** for Web Browser
- **4** to scan QR code with Expo Go on physical device

### Step 3: Start Developing!

The app will automatically:
1. ✅ Start PostgreSQL database
2. ✅ Build and launch backend API on http://localhost:8081
3. ✅ Start frontend development server
4. ✅ Open your chosen platform

## 📚 Documentation

### For Detailed Setup Instructions:
See [setup_guide.pdf](./setup_guide.pdf) - comprehensive guide with:
- Prerequisites installation
- Detailed setup steps for both frontend and backend
- Configuration options
- Troubleshooting
- API documentation

### For Script Usage:
See [scripts/README.md](./scripts/README.md) - startup scripts documentation

### Platform-Specific:
- Unix/Linux/macOS: [scripts/unix/README.md](./scripts/unix/README.md)
- Windows: [scripts/windows/README.md](./scripts/windows/README.md)

## 📋 Prerequisites

Before you start, make sure you have:

- ☑️ **Java 21** - Backend runtime
- ☑️ **Maven 3.6+** - Backend build tool
- ☑️ **Node.js 18+** - Frontend runtime
- ☑️ **npm 9+** - Package manager
- ☑️ **Docker Desktop** - Database container

### Quick Install

**macOS (Homebrew):**
```bash
brew install openjdk@21 maven node docker
```

**Windows (Chocolatey):**
```powershell
choco install openjdk21 maven nodejs docker-desktop
```

**Linux (Ubuntu/Debian):**
```bash
sudo apt update
sudo apt install openjdk-21-jdk maven nodejs npm docker.io
```

## 🎯 What This App Does

The **Tourist Luggage App** is a mobile solution for travelers to find secure luggage storage locations in cities. Users can:

- 📍 Find nearby storage locations on a map
- 🔍 Search and filter by price, capacity, and location
- 📅 Book storage by hour or day
- ⭐ Read and leave reviews
- 💳 Make secure payments (Stripe integration planned)

### Tech Stack

**Frontend:**
- React Native 0.81.4
- Expo 54.0.13
- Zustand (state management)
- React Native Maps

**Backend:**
- Spring Boot 3.5.6
- Java 21
- PostgreSQL 14
- JWT Authentication
- Flyway Migrations

**Infrastructure:**
- Docker (PostgreSQL)
- Maven (backend build)
- npm (frontend dependencies)

## 🔧 Running Manually (Without Scripts)

If you prefer to run services individually:

### Database:
```bash
cd luggage-backend/luggage-backend
docker-compose up -d
```

### Backend:
```bash
cd luggage-backend/luggage-backend
mvn spring-boot:run
# Runs on http://localhost:8081
```

### Frontend:
```bash
cd luggage-frontend
npm install
npx expo start
```

## 🛑 Stopping the App

**Using scripts:**
```bash
# macOS/Linux
cd scripts/unix
./stop-app.sh

# Windows PowerShell
cd scripts\windows
.\stop-app.ps1

# Windows Batch
cd scripts\windows
stop-app.bat
```

**Manually:**
- Press Ctrl+C in each terminal window
- Stop Docker: `docker-compose stop`

## 🌐 Accessing the App

Once running:

- **Backend API**: http://localhost:8081
- **API Documentation**: http://localhost:8081/api/locations (test endpoint)
- **Frontend**: Opens automatically based on your platform choice
- **Database**: localhost:5432 (credentials: luggo/luggo)

## 🆘 Need Help?

### Common Issues:

**Port already in use:**
```bash
# macOS/Linux
lsof -i :8081
kill -9 <PID>

# Windows PowerShell
Get-NetTCPConnection -LocalPort 8081
Stop-Process -Id <PID> -Force
```

**Docker not running:**
```bash
# Make sure Docker Desktop is running
docker ps
```

**Dependencies not installing:**
```bash
# Backend
cd luggage-backend/luggage-backend
mvn clean install

# Frontend
cd luggage-frontend
rm -rf node_modules
npm install
```

### Getting More Help:

1. Check [setup_guide.pdf](./setup_guide.pdf) for detailed troubleshooting
2. See platform-specific READMEs in `scripts/unix/` or `scripts/windows/`
3. Review logs in `app.log` (backend logs)
4. Contact the development team

## 👥 Team

**Team Lead:** Enrique Vázquez Lillo (evazq084@fiu.edu)

**Developers:**
- Andres Linares
- Kevin Pluas
- Daniel Reyes
- John Valdespino

## 📖 Contributing

See [CONTRIBUTING.md](./CONTRIBUTING.md) for contribution guidelines.

## 📄 License

[Add license information here]

---

**Ready to start?** Run the appropriate startup script for your platform and you'll be coding in minutes! 🚀
