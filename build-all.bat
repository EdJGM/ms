@echo off
REM Script para construir todos los microservicios en Windows

echo === Building Auction Microservices ===

REM Función para construir un microservicio
set "services=eureka-server api-gateway user-service auction-service bid-service notification-service"

for %%s in (%services%) do (
    echo Building %%s...
    if exist "%%s" (
        cd "%%s"
        call mvn clean package -DskipTests
        if errorlevel 1 (
            echo ❌ Failed to build %%s
            exit /b 1
        ) else (
            echo ✅ %%s built successfully
        )
        cd ..
    ) else (
        echo ⚠️  Directory %%s not found
    )
)

echo === All microservices built successfully ===
echo You can now run: docker-compose up -d
pause
