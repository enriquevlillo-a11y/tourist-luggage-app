#!/bin/bash

# Tourist Luggage App - Stop Script
# This script stops all running services

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

print_info() {
    echo -e "${BLUE}ℹ️  $1${NC}"
}

print_success() {
    echo -e "${GREEN}✅ $1${NC}"
}

print_warning() {
    echo -e "${YELLOW}⚠️  $1${NC}"
}

print_header() {
    echo -e "\n${BLUE}═══════════════════════════════════════════════════${NC}"
    echo -e "${BLUE}🧳  $1${NC}"
    echo -e "${BLUE}═══════════════════════════════════════════════════${NC}\n"
}

PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
BACKEND_DIR="$PROJECT_ROOT/luggage-backend/luggage-backend"

main() {
    print_header "Stopping Tourist Luggage App"
    
    # Stop backend (Spring Boot)
    print_info "Stopping backend server..."
    pkill -f "spring-boot" 2>/dev/null && print_success "Backend stopped" || print_info "No backend process found"
    
    # Stop frontend (Expo/Metro)
    print_info "Stopping frontend server..."
    pkill -f "expo" 2>/dev/null && print_success "Frontend stopped" || print_info "No frontend process found"
    pkill -f "metro" 2>/dev/null || true
    
    # Stop database (optional)
    echo ""
    read -p "Do you want to stop the PostgreSQL database? (y/N): " stop_db
    
    if [[ $stop_db =~ ^[Yy]$ ]]; then
        cd "$BACKEND_DIR"
        print_info "Stopping PostgreSQL container..."
        docker-compose stop
        print_success "PostgreSQL stopped"
        
        echo ""
        read -p "Do you want to remove the database container and data? (y/N): " remove_db
        
        if [[ $remove_db =~ ^[Yy]$ ]]; then
            print_warning "Removing database container and volumes..."
            docker-compose down -v
            print_success "Database removed (all data deleted)"
        fi
    else
        print_info "PostgreSQL container left running"
    fi
    
    print_header "🛑 Services Stopped"
    
    # Show remaining processes
    if docker ps | grep -q luggage-postgres; then
        echo -e "${GREEN}✅ PostgreSQL:${NC} Still running"
    else
        echo -e "${YELLOW}⏹  PostgreSQL:${NC} Stopped"
    fi
    
    if lsof -i :8081 >/dev/null 2>&1; then
        echo -e "${YELLOW}⚠️  Warning:${NC} Something is still using port 8081"
    else
        echo -e "${GREEN}✅ Backend:${NC} Stopped"
    fi
    
    echo ""
    print_success "Cleanup complete!"
}

main "$@"
