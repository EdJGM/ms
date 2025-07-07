# 🚀 Quick Start Guide - Sistema de Subastas

Esta guía te ayudará a poner en marcha el sistema de subastas en menos de 5 minutos.

## 📋 Prerrequisitos

- **Java 17+**
- **Maven 3.8+**
- **Docker & Docker Compose**
- **Git**

## 🐳 Opción 1: Ejecución con Docker (Recomendado)

### Paso 1: Obtener el código
```bash
# Clonar el repositorio
git clone <repository-url>
cd ms/
```

### Paso 2: Construir y ejecutar
```bash
# Construir todas las imágenes
docker-compose build

# Iniciar todos los servicios
docker-compose up -d

# Verificar que los servicios estén activos
docker-compose ps
```

### Paso 3: Verificar el funcionamiento
```bash
# Ver logs en tiempo real
docker-compose logs -f

# Verificar estado de salud
curl http://localhost:8080/actuator/health
```

## 🖥️ Opción 2: Ejecución Local

### Paso 1: Configurar bases de datos
```bash
# Crear bases de datos PostgreSQL
createdb users_db
createdb auctions_db
createdb bids_db
createdb notifications_db
```

### Paso 2: Iniciar servicios de infraestructura
```bash
# Redis
docker run -d -p 6379:6379 redis:7-alpine

# RabbitMQ
docker run -d -p 5672:5672 -p 15672:15672 rabbitmq:3-management
```

### Paso 3: Compilar y ejecutar servicios
```bash
# Compilar todos los servicios
mvn clean install -DskipTests

# Ejecutar servicios en orden (en terminales separadas)
cd eureka-server && mvn spring-boot:run
cd api-gateway && mvn spring-boot:run
cd user-service && mvn spring-boot:run
cd auction-service && mvn spring-boot:run
cd bid-service && mvn spring-boot:run
cd notification-service && mvn spring-boot:run
```

## 🔍 Verificación del Sistema

### Dashboards y Monitoreo
- **Eureka Dashboard**: http://localhost:8761
- **RabbitMQ Management**: http://localhost:15672 (admin/password)
- **API Gateway**: http://localhost:8080

### Health Checks
```bash
# Verificar estado de todos los servicios
curl http://localhost:8080/actuator/health
curl http://localhost:8081/actuator/health
curl http://localhost:8083/actuator/health
curl http://localhost:8084/actuator/health
curl http://localhost:8085/actuator/health
```

## 🧪 Pruebas Rápidas del Sistema

### 1. Registrar un usuario
```bash
curl -X POST http://localhost:8080/api/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "password123",
    "firstName": "Test",
    "lastName": "User"
  }'
```

### 2. Hacer login y obtener JWT
```bash
curl -X POST http://localhost:8080/api/users/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "password123"
  }'
```

**Guarda el token JWT de la respuesta para los siguientes pasos**

### 3. Crear una subasta
```bash
export JWT_TOKEN="tu-jwt-token-aqui"

curl -X POST http://localhost:8080/api/auctions \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -d '{
    "title": "iPhone 15 Pro",
    "description": "Nuevo iPhone 15 Pro 256GB",
    "startPrice": 500.00,
    "startDate": "2025-07-07T10:00:00",
    "endDate": "2025-07-14T10:00:00"
  }'
```

### 4. Obtener lista de subastas
```bash
curl -X GET http://localhost:8080/api/auctions \
  -H "Authorization: Bearer $JWT_TOKEN"
```

### 5. Crear una puja
```bash
curl -X POST "http://localhost:8080/api/auctions/1/bids?userId=1" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -d '{
    "amount": 550.00
  }'
```

### 6. Enviar una notificación
```bash
curl -X POST http://localhost:8080/api/notifications/send \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -d '{
    "userId": 1,
    "title": "Nueva puja",
    "message": "Alguien ha superado tu puja",
    "type": "BID_OUTBID"
  }'
```

### 7. Ver historial de pujas del usuario
```bash
curl -X GET http://localhost:8080/api/users/1/bids \
  -H "Authorization: Bearer $JWT_TOKEN"
```

## 📊 Endpoints Principales

### 🔐 Autenticación
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/users/register` | Registrar nuevo usuario |
| POST | `/api/users/login` | Login y obtener JWT |

### 👤 Usuarios
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/users/profile` | Obtener perfil del usuario |
| PUT | `/api/users/profile` | Actualizar perfil |
| GET | `/api/users` | Listar usuarios (ADMIN) |

### 🏷️ Subastas
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/auctions` | Listar subastas |
| GET | `/api/auctions/{id}` | Obtener subasta específica |
| POST | `/api/auctions` | Crear nueva subasta |
| PUT | `/api/auctions/{id}` | Actualizar subasta |
| DELETE | `/api/auctions/{id}` | Eliminar subasta (ADMIN) |

### 💰 Pujas
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/auctions/{id}/bids` | Crear puja |
| GET | `/api/auctions/{id}/bids` | Obtener pujas de subasta |
| GET | `/api/users/{id}/bids` | Obtener pujas de usuario |

### 🔔 Notificaciones
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/notifications/send` | Enviar notificación |
| GET | `/api/notifications/history` | Historial de notificaciones |
| GET | `/api/notifications/user/{userId}` | Notificaciones de usuario |

## 🔧 Configuración Rápida

### Variables de entorno importantes
```bash
# JWT Configuration
export JWT_SECRET="mySecretKey123456789012345678901234567890"
export JWT_EXPIRATION="86400000"

# Database Configuration
export DB_HOST="localhost"
export DB_PORT="5432"
export DB_USER="postgres"
export DB_PASSWORD="your_password"
```

### Puertos por defecto
- **Eureka Server**: 8761
- **API Gateway**: 8080
- **User Service**: 8081
- **Auth Service**: 8082
- **Auction Service**: 8083
- **Bid Service**: 8084
- **Notification Service**: 8085

## 🐛 Solución de Problemas

### Problema: Servicios no se registran en Eureka
```bash
# Verificar logs de Eureka
docker-compose logs eureka-server

# Verificar conectividad
docker-compose exec user-service ping eureka-server
```

### Problema: Error de JWT
```bash
# Verificar que el secret sea el mismo en todos los servicios
grep -r "jwtSecret" */src/main/resources/application.yml
```

### Problema: Puerto ya en uso
```bash
# Verificar puertos ocupados
netstat -tulpn | grep :8080

# Detener servicios Docker
docker-compose down
```

### Problema: Base de datos no conecta
```bash
# Verificar PostgreSQL
docker-compose exec user-service pg_isready -h postgres

# Verificar logs de BD
docker-compose logs postgres
```

## 🎯 Casos de Uso Comunes

### Crear un usuario administrador
```bash
# 1. Registrar usuario normal
curl -X POST http://localhost:8080/api/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "email": "admin@example.com",
    "password": "admin123",
    "firstName": "Admin",
    "lastName": "User"
  }'

# 2. Cambiar rol a ADMIN en la base de datos
# Conectar a PostgreSQL y ejecutar:
# UPDATE users SET role = 'ADMIN' WHERE email = 'admin@example.com';
```

### Flujo completo de subasta
```bash
# 1. Login como usuario
TOKEN=$(curl -s -X POST http://localhost:8080/api/users/login \
  -H "Content-Type: application/json" \
  -d '{"email": "test@example.com", "password": "password123"}' | jq -r '.token')

# 2. Crear subasta
AUCTION_ID=$(curl -s -X POST http://localhost:8080/api/auctions \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "title": "MacBook Pro",
    "description": "MacBook Pro 2025",
    "startPrice": 1000.00,
    "startDate": "2025-07-07T10:00:00",
    "endDate": "2025-07-14T10:00:00"
  }' | jq -r '.id')

# 3. Hacer puja
curl -X POST "http://localhost:8080/api/auctions/$AUCTION_ID/bids?userId=1" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"amount": 1100.00}'

# 4. Enviar notificación
curl -X POST http://localhost:8080/api/notifications/send \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "userId": 1,
    "title": "Nueva puja",
    "message": "Tu puja ha sido registrada exitosamente",
    "type": "BID_CREATED"
  }'
```

## 🔄 Comandos Útiles

### Docker Compose
```bash
# Ver estado de servicios
docker-compose ps

# Reiniciar un servicio específico
docker-compose restart user-service

# Ver logs de un servicio
docker-compose logs -f user-service

# Ejecutar comando en contenedor
docker-compose exec user-service bash

# Limpiar todo
docker-compose down -v --remove-orphans
```

### Maven
```bash
# Compilar todos los servicios
mvn clean install -DskipTests

# Ejecutar tests
mvn test

# Generar reporte de cobertura
mvn jacoco:report

# Ejecutar un servicio específico
mvn spring-boot:run -Dspring-boot.run.profiles=development
```

## 📚 Recursos Adicionales

- **Documentación completa**: Ver `README.md`
- **Arquitectura**: Diagramas en `docs/architecture/`
- **Postman Collection**: `docs/postman/auction-system.postman_collection.json`
- **Ejemplos de código**: `docs/examples/`

## 🆘 Obtener Ayuda

Si tienes problemas:
1. Revisa los logs con `docker-compose logs -f`
2. Verifica el estado de Eureka en http://localhost:8761
3. Consulta la sección de troubleshooting en el README.md
4. Abre un issue en GitHub

¡Listo! Tu sistema de subastas está funcionando. 🎉
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "password123",
    "firstName": "Test",
    "lastName": "User"
  }'
```

#### Login
```bash
curl -X POST http://localhost:8080/api/users/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "password123"
  }'
```

### 5. Monitoreo

#### RabbitMQ Management
- URL: http://localhost:15672
- Usuario: admin
- Contraseña: password

#### Health Checks
```bash
curl http://localhost:8080/actuator/health
curl http://localhost:8081/actuator/health
curl http://localhost:8082/actuator/health
curl http://localhost:8083/actuator/health
curl http://localhost:8084/actuator/health
```

### 6. Detener servicios

```bash
# Detener todos los servicios
docker-compose down

# Detener y eliminar volúmenes
docker-compose down -v
```

### 7. Desarrollo Local (Opcional)

Si prefieres ejecutar localmente sin Docker:

```bash
# 1. Instalar PostgreSQL y crear bases de datos
createdb users_db
createdb auctions_db
createdb bids_db

# 2. Ejecutar Redis
docker run -d -p 6379:6379 redis:7-alpine

# 3. Ejecutar RabbitMQ
docker run -d -p 5672:5672 -p 15672:15672 rabbitmq:3-management

# 4. Construir todos los servicios
./build-all.bat  # Windows
# o
./build-all.sh   # Linux/Mac

# 5. Ejecutar servicios en orden
cd eureka-server && mvn spring-boot:run &
cd api-gateway && mvn spring-boot:run &
cd user-service && mvn spring-boot:run &
cd auction-service && mvn spring-boot:run &
cd bid-service && mvn spring-boot:run &
cd notification-service && mvn spring-boot:run &
```

### 8. Arquitectura de Microservicios

```
┌─────────────────┐    ┌─────────────────┐
│   Frontend      │────│   API Gateway   │
│   (Port 3000)   │    │   (Port 8080)   │
└─────────────────┘    └─────────────────┘
                                │
                       ┌────────┴────────┐
                       │                 │
                ┌─────────────┐    ┌─────────────┐
                │   Eureka    │    │   Services  │
                │   Server    │    │             │
                │ (Port 8761) │    │ User: 8081  │
                └─────────────┘    │ Auction:8082│
                                   │ Bid: 8083   │
                                   │ Notify: 8084│
                                   └─────────────┘
                                          │
                        ┌─────────────────┼─────────────────┐
                        │                 │                 │
                 ┌─────────────┐   ┌─────────────┐   ┌─────────────┐
                 │ PostgreSQL  │   │    Redis    │   │  RabbitMQ   │
                 │ (Databases) │   │   (Cache)   │   │ (Messages)  │
                 └─────────────┘   └─────────────┘   └─────────────┘
```

### 9. Troubleshooting

#### Problemas comunes:

1. **Puerto ocupado**: Cambiar puertos en `docker-compose.yml`
2. **Servicios no se conectan**: Verificar que Eureka esté corriendo
3. **Error de base de datos**: Verificar credenciales en `application.yml`
4. **JWT Token inválido**: Verificar que el secret sea el mismo en todos los servicios

#### Comandos útiles:

```bash
# Ver logs específicos
docker-compose logs -f user-service

# Reiniciar un servicio específico
docker-compose restart user-service

# Eliminar todo y empezar de nuevo
docker-compose down -v
docker system prune -a
```

¡Ahora tu sistema de subastas con microservicios está listo para funcionar! 🎉
