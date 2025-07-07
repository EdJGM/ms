#!/bin/bash

# Script para construir todos los microservicios

echo "=== Building Auction Microservices ==="

# Función para construir un microservicio
build_service() {
    local service_name=$1
    echo "Building $service_name..."
    
    if [ -d "$service_name" ]; then
        cd "$service_name"
        mvn clean package -DskipTests
        if [ $? -eq 0 ]; then
            echo "✅ $service_name built successfully"
        else
            echo "❌ Failed to build $service_name"
            exit 1
        fi
        cd ..
    else
        echo "⚠️  Directory $service_name not found"
    fi
}

# Construir todos los microservicios
services=("eureka-server" "api-gateway" "user-service" "auction-service" "bid-service" "notification-service")

for service in "${services[@]}"; do
    build_service "$service"
done

echo "=== All microservices built successfully ==="
echo "You can now run: docker-compose up -d"
