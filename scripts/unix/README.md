# Unix/Linux/macOS Startup Scripts

These scripts work on macOS, Linux, and any Unix-like system with bash.

## 🚀 Available Scripts

### 1. `start-app.sh` (Recommended)
Full-featured startup script with:
- Prerequisites checking
- Port validation
- Automatic database startup
- Backend build and launch
- Frontend development server
- Platform selection (iOS/Android/Web)

**Usage:**
```bash
# First time only - make executable
chmod +x start-app.sh

# Start the app
./start-app.sh
```

### 2. `stop-app.sh`
Stops all services gracefully.

**Usage:**
```bash
./stop-app.sh
```

### 3. `dev-start.sh`
Quick start with minimal output for experienced developers.

**Usage:**
```bash
chmod +x dev-start.sh
./dev-start.sh
```

## 📋 Prerequisites

- Java 21
- Maven 3.6+
- Node.js 18+
- npm 9+
- Docker Desktop

**Quick Install (macOS):**
```bash
brew install openjdk@21 maven node docker
```

**Quick Install (Linux - Ubuntu/Debian):**
```bash
sudo apt update
sudo apt install openjdk-21-jdk maven nodejs npm docker.io
```

## 🔧 What Gets Started

1. PostgreSQL Database (Docker container on port 5432)
2. Spring Boot Backend API (port 8081)
3. Expo Frontend Development Server

## 📝 Notes

- All scripts must be run from this directory (`scripts/unix/`)
- Backend logs are saved to `app.log` in project root
- First run takes longer (builds backend, installs dependencies)
- Subsequent runs are much faster

## 💡 Tips

**Alias for convenience:**
```bash
# Add to ~/.bashrc or ~/.zsh rc
alias luggage-start="cd ~/path/to/tourist-luggage-app/scripts/unix && ./start-app.sh"
alias luggage-stop="cd ~/path/to/tourist-luggage-app/scripts/unix && ./stop-app.sh"
```

**Run from anywhere:**
```bash
# From project root
./scripts/unix/start-app.sh

# Or use absolute path
/full/path/to/tourist-luggage-app/scripts/unix/start-app.sh
```
