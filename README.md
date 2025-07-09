# Sistema de Subastas - Arquitectura de Microservicios

Este proyecto implementa un sistema completo de subastas en línea usando una arquitectura de microservicios con Spring Boot, Spring Cloud y JWT authentication.

## 🏗️ Arquitectura del Sistema

### Microservicios

1. **Eureka Server** (Puerto 8761) - Service Discovery y Registry
2. **API Gateway** (Puerto 8080) - Punto de entrada único con autenticación JWT
3. **User Service** (Puerto 8081) - Gestión de usuarios, autenticación y autorización
4. **Auth Service** (Puerto 8082) - Servicio de autenticación (opcional)
5. **Auction Service** (Puerto 8083) - Gestión de subastas y categorías
6. **Bid Service** (Puerto 8084) - Gestión de pujas y ofertas
7. **Notification Service** (Puerto 8085) - Sistema de notificaciones

### 🛠️ Tecnologías Utilizadas

- **Version de JAVA** - 17
- **Spring Boot 3.2.0** - Framework principal
- **Spring Cloud 2023.0.0** - Microservicios y Service Discovery
- **Spring Security 6.x** - Seguridad y autenticación
- **JWT (JSON Web Tokens)** - Autenticación stateless
- **PostgreSQL** - Base de datos relacional
- **Redis** - Cache distribuido
- **RabbitMQ** - Message broker para comunicación asíncrona
- **Docker & Docker Compose** - Contenerización
- **Netflix Eureka** - Service Discovery
- **Spring Cloud Gateway** - API Gateway con filtros personalizados

## 📁 Estructura del Proyecto

```
ms/
├── docker-compose.yml          # Orquestación de contenedores
├── build-all.sh               # Script de construcción (Linux/Mac)
├── build-all.bat              # Script de construcción (Windows)
├── README.md                   # Documentación principal
├── QUICK_START.md              # Guía rápida de inicio
├── eureka-server/             # Service Discovery
├── api-gateway/               # API Gateway con filtros JWT
├── user-service/              # Servicio de usuarios y autenticación
├── auth-service/              # Servicio de autenticación (opcional)
├── auction-service/           # Servicio de subastas
├── bid-service/               # Servicio de pujas
└── notification-service/      # Servicio de notificaciones
```

## 🚀 Características Principales

### 🔐 Seguridad y Autenticación
- **JWT Authentication**: Tokens seguros con expiración configurable
- **Role-based Authorization**: Roles USER y ADMIN
- **Password Encryption**: BCrypt para hash de contraseñas
- **API Gateway Security**: Filtros JWT centralizados
- **CORS Configuration**: Configuración para desarrollo y producción

### 📊 Funcionalidades del Sistema
- **Gestión de Usuarios**: Registro, login, perfiles
- **Sistema de Subastas**: Crear, editar, eliminar subastas
- **Sistema de Pujas**: Crear pujas, historial por usuario/subasta
- **Notificaciones**: Sistema de alertas y notificaciones
- **Monitoreo**: Health checks y métricas con Actuator

### 🔄 Comunicación Entre Servicios
- **API Gateway**: Enrutamiento inteligente con balanceo de carga
- **Service Discovery**: Registro automático con Eureka
- **Circuit Breaker**: Tolerancia a fallos
- **Load Balancing**: Distribución de carga automática

## 🛠️ Prerrequisitos

- **Java 17+**
- **Maven 3.8+**
- **Docker & Docker Compose**
- **PostgreSQL** (para ejecución local)
- **Redis** (para caché)
- **RabbitMQ** (para messaging)

## 🚀 Configuración y Ejecución

### Opción 1: Docker Compose (Recomendado)

```bash
# Clonar el repositorio
git clone <repository-url>
cd ms

# Construir todas las imágenes
docker-compose build

# Iniciar todos los servicios
docker-compose up -d

# Verificar estado de los servicios
docker-compose ps

# Ver logs
docker-compose logs -f

# Detener servicios
docker-compose down
```

### Opción 2: Ejecución Local

#### Paso 1: Configurar bases de datos
```bash
# Crear bases de datos en PostgreSQL o cockroach
auth_db
users_db
auctions_db
auction_query_db
bids_db
notifications_db
```

#### Paso 2: Iniciar servicios de infraestructura
```bash
# Redis
docker run -d -p 6379:6379 redis:7-alpine

# RabbitMQ
docker run -d -p 5672:5672 -p 15672:15672 rabbitmq:3-management
```

#### Paso 3: Compilar servicios
```bash
# Usar script de construcción
./build-all.sh   # Linux/Mac
build-all.bat    # Windows

# O compilar individualmente
mvn clean install -DskipTests
```

#### Paso 4: Ejecutar servicios (en orden)
```bash
# 1. Eureka Server
cd eureka-server && mvn spring-boot:run &

# 2. API Gateway
cd api-gateway && mvn spring-boot:run &

# 3. User Service
cd user-service && mvn spring-boot:run &

# 4. Auction Service
cd auction-service && mvn spring-boot:run &

# 5. Bid Service
cd bid-service && mvn spring-boot:run &

# 6. Notification Service
cd notification-service && mvn spring-boot:run &
```

## 📡 API Endpoints

### 🔐 Autenticación
```http
POST /api/users/register    # Registro de usuario
POST /api/users/login       # Login (obtener JWT)
```

### 👤 Gestión de Usuarios
```http
GET    /api/users/profile   # Obtener perfil del usuario
PUT    /api/users/profile   # Actualizar perfil
GET    /api/users           # Listar usuarios (ADMIN)
GET    /api/users/{id}      # Obtener usuario por ID
```

### 🏷️ Gestión de Subastas
```http
GET    /api/auctions        # Listar todas las subastas
GET    /api/auctions/{id}   # Obtener subasta por ID
POST   /api/auctions        # Crear nueva subasta (AUTH)
PUT    /api/auctions/{id}   # Actualizar subasta (AUTH)
DELETE /api/auctions/{id}   # Eliminar subasta (ADMIN)
```

### 💰 Gestión de Pujas
```http
POST   /api/auctions/{id}/bids     # Crear puja en subasta
GET    /api/auctions/{id}/bids     # Obtener pujas de subasta
GET    /api/users/{id}/bids        # Obtener pujas de usuario
DELETE /api/auctions/{auctionId}/bids/{bidId}  # Eliminar puja
```

### 🔔 Notificaciones
```http
POST   /api/notifications/send     # Enviar notificación
GET    /api/notifications/history  # Historial de notificaciones enviadas
GET    /api/notifications/user/{userId}  # Notificaciones de usuario
```

## 🔧 Configuración JWT

### Headers requeridos
```http
Authorization: Bearer <jwt-token>
```

### Ejemplo de uso
```bash
# 1. Registrar usuario
curl -X POST http://localhost:8080/api/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "usuario1",
    "email": "usuario1@email.com",
    "password": "password123",
    "firstName": "Juan",
    "lastName": "Pérez"
  }'

# 2. Login para obtener token
curl -X POST http://localhost:8080/api/users/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "usuario1@email.com",
    "password": "password123"
  }'

# 3. Usar token en requests autenticados
curl -X GET http://localhost:8080/api/users/profile \
  -H "Authorization: Bearer <jwt-token>"
```

## 📊 Monitoreo y Administración

### Eureka Dashboard
- **URL**: http://localhost:8761
- **Descripción**: Visualizar servicios registrados

### RabbitMQ Management
- **URL**: http://localhost:15672
- **Usuario**: admin
- **Contraseña**: password

### Actuator Endpoints
Cada servicio expone endpoints de monitoreo:
```http
GET /actuator/health    # Estado de salud
GET /actuator/metrics   # Métricas del sistema
GET /actuator/info      # Información del servicio
```

## 🗃️ Modelo de Datos

### Users (user-service)
```sql
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    phone_number VARCHAR(20),
    role VARCHAR(20) DEFAULT 'USER',
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### Auctions (auction-service)
```sql
CREATE TABLE auctions (
    id SERIAL PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    start_price DECIMAL(10,2) NOT NULL,
    current_price DECIMAL(10,2),
    start_date TIMESTAMP NOT NULL,
    end_date TIMESTAMP NOT NULL,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    seller_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### Bids (bid-service)
```sql
CREATE TABLE bids (
    id SERIAL PRIMARY KEY,
    auction_id BIGINT NOT NULL,
    bidder_id BIGINT NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    bid_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### Notifications (notification-service)
```sql
CREATE TABLE notifications (
    id SERIAL PRIMARY KEY,
    user_id BIGINT,
    title VARCHAR(200),
    message TEXT,
    type VARCHAR(50),
    email VARCHAR(100),
    subject VARCHAR(200),
    text TEXT,
    sent_by VARCHAR(100),
    sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

## 🔧 Configuración de Desarrollo

### Variables de Entorno
```bash
# JWT Configuration
JWT_SECRET=mySecretKey123456789012345678901234567890
JWT_EXPIRATION=86400000

# Database Configuration
DB_HOST=localhost
DB_PORT=5432
DB_NAME=auction_db
DB_USER=postgres
DB_PASSWORD=your_password

# Redis Configuration
REDIS_HOST=localhost
REDIS_PORT=6379

# RabbitMQ Configuration
RABBITMQ_HOST=localhost
RABBITMQ_PORT=5672
RABBITMQ_USER=admin
RABBITMQ_PASSWORD=password
```

### Profiles de Spring
- **development**: Configuración local
- **docker**: Configuración para contenedores
- **production**: Configuración optimizada

## 🐛 Troubleshooting

### Problemas Comunes

1. **Servicios no se registran en Eureka**
   ```bash
   # Verificar logs de Eureka
   docker-compose logs eureka-server
   
   # Verificar conectividad de red
   docker-compose exec user-service ping eureka-server
   ```

2. **Error de JWT Token inválido**
   ```bash
   # Verificar que el secret sea el mismo en todos los servicios
   grep -r "jwtSecret" */src/main/resources/application.yml
   ```

3. **Problemas de base de datos**
   ```bash
   # Verificar conexión a PostgreSQL
   docker-compose exec user-service pg_isready -h postgres
   ```

4. **Puerto ya en uso**
   ```bash
   # Verificar puertos ocupados
   netstat -tulpn | grep :8080
   
   # Cambiar puerto en application.yml
   server:
     port: 8081
   ```

### Logs Útiles

```bash
# Ver logs de un servicio específico
docker-compose logs -f user-service

# Ver logs de todos los servicios
docker-compose logs -f

# Logs en tiempo real con filtro
docker-compose logs -f --tail=100 | grep ERROR
```

## 🧪 Testing

### Ejecutar Tests
```bash
# Tests unitarios
mvn test

# Tests de integración
mvn verify

# Tests con cobertura
mvn jacoco:report
```

### Colección de Postman
Se incluye una colección de Postman con todos los endpoints configurados en `docs/postman/`.

## 📈 Métricas y Monitoreo

### Métricas Disponibles
- **Response Time**: Tiempo de respuesta promedio
- **Throughput**: Requests por segundo
- **Error Rate**: Tasa de errores
- **Circuit Breaker**: Estado de los circuit breakers

### Grafana Dashboard
```yaml
# docker-compose.monitoring.yml
version: '3.8'
services:
  grafana:
    image: grafana/grafana:latest
    ports:
      - "3000:3000"
    environment:
      - GF_SECURITY_ADMIN_PASSWORD=admin
```

## 🤝 Contribución

1. Fork el proyecto
2. Crear feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit cambios (`git commit -m 'Add AmazingFeature'`)
4. Push a branch (`git push origin feature/AmazingFeature`)
5. Crear Pull Request

### Estándares de Código
- **Java**: Google Java Style Guide
- **Commits**: Conventional Commits
- **Tests**: Mínimo 80% de cobertura

## 📝 Licencia

Este proyecto está bajo la licencia MIT. Ver `LICENSE` para más detalles.

## 👥 Autores

- **Tu Nombre** - Desarrollo inicial - [Tu GitHub](https://github.com/tuusuario)

## 📞 Soporte

Para soporte y preguntas:
- **Email**: tu-email@ejemplo.com
- **Issues**: GitHub Issues
- **Discord**: [Server Link]

---

⭐ Si este proyecto te ayudó, considera darle una estrella en GitHub!
