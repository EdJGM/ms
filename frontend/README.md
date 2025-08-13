# SubastasApp Frontend

Frontend de Next.js para el sistema de subastas en línea construido con microservicios.

## 🚀 Características

- **Autenticación JWT**: Login, registro y gestión de sesiones
- **Subastas en tiempo real**: WebSocket para actualizaciones en vivo
- **Interfaz de pujas**: Sistema completo de pujas con validación
- **Responsive Design**: Optimizado para móviles y escritorio
- **Gestión de estado**: Zustand para estado global
- **TypeScript**: Tipado completo para mejor desarrollo
- **Tailwind CSS**: Diseño moderno y consistente

## 🛠️ Tecnologías

- **Next.js 14** - Framework React con App Router
- **TypeScript** - Tipado estático
- **Tailwind CSS** - Framework de CSS utility-first
- **Zustand** - Gestión de estado simple y eficiente
- **Axios** - Cliente HTTP para APIs
- **Socket.io Client** - WebSocket para tiempo real
- **React Hot Toast** - Notificaciones elegantes
- **Moment.js** - Manejo de fechas
- **Heroicons** - Iconos SVG

## 📁 Estructura del Proyecto

```
src/
├── app/                    # App Router de Next.js
│   ├── auth/              # Páginas de autenticación
│   ├── auctions/          # Páginas de subastas
│   └── page.tsx           # Página principal
├── components/            # Componentes reutilizables
│   ├── auth/              # Componentes de autenticación
│   ├── auctions/          # Componentes de subastas
│   └── layout/            # Componentes de layout
├── lib/                   # Utilidades y configuración
│   ├── api.ts            # Cliente API
│   ├── auth.ts           # Utilidades de autenticación
│   └── websocket.ts      # Cliente WebSocket
├── store/                 # Gestión de estado Zustand
│   ├── authStore.ts      # Estado de autenticación
│   └── auctionStore.ts   # Estado de subastas
└── types/                 # Definiciones TypeScript
    └── index.ts
```

## 🚀 Instalación y Configuración

### Prerrequisitos

- Node.js 18+ 
- npm o yarn
- Microservicios backend ejecutándose

### Instalación

1. **Instalar dependencias**:
```bash
npm install
```

2. **Configurar variables de entorno**:
```bash
# Crear archivo .env.local
NEXT_PUBLIC_API_BASE_URL=http://localhost:8000
NEXT_PUBLIC_WS_URL=ws://localhost:8085
```

3. **Ejecutar en desarrollo**:
```bash
npm run dev
```

4. **Construir para producción**:
```bash
npm run build
npm start
```

## 🔧 Configuración del Backend

Asegúrate de que los siguientes servicios estén ejecutándose:

- **API Gateway**: `http://localhost:8000`
- **Eureka Server**: `http://localhost:8761`
- **Notification Service WebSocket**: `ws://localhost:8085`
- **Bases de datos**: CockroachDB cluster
- **Message Broker**: RabbitMQ

## 📱 Funcionalidades Principales

### Autenticación
- ✅ Registro de usuarios con validación
- ✅ Login con JWT
- ✅ Gestión de sesiones persistentes
- ✅ Roles de usuario (PARTICIPANTE, MODERADOR, ADMINISTRADOR)

### Subastas
- ✅ Lista de subastas con filtros y búsqueda
- ✅ Vista detallada de subasta
- ✅ Estados: programada, activa, finalizada
- ✅ Información completa del producto
- ✅ Timer en tiempo real

### Sistema de Pujas
- ✅ Interfaz de pujas en tiempo real
- ✅ Validación de montos mínimos
- ✅ Historial de pujas
- ✅ Notificaciones de nuevas pujas
- ✅ Confirmación antes de pujar

### Tiempo Real
- ✅ WebSocket para actualizaciones en vivo
- ✅ Nuevas pujas sin refresh
- ✅ Notificaciones instantáneas
- ✅ Estado de conexión visible

### Diseño y UX
- ✅ Responsive design
- ✅ Dark/light mode friendly
- ✅ Loading states
- ✅ Error handling
- ✅ Accesibilidad básica

## 🎨 Componentes Principales

### `AuctionCard`
Tarjeta de subasta con información esencial:
- Estado visual con colores
- Precios y tiempo restante
- Información del vendedor
- Acceso a detalles

### `BiddingInterface`
Interfaz completa para realizar pujas:
- Validación de montos
- Pujas rápidas predefinidas
- Confirmación de seguridad
- Estado de loading

### `BidHistory`
Historial detallado de pujas:
- Lista ordenada de pujas
- Identificación de puja ganadora
- Estadísticas de participación
- Opción de ocultar usuarios

### `Navbar`
Navegación principal:
- Menu responsive
- Estado de autenticación
- Links contextuales por rol
- Dropdown de usuario

## 🔒 Seguridad

- **JWT Tokens**: Almacenamiento seguro en cookies
- **Validación Frontend**: Validación de formularios
- **Sanitización**: Prevención XSS básica
- **HTTPS Ready**: Configurado para producción
- **CORS**: Configuración adecuada

## 📊 Estado de la Aplicación

### AuthStore (Zustand)
```typescript
interface AuthState {
  user: Partial<User> | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  error: string | null;
  // Actions: login, register, logout, validateAuth
}
```

### AuctionStore (Zustand)
```typescript
interface AuctionState {
  auctions: AuctionView[];
  currentAuction: AuctionView | null;
  currentBids: Bid[];
  isLoading: boolean;
  error: string | null;
  // Actions: loadAuctions, placeBid, search, etc.
}
```

## 🔗 Integración con Backend

### Endpoints Principales
- `POST /api/v1/auth/login` - Autenticación
- `POST /api/v1/auth/register` - Registro
- `GET /api/v1/subastas` - Lista de subastas
- `GET /api/v1/subastas/{id}` - Detalle de subasta
- `POST /api/v1/subastas/{id}/pujas` - Crear puja
- `GET /api/v1/subastas/{id}/pujas` - Historial de pujas

### WebSocket Events
- `new-bid` - Nueva puja realizada
- `auction-extended` - Subasta extendida
- `auction-ended` - Subasta finalizada
- `moderator-joined` - Moderador se unió

## 🧪 Testing

```bash
# Ejecutar tests (si están configurados)
npm test

# Linting
npm run lint

# Type checking
npm run type-check
```

## 📈 Performance

- **Code Splitting**: Automático con Next.js
- **Image Optimization**: Next.js Image component
- **Bundle Analysis**: `npm run analyze`
- **Caching**: Headers HTTP apropiados
- **Lazy Loading**: Componentes bajo demanda

## 🚀 Deployment

### Vercel (Recomendado)
```bash
npm run build
# Deploy automático con git push
```

### Docker
```dockerfile
# Dockerfile incluido para contenedores
docker build -t auctions-frontend .
docker run -p 3000:3000 auctions-frontend
```

### Variables de Entorno para Producción
```env
NEXT_PUBLIC_API_BASE_URL=https://api.subastasapp.com
NEXT_PUBLIC_WS_URL=wss://ws.subastasapp.com
NODE_ENV=production
```

## 🤝 Contribución

1. Fork el proyecto
2. Crear feature branch (`git checkout -b feature/nueva-funcionalidad`)
3. Commit cambios (`git commit -m 'Agregar nueva funcionalidad'`)
4. Push a branch (`git push origin feature/nueva-funcionalidad`)
5. Crear Pull Request

## 📄 Licencia

Este proyecto está bajo la licencia MIT. Ver `LICENSE` para más detalles.

## 🆘 Soporte

- **Issues**: [GitHub Issues](https://github.com/tu-usuario/subastas-frontend/issues)
- **Documentación**: Este README y comentarios en código
- **Email**: soporte@subastasapp.com

---

**SubastasApp Frontend** - Desarrollado con ❤️ usando Next.js y tecnologías modernas