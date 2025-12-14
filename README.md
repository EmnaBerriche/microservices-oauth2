# Microservices OAuth2 with Keycloak

A microservices architecture demonstrating OAuth2 authentication using Keycloak, Spring Boot backend services, and an Angular frontend.

## Architecture

- **Keycloak**: Identity and access management (port 8080)
- **Service1**: Spring Boot OAuth2 resource server (port 8081)
- **Service2**: Spring Boot OAuth2 resource server (port 8082)
- **Frontend**: Angular application with Nginx (port 4200)

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
