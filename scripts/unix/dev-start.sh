#!/bin/bash

# Quick Development Start Script
# For experienced developers who want minimal output

PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
BACKEND_DIR="$PROJECT_ROOT/luggage-backend/luggage-backend"
FRONTEND_DIR="$PROJECT_ROOT/luggage-frontend"

echo "🧳 Starting Tourist Luggage App..."

# Start database
cd "$BACKEND_DIR"
docker-compose up -d 2>/dev/null
echo "✅ Database started"

# Start backend
mvn spring-boot:run > /dev/null 2>&1 &
echo "✅ Backend starting on http://localhost:8081"

# Wait a bit for backend
sleep 10

# Start frontend
cd "$FRONTEND_DIR"
npx expo start > /dev/null 2>&1 &
echo "✅ Frontend starting"

echo ""
echo "🎉 All services started!"
echo ""
echo "Backend: http://localhost:8081"
echo "Frontend: Check terminal for QR code"
echo ""
echo "To stop: run ./stop-app.sh or press Ctrl+C"

wait
