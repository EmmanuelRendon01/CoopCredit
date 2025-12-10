#!/bin/bash

# CoopCredit Frontend - Quick Start Script

echo "🚀 CoopCredit Frontend - Quick Start"
echo "===================================="
echo ""

# Check if backend is running
echo "📡 Checking backend connection..."
if curl -s http://localhost:8080/actuator/health > /dev/null 2>&1; then
    echo "✅ Backend is running on http://localhost:8080"
else
    echo "⚠️  Backend is NOT running on http://localhost:8080"
    echo "   Please start the backend first:"
    echo "   cd ../credit-application-service"
    echo "   mvn spring-boot:run"
    echo ""
fi

echo ""
echo "Choose how to run the frontend:"
echo ""
echo "1) Python HTTP Server (port 3000)"
echo "2) Docker (port 3000)"
echo "3) Just open in browser"
echo ""
read -p "Enter option (1-3): " option

case $option in
    1)
        echo ""
        echo "🌐 Starting Python HTTP Server on port 3000..."
        echo "   Access at: http://localhost:3000"
        echo "   Press Ctrl+C to stop"
        echo ""
        python3 -m http.server 3000
        ;;
    2)
        echo ""
        echo "🐳 Building and running Docker container..."
        docker build -t coopcredit-frontend .
        docker run -d -p 3000:80 --name coopcredit-frontend coopcredit-frontend
        echo ""
        echo "✅ Frontend running in Docker"
        echo "   Access at: http://localhost:3000"
        echo "   Stop with: docker stop coopcredit-frontend"
        echo "   Remove with: docker rm coopcredit-frontend"
        ;;
    3)
        echo ""
        echo "🌐 Opening in browser..."
        if command -v xdg-open > /dev/null; then
            xdg-open index.html
        elif command -v open > /dev/null; then
            open index.html
        else
            echo "Please open index.html manually in your browser"
        fi
        ;;
    *)
        echo "Invalid option"
        exit 1
        ;;
esac
