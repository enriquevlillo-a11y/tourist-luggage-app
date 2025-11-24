#!/bin/bash

# Tourist Luggage App - Complete Startup Script
# This script starts the database, backend, and frontend in the correct order

set -e  # Exit on error

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Project root directory (go up two levels from scripts/unix/)
PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
BACKEND_DIR="$PROJECT_ROOT/luggage-backend/luggage-backend"
FRONTEND_DIR="$PROJECT_ROOT/luggage-frontend"

# Log file for backend
LOG_FILE="$PROJECT_ROOT/app.log"

# Function to print colored messages
print_info() {
    echo -e "${BLUE}ℹ️  $1${NC}"
}

print_success() {
    echo -e "${GREEN}✅ $1${NC}"
}

print_warning() {
    echo -e "${YELLOW}⚠️  $1${NC}"
}

print_error() {
    echo -e "${RED}❌ $1${NC}"
}

print_header() {
    echo -e "\n${BLUE}═══════════════════════════════════════════════════${NC}"
    echo -e "${BLUE}🧳  $1${NC}"
    echo -e "${BLUE}═══════════════════════════════════════════════════${NC}\n"
}

# Function to check if a command exists
command_exists() {
    command -v "$1" >/dev/null 2>&1
}

# Function to check if a port is in use
port_in_use() {
    lsof -i :"$1" >/dev/null 2>&1
}

# Function to wait for a service to be ready
wait_for_service() {
    local url=$1
    local max_attempts=30
    local attempt=0
    
    print_info "Waiting for service at $url..."
    
    while [ $attempt -lt $max_attempts ]; do
        if curl -s "$url" >/dev/null 2>&1; then
            return 0
        fi
        attempt=$((attempt + 1))
        sleep 1
        echo -n "."
    done
    
    echo ""
    return 1
}

# Cleanup function
cleanup() {
    print_warning "Shutting down services..."
    
    # Kill background processes
    jobs -p | xargs -r kill 2>/dev/null
    
    print_info "Services stopped"
    exit 0
}

# Set up trap for cleanup
trap cleanup SIGINT SIGTERM

# Main startup function
main() {
    print_header "Tourist Luggage App - Startup Script"
    
    # Step 1: Check prerequisites
    print_header "Step 1: Checking Prerequisites"
    
    print_info "Checking required tools..."
    
    if ! command_exists docker; then
        print_error "Docker is not installed. Please install Docker Desktop."
        exit 1
    fi
    print_success "Docker found"
    
    if ! command_exists mvn; then
        print_error "Maven is not installed. Please install Maven 3.6+."
        exit 1
    fi
    print_success "Maven found ($(mvn -v | head -n 1))"
    
    if ! command_exists node; then
        print_error "Node.js is not installed. Please install Node.js 18+."
        exit 1
    fi
    print_success "Node.js found ($(node -v))"
    
    if ! command_exists npm; then
        print_error "npm is not installed. Please install npm."
        exit 1
    fi
    print_success "npm found ($(npm -v))"
    
    # Check Java version
    if ! command_exists java; then
        print_error "Java is not installed. Please install Java 21."
        exit 1
    fi
    
    java_version=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | cut -d. -f1)
    if [ "$java_version" -lt 21 ]; then
        print_warning "Java version $java_version detected. Java 21 is recommended."
    else
        print_success "Java found (version $java_version)"
    fi
    
    # Step 2: Check for port conflicts
    print_header "Step 2: Checking Port Availability"
    
    if port_in_use 8081; then
        print_error "Port 8081 is already in use. Please free up this port."
        print_info "Run: lsof -i :8081 to see what's using it"
        exit 1
    fi
    print_success "Port 8081 is available (Backend API)"
    
    if port_in_use 5432; then
        print_warning "Port 5432 is in use. Checking if it's our PostgreSQL container..."
        if docker ps | grep -q luggage-postgres; then
            print_success "PostgreSQL container is already running"
        else
            print_error "Port 5432 is in use by another process. Please free it up."
            print_info "If you have local PostgreSQL: brew services stop postgresql@14"
            exit 1
        fi
    fi
    
    # Step 3: Start PostgreSQL Database
    print_header "Step 3: Starting PostgreSQL Database"
    
    cd "$BACKEND_DIR"
    
    if ! docker ps | grep -q luggage-postgres; then
        print_info "Starting PostgreSQL container..."
        docker-compose up -d
        
        # Wait for PostgreSQL to be ready
        print_info "Waiting for PostgreSQL to be ready..."
        sleep 5
        
        # Test database connection
        if docker exec luggage-postgres pg_isready -U luggo >/dev/null 2>&1; then
            print_success "PostgreSQL is ready"
        else
            print_error "PostgreSQL failed to start properly"
            exit 1
        fi
    else
        print_success "PostgreSQL container is already running"
    fi
    
    # Step 4: Build Backend (if needed)
    print_header "Step 4: Preparing Backend"
    
    if [ ! -f "$BACKEND_DIR/target/luggage-backend-0.0.1-SNAPSHOT.jar" ]; then
        print_info "Backend JAR not found. Building with Maven..."
        mvn clean package -DskipTests
        print_success "Backend built successfully"
    else
        print_info "Using existing backend JAR"
        print_warning "To rebuild, delete target/ directory and run this script again"
    fi
    
    # Step 5: Start Backend
    print_header "Step 5: Starting Backend Server"
    
    print_info "Starting Spring Boot backend on port 8081..."
    
    # Start backend in background and redirect output to log file
    mvn spring-boot:run > "$LOG_FILE" 2>&1 &
    BACKEND_PID=$!
    
    print_info "Backend PID: $BACKEND_PID"
    print_info "Backend logs: tail -f $LOG_FILE"
    
    # Wait for backend to be ready
    if wait_for_service "http://localhost:8081/api/locations"; then
        print_success "Backend is ready and responding"
    else
        print_error "Backend failed to start. Check logs: $LOG_FILE"
        exit 1
    fi
    
    # Step 6: Prepare Frontend
    print_header "Step 6: Preparing Frontend"
    
    cd "$FRONTEND_DIR"
    
    if [ ! -d "node_modules" ]; then
        print_info "Installing frontend dependencies..."
        npm install
        print_success "Dependencies installed"
    else
        print_info "Frontend dependencies already installed"
    fi
    
    # Step 7: Start Frontend
    print_header "Step 7: Starting Frontend Development Server"
    
    print_info "Starting Expo development server..."
    print_warning "The Expo DevTools will open in your browser"
    
    # Prompt user for platform choice
    echo ""
    echo -e "${YELLOW}Choose platform to launch:${NC}"
    echo "  1) iOS Simulator (macOS only)"
    echo "  2) Android Emulator"
    echo "  3) Web Browser"
    echo "  4) Just start server (scan QR code manually)"
    echo ""
    read -p "Enter choice [1-4]: " platform_choice
    
    case $platform_choice in
        1)
            print_info "Starting iOS Simulator..."
            npx expo start --ios &
            ;;
        2)
            print_info "Starting Android Emulator..."
            npx expo start --android &
            ;;
        3)
            print_info "Starting Web Browser..."
            npx expo start --web &
            ;;
        4)
            print_info "Starting Expo server only..."
            npx expo start &
            ;;
        *)
            print_warning "Invalid choice. Starting server only..."
            npx expo start &
            ;;
    esac
    
    FRONTEND_PID=$!
    
    # Final status
    print_header "🎉 Application Started Successfully!"
    
    echo -e "${GREEN}✅ PostgreSQL Database:${NC} Running (port 5432)"
    echo -e "${GREEN}✅ Backend API:${NC} http://localhost:8081"
    echo -e "${GREEN}✅ Frontend:${NC} Running on Expo"
    echo ""
    echo -e "${BLUE}📊 Process IDs:${NC}"
    echo "   Backend: $BACKEND_PID"
    echo "   Frontend: $FRONTEND_PID"
    echo ""
    echo -e "${BLUE}📝 Useful Commands:${NC}"
    echo "   View backend logs: tail -f $LOG_FILE"
    echo "   Stop all services: Press Ctrl+C"
    echo "   Database CLI: docker exec -it luggage-postgres psql -U luggo -d luggage-backend"
    echo ""
    echo -e "${YELLOW}Press Ctrl+C to stop all services${NC}"
    echo ""
    
    # Keep script running and wait for user interrupt
    wait
}

# Run main function
main "$@"
