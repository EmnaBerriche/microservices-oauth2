# Microservices OAuth2 with Keycloak

A microservices architecture demonstrating OAuth2 authentication using Keycloak, Spring Boot backend services, and an Angular frontend.

## 🏗️ Architecture Overview

### Components

```
┌─────────────────────────────────────────────────────────────────┐
│                         Browser (User)                          │
└────────────────────────┬────────────────────────────────────────┘
                         │
                         ├─────► Keycloak (Port 8080)
                         │       • Authentication Server
                         │       • Issues JWT tokens
                         │       • Manages users & clients
                         │
                         ├─────► Frontend (Port 4200)
                         │       • Angular + Nginx
                         │       • Login UI
                         │       • API Consumer
                         │
                         ├─────► Service1 (Port 8081)
                         │       • Spring Boot REST API
                         │       • OAuth2 Resource Server
                         │       • Calls Service2
                         │
                         └─────► Service2 (Port 8082)
                                 • Spring Boot REST API
                                 • OAuth2 Resource Server
                                 • Partner data provider
```

### Service Details

| Service | Technology | Port | Role |
|---------|-----------|------|------|
| **Keycloak** | Keycloak 24.0.4 | 8080 | Identity & Access Management |
| **Frontend** | Angular 20 + Nginx | 4200 | Web UI, initiates OAuth flow |
| **Service1** | Spring Boot 4.0 + Java 17 | 8081 | Protected API, orchestrator |
| **Service2** | Spring Boot 4.0 + Java 17 | 8082 | Protected API, data provider |

## 🔐 OAuth2 Authentication Flow

### 1. User Login Flow
```
User → Frontend → Keycloak → Login Page
                      ↓
                 Authenticate
                      ↓
            Issue JWT Token (angular-client)
                      ↓
            Frontend ← JWT Token
```

### 2. API Call Flow (Frontend → Service1)
```
Frontend → Service1 (/api/private)
   │           │
   │           └─► Validate JWT Token
   │                   ↓
   │              Check signature with Keycloak JWKS
   │                   ↓
   │              ✓ Token valid
   │                   ↓
   └─────────► Process Request
                      ↓
               Return Response
```

### 3. Service-to-Service Flow (Service1 → Service2)
```
Service1 → Keycloak
   │           │
   │           └─► Request token (client_credentials)
   │                   ↓
   │           Issue JWT Token (spring-api-client)
   │                   ↓
Service1 ← JWT Token
   │
   ├─────────► Service2 (/api/partner)
                   │
                   └─► Validate JWT Token
                           ↓
                      ✓ Token valid
                           ↓
                   Return Partner Data
                           ↓
Service1 ← Partner Data
   │
   └─────────► Return Combined Response
```

## 🔑 Security Configuration

### Keycloak Setup

**Realm**: `demo-realm`

**Clients**:
1. **spring-api-client** (Confidential)
   - Client ID: `spring-api-client`
   - Secret: `0CMyWyL8nyXECetrilQVnJFbi8uCRx9W`
   - Grant Type: `client_credentials`
   - Used by: Service1 → Service2 calls

2. **angular-client** (Public)
   - Client ID: `angular-client`
   - Grant Type: `authorization_code` + `refresh_token`
   - Redirect URI: `http://localhost:4200/*`
   - Used by: Frontend user authentication

### JWT Token Validation

Services use **JWKS (JSON Web Key Set)** from Keycloak to validate tokens:
- Keycloak JWKS URL: `http://keycloak:8080/realms/demo-realm/protocol/openid-connect/certs`
- Services fetch public keys to verify JWT signatures
- No shared secrets between services and Keycloak (asymmetric validation)

## Prerequisites

- Docker Desktop (Windows/Mac) or Docker Engine (Linux)
- Docker Compose v3.8+

## Quick Start with Docker

### 1. Build all services

```powershell
docker-compose build
```

This will:
- Build Spring Boot services using Maven multi-stage builds
- Build Angular frontend and package with Nginx
- Pull Keycloak image

### 2. Start all services

```powershell
docker-compose up -d
```

Or to see logs in real-time:

```powershell
docker-compose up
```

### 3. Access the applications

- **Frontend**: http://localhost:4200
- **Keycloak Admin**: http://localhost:8080 (admin/admin)
- **Service1 API**: http://localhost:8081
- **Service2 API**: http://localhost:8082

### 4. Stop all services

```powershell
docker-compose down
```

To also remove volumes:

```powershell
docker-compose down -v
```

## Keycloak Configuration

1. Access Keycloak admin console: http://localhost:8080
2. Login with `admin/admin`
3. Create realm: `demo-realm`
4. Create client: `spring-api-client`
5. Configure client settings and secret: `0CMyWyL8nyXECetrilQVnJFbi8uCRx9W`

## Development

### Running services individually

```powershell
# Build specific service
docker-compose build service1

# Start specific service
docker-compose up service1

# View logs
docker-compose logs -f service1
```

### Rebuilding after code changes

```powershell
docker-compose up --build
```

### Local development (without Docker)

**Backend services:**
```powershell
cd service1
.\mvnw spring-boot:run
```

**Frontend:**
```powershell
cd frontend
npm install
npm start
```

## Docker Image Details

- **Service1 & Service2**: Multi-stage build with Maven 3.9 and Eclipse Temurin JRE 17
- **Frontend**: Multi-stage build with Node 20 and Nginx Alpine
- **Keycloak**: Official Quay.io image v24.0.4

## Networking

All services communicate through the `microservices-net` Docker network. Service names resolve as hostnames (e.g., `http://keycloak:8080`).

## Troubleshooting

### Services can't connect to Keycloak

Ensure Keycloak is fully started:
```powershell
docker-compose logs keycloak
```

Wait for: `Keycloak X.X.X started`

### Build failures

Clear Docker cache and rebuild:
```powershell
docker-compose build --no-cache
```

### Port conflicts

If ports are in use, modify `docker-compose.yml` port mappings:
```yaml
ports:
  - "8081:8081"  # Change left side to available port
```

## License

MIT
