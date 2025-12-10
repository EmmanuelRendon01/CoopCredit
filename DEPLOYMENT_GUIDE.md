# 🚀 CoopCredit - Complete Deployment Guide

## Table of Contents
- [Quick Start](#quick-start)
- [Option 1: Full Stack with Docker](#option-1-full-stack-with-docker-complete)
- [Option 2: Backend Only with Docker](#option-2-backend-only-with-docker)
- [Option 3: Local Development](#option-3-local-development-without-docker)
- [Frontend Deployment](#frontend-deployment)
- [Verification Steps](#verification-steps)
- [Troubleshooting](#troubleshooting)

---

## Quick Start

The **fastest** way to run the complete application:

```bash
# 1. Clone repository
git clone https://github.com/EmmanuelRendon01/CoopCredit.git
cd CoopCredit

# 2. Start all backend services
docker-compose up -d

# 3. Start frontend (in a new terminal)
cd CoopCreditFront
sudo docker build -t coopcredit-frontend .
sudo docker run -d -p 3001:3001 --name coopcredit-frontend coopcredit-frontend

# 4. Access the application
# Frontend: http://localhost:3001
# Backend API: http://localhost:8080
```

---

## Option 1: Full Stack with Docker (Complete)

### Prerequisites
- Docker 20+ and Docker Compose
- Ports 5433, 8080, 8081, 3001 available

### Step-by-Step

#### 1. Start Backend Services

```bash
# Navigate to project root
cd CoopCredit

# Build and start all backend services
docker-compose up -d

# Verify services are running
docker-compose ps
```

**Expected output:**
```
NAME                           STATUS    PORTS
credit-application-service     Up        0.0.0.0:8080->8080/tcp
coopcredit-db                  Up        0.0.0.0:5433->5432/tcp
risk-central-service           Up        0.0.0.0:8081->8081/tcp
```

#### 2. Verify Backend Health

```bash
# Credit Application Service
curl http://localhost:8080/actuator/health

# Risk Central Service
curl http://localhost:8081/actuator/health
```

**Expected response:**
```json
{
  "status": "UP",
  "components": {
    "db": { "status": "UP" },
    "ping": { "status": "UP" }
  }
}
```

#### 3. Deploy Frontend

```bash
# Navigate to frontend directory
cd CoopCreditFront

# Build Docker image
sudo docker build -t coopcredit-frontend .

# Run frontend container
sudo docker run -d -p 3001:3001 --name coopcredit-frontend coopcredit-frontend

# Verify frontend is running
sudo docker ps | grep coopcredit-frontend
```

#### 4. Access the Application

- **Frontend UI**: http://localhost:3001
- **Backend API**: http://localhost:8080
- **API Documentation**: http://localhost:8080/actuator
- **Database**: localhost:5433
  - Database: `coopcredit`
  - Username: `postgres`
  - Password: `postgres`

#### 5. Login to Frontend

**Test Users** (pre-loaded):
```
Admin:
  Username: admin
  Password: admin123

Analyst:
  Username: analyst
  Password: analyst123

Affiliate:
  Username: affiliate1
  Password: affiliate123
```

#### 6. View Logs

```bash
# Backend logs
docker-compose logs -f credit-application-service

# Frontend logs
sudo docker logs -f coopcredit-frontend

# All services
docker-compose logs -f
```

#### 7. Stop Services

```bash
# Stop backend
docker-compose down

# Stop frontend
sudo docker stop coopcredit-frontend
sudo docker rm coopcredit-frontend

# Complete cleanup (removes volumes)
docker-compose down -v
sudo docker rm -f coopcredit-frontend
```

---

## Option 2: Backend Only with Docker

If you only need the backend services (for API testing with Postman):

```bash
# Start backend services
docker-compose up -d

# Verify
curl http://localhost:8080/actuator/health

# Test API (after login)
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

---

## Option 3: Local Development (Without Docker)

### Prerequisites
- Java 17+ JDK
- Maven 3.8+
- PostgreSQL 15
- Node.js (for frontend)

### Backend Setup

#### 1. Setup PostgreSQL

```bash
# Create database
createdb coopcredit

# Or using psql
psql -U postgres
CREATE DATABASE coopcredit;
\q
```

#### 2. Configure Application

Edit `credit-application-service/src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/coopcredit
spring.datasource.username=postgres
spring.datasource.password=your_password
```

#### 3. Start Risk Central Service

```bash
cd risk-central-mock-service

# Build
mvn clean package

# Run
java -jar target/risk-central-mock-service-*.jar

# Or with Maven
mvn spring-boot:run
```

Service will start on port **8081**.

#### 4. Start Credit Application Service

```bash
cd credit-application-service

# Build
mvn clean package

# Run
java -jar target/credit-application-service-*.jar

# Or with Maven
mvn spring-boot:run
```

Service will start on port **8080**.

#### 5. Verify Services

```bash
# Health checks
curl http://localhost:8080/actuator/health
curl http://localhost:8081/actuator/health
```

---

## Frontend Deployment

### Option A: Docker (Recommended)

```bash
cd CoopCreditFront

# Build image
sudo docker build -t coopcredit-frontend .

# Run container
sudo docker run -d -p 3001:3001 --name coopcredit-frontend coopcredit-frontend

# Access
open http://localhost:3001
```

### Option B: Python HTTP Server

```bash
cd CoopCreditFront

# Start server
python3 -m http.server 3000

# Access
open http://localhost:3000
```

### Option C: Node.js HTTP Server

```bash
cd CoopCreditFront

# Start server
npx http-server -p 3000

# Access
open http://localhost:3000
```

### Option D: Using Start Script

```bash
cd CoopCreditFront

# Make executable
chmod +x start.sh

# Run and follow prompts
./start.sh
```

---

## Verification Steps

### 1. Check All Services

```bash
# Backend services
docker-compose ps

# Frontend
sudo docker ps | grep coopcredit-frontend

# Ports in use
netstat -ano | grep -E "8080|8081|3001|5433"
```

### 2. Test Backend API

```bash
# Health check
curl http://localhost:8080/actuator/health

# Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'

# This will return a JWT token
```

### 3. Test Frontend

1. Open browser: http://localhost:3001
2. Login with test credentials
3. Navigate through dashboards
4. Create a credit application

### 4. Check Database

```bash
# Connect to PostgreSQL
docker exec -it coopcredit-db psql -U postgres -d coopcredit

# List tables
\dt

# Check users
SELECT username, email FROM users;

# Exit
\q
```

---

## Troubleshooting

### Port Already in Use

```bash
# Check what's using the port
sudo lsof -i :8080  # or :3001, :8081, :5433

# Kill the process
sudo kill -9 <PID>

# Or stop and remove containers
docker-compose down
sudo docker rm -f coopcredit-frontend
```

### Database Connection Failed

```bash
# Check PostgreSQL is running
docker-compose ps coopcredit-db

# Restart PostgreSQL
docker-compose restart coopcredit-db

# Check logs
docker-compose logs coopcredit-db
```

### Frontend Can't Connect to Backend

1. Check backend is running:
   ```bash
   curl http://localhost:8080/actuator/health
   ```

2. Check API URL in `CoopCreditFront/js/api.js`:
   ```javascript
   const API_BASE_URL = 'http://localhost:8080/api';
   ```

3. CORS issues - backend should allow `http://localhost:3001`

### Docker Permission Denied

```bash
# Add user to docker group (Linux)
sudo usermod -aG docker $USER

# Logout and login again
# Or use sudo for docker commands
```

### Services Won't Start

```bash
# Clean everything
docker-compose down -v
sudo docker rm -f coopcredit-frontend
sudo docker system prune -a

# Rebuild from scratch
docker-compose build --no-cache
docker-compose up -d
```

---

## Port Summary

| Service | Port | URL |
|---------|------|-----|
| Frontend | 3001 | http://localhost:3001 |
| Credit Application Service | 8080 | http://localhost:8080 |
| Risk Central Service | 8081 | http://localhost:8081 |
| PostgreSQL | 5433 | localhost:5433 |

---

## Environment Variables

### Backend (Optional)

Create `.env` file in project root:

```env
# Database
POSTGRES_USER=postgres
POSTGRES_PASSWORD=postgres
POSTGRES_DB=coopcredit

# JWT
JWT_SECRET=CoopCreditSecretKeyForJWT2024MustBeLongEnoughForHS256Algorithm
JWT_EXPIRATION=86400000

# Risk Service
RISK_CENTRAL_URL=http://risk-central-mock-service:8081
```

### Frontend (Optional)

Edit `CoopCreditFront/js/api.js`:

```javascript
const API_BASE_URL = process.env.API_URL || 'http://localhost:8080/api';
```

---

## Production Deployment

### Recommendations

1. **Use environment variables** for secrets
2. **Enable HTTPS** with SSL certificates
3. **Use Nginx** as reverse proxy
4. **Set up monitoring** with Prometheus + Grafana
5. **Configure log aggregation** with ELK stack
6. **Enable database backups**
7. **Use managed PostgreSQL** (AWS RDS, Azure Database)
8. **Implement CI/CD** pipeline

### Example Nginx Configuration

```nginx
server {
    listen 80;
    server_name coopcredit.example.com;

    location / {
        proxy_pass http://localhost:3001;
    }

    location /api {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

---

## Next Steps

After successful deployment:

1. **Import Postman Collection** - See [GUIA-DE-PRUEBAS.md](GUIA-DE-PRUEBAS.md)
2. **Run Tests** - `mvn test` in backend services
3. **Check Metrics** - http://localhost:8080/actuator/metrics
4. **View Logs** - `docker-compose logs -f`
5. **Explore API** - Try creating credit applications

---

## Support

For issues or questions:
1. Check [Troubleshooting](#troubleshooting) section
2. Review logs: `docker-compose logs`
3. Verify all prerequisites are installed
4. Ensure ports are not in use

**Version:** 1.0.0  
**Last Updated:** December 9, 2025
