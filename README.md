# CoopCredit - Credit Management System

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-green)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue)
![Docker](https://img.shields.io/badge/Docker-Ready-blue)
![License](https://img.shields.io/badge/License-MIT-yellow)


Comprehensive credit application management and automated evaluation system for CoopCredit, implemented with hexagonal architecture, microservices, and robust JWT security.

---

## 📋 Table of Contents

- [Description](#description)
- [Architecture](#architecture)
- [Technologies](#technologies)
- [Prerequisites](#prerequisites)
- [Installation and Execution](#installation-and-execution)
- [API Endpoints](#api-endpoints)
- [Roles and Permissions](#roles-and-permissions)
- [Metrics and Observability](#metrics-and-observability)
- [Testing](#testing)
- [Technical Documentation](#technical-documentation)

---

## 🎯 Description

CoopCredit is a savings and credit cooperative that requires digitalization of its credit application and evaluation process. This system provides:

- **Affiliate Management**: Member registration and administration
- **Credit Applications**: Application creation and tracking
- **Automated Evaluation**: Integration with external scoring service
- **Robust Security**: JWT authentication and role-based authorization
- **Observability**: Business and technical metrics with Prometheus
- **High Availability**: Microservices architecture with Docker

---

## 🏗️ Architecture

### Hexagonal Architecture (Ports & Adapters)

```
┌─────────────────────────────────────────────────────────────┐
│                   CREDIT APPLICATION SERVICE                 │
├─────────────────────────────────────────────────────────────┤
│                                                               │
│  ┌───────────────────────────────────────────────────────┐  │
│  │           ADAPTERS IN (Primary/Driving)               │  │
│  │  ┌────────────────┐  ┌──────────────────────────┐    │  │
│  │  │ REST           │  │ Security Filter          │    │  │
│  │  │ Controllers    │  │ (JWT Authentication)     │    │  │
│  │  └────────────────┘  └──────────────────────────┘    │  │
│  └───────────────────────────────────────────────────────┘  │
│                          ▼                                   │
│  ┌───────────────────────────────────────────────────────┐  │
│  │                  APPLICATION LAYER                     │  │
│  │  ┌──────────────────────────────────────────────────┐ │  │
│  │  │  Use Cases (Business Logic)                      │ │  │
│  │  │  • RegisterAffiliate                             │ │  │
│  │  │  • RegisterCreditApplication                     │ │  │
│  │  │  • EvaluateCreditApplication                     │ │  │
│  │  │  • AuthenticateUser                              │ │  │
│  │  └──────────────────────────────────────────────────┘ │  │
│  └───────────────────────────────────────────────────────┘  │
│                          ▼                                   │
│  ┌───────────────────────────────────────────────────────┐  │
│  │                    DOMAIN LAYER                        │  │
│  │  ┌──────────────────────────────────────────────────┐ │  │
│  │  │  Models (POJOs - No framework dependencies)      │ │  │
│  │  │  • Affiliate                                     │ │  │
│  │  │  • CreditApplication                             │ │  │
│  │  │  • RiskEvaluation                                │ │  │
│  │  │  • User, Role                                    │ │  │
│  │  └──────────────────────────────────────────────────┘ │  │
│  │  ┌──────────────────────────────────────────────────┐ │  │
│  │  │  Ports (Interfaces)                              │ │  │
│  │  │  IN:  RegisterCreditApplicationUseCase           │ │  │
│  │  │  OUT: AffiliateRepositoryPort                    │ │  │
│  │  │       RiskEvaluationPort                         │ │  │
│  │  └──────────────────────────────────────────────────┘ │  │
│  └───────────────────────────────────────────────────────┘  │
│                          ▼                                   │
│  ┌───────────────────────────────────────────────────────┐  │
│  │         ADAPTERS OUT (Secondary/Driven)               │  │
│  │  ┌─────────────────┐  ┌────────────────────────────┐ │  │
│  │  │ JPA Persistence │  │ REST Client                │ │  │
│  │  │ (PostgreSQL)    │  │ (Risk Central Service)     │ │  │
│  │  └─────────────────┘  └────────────────────────────┘ │  │
│  │  ┌─────────────────┐  ┌────────────────────────────┐ │  │
│  │  │ JWT Security    │  │ Metrics                    │ │  │
│  │  │ (Token Gen)     │  │ (Micrometer)               │ │  │
│  │  └─────────────────┘  └────────────────────────────┘ │  │
│  └───────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
                          │
                          │ HTTP
                          ▼
┌─────────────────────────────────────────────────────────────┐
│                   RISK CENTRAL SERVICE                       │
│               (Mock External Scoring Service)                │
│                                                               │
│  POST /api/risk-evaluation/evaluate                          │
│  • Hash-based consistent scoring                             │
│  • Document → Score (0-1000)                                 │
│  • Risk factors analysis                                     │
└─────────────────────────────────────────────────────────────┘
```

### Microservices Diagram

```
┌──────────────┐         ┌─────────────────────┐         ┌──────────────────┐
│              │         │  Credit Application │         │                  │
│   Client     │◄───────▶│      Service        │◄───────▶│   PostgreSQL     │
│  (Postman)   │   HTTP  │   (Port 8080)       │  JDBC   │   (Port 5432)    │
│              │         │                     │         │                  │
└──────────────┘         └──────────┬──────────┘         └──────────────────┘
                                    │
                                    │ HTTP
                                    │
                         ┌──────────▼──────────┐
                         │  Risk Central       │
                         │  Mock Service       │
                         │  (Port 8081)        │
                         └─────────────────────┘
```

---

## 🛠️ Technologies

### Backend Framework
- **Java 17** - LTS version with records and pattern matching
- **Spring Boot 3.2.0** - Enterprise framework
- **Spring Security 6** - Authentication and authorization
- **Spring Data JPA** - Persistence with Hibernate
- **Flyway** - Database migrations

### Security
- **JWT (jjwt 0.12.3)** - Stateless tokens with HS256
- **BCrypt** - Password hashing

### Persistence
- **PostgreSQL 15** - Relational database
- **Hibernate 6** - ORM with optimizations (EntityGraph, batch-size)
- **HikariCP** - Connection pooling

### Observability
- **Spring Boot Actuator** - Health checks and metrics
- **Micrometer** - Custom metrics
- **Prometheus** - Metrics export (optional)
- **Logback** - Structured JSON logging

### Testing
- **JUnit 5** - Testing framework
- **Mockito** - Dependency mocking
- **MockMvc** - REST API testing
- **Testcontainers** - PostgreSQL in container for tests
- **H2** - In-memory database for fast tests

### DevOps
- **Docker** - Containerization
- **Docker Compose** - Service orchestration
- **Maven** - Dependency and build management

---

## 📦 Prerequisites

- **Java 17+** (JDK)
- **Maven 3.8+**
- **Docker 20+** and Docker Compose
- **PostgreSQL 15** (if running without Docker)
- **Git**
- **Node.js** (optional, for frontend development)

---

## 🚀 Installation and Execution

### 🎯 Quick Start

```bash
# 1. Clone repository
git clone https://github.com/EmmanuelRendon01/CoopCredit.git
cd CoopCredit

# 2. Start all backend services
docker-compose up -d

# 3. Start frontend (in new terminal)
cd CoopCreditFront
sudo docker build -t coopcredit-frontend .
sudo docker run -d -p 3001:3001 --name coopcredit-frontend coopcredit-frontend

# 4. Access the application
# Frontend: http://localhost:3001
# Backend: http://localhost:8080
```

### 📚 Complete Deployment Guide

For detailed step-by-step instructions, multiple deployment options, and troubleshooting, see:

**[📖 DEPLOYMENT_GUIDE.md](DEPLOYMENT_GUIDE.md)** - Complete deployment and execution guide

This guide covers:
- ✅ Full stack deployment with Docker
- ✅ Backend-only deployment
- ✅ Local development setup
- ✅ Frontend deployment options
- ✅ Verification steps
- ✅ Troubleshooting common issues
- ✅ Production deployment recommendations

### Services and Ports

| Service | Port | URL |
|---------|------|-----|
| **Frontend** | 3001 | http://localhost:3001 |
| **Backend API** | 8080 | http://localhost:8080 |
| **Risk Service** | 8081 | http://localhost:8081 |
| **PostgreSQL** | 5432 | localhost:5432 |

### Test Users

| Username | Password | Role |
|----------|----------|------|
| `admin` | `Admin123!` | ADMIN |
| `analyst` | `Analyst123!` | ANALYST |
| `anam` | `Affiliate123!` | AFFILIATE |

---

## 📡 API Endpoints

### 🔐 Authentication

#### POST /api/auth/register
Registers a new affiliate with user credentials.

**Request:**
```json
{
  "username": "john.doe",
  "password": "Secure123",
  "email": "john.doe@example.com",
  "documentType": "CC",
  "documentNumber": "1234567890",
  "firstName": "John",
  "lastName": "Doe",
  "phone": "3001234567",
  "salary": 5000000
}
```

**Response (201 Created):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "username": "john.doe",
  "email": "john.doe@example.com",
  "roles": ["AFFILIATE"]
}
```

#### POST /api/auth/login
Authenticates user and generates JWT token.

**Request:**
```json
{
  "username": "john.doe",
  "password": "Secure123"
}
```

**Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "username": "john.doe",
  "email": "john.doe@example.com",
  "roles": ["AFFILIATE"]
}
```

### 💳 Credit Applications

#### POST /api/credit-applications/affiliates/{affiliateId}
Creates a new credit application.

**Headers:**
```
Authorization: Bearer {token}
Content-Type: application/json
```

**Request:**
```json
{
  "requestedAmount": 10000000,
  "termMonths": 24,
  "interestRate": 1.5,
  "monthlyIncome": 5000000,
  "currentDebt": 500000,
  "purpose": "Vehicle purchase"
}
```

**Response (201 Created):**
```json
{
  "id": 1,
  "affiliateId": 1,
  "affiliateName": "John Doe",
  "requestedAmount": 10000000,
  "termMonths": 24,
  "interestRate": 1.5,
  "monthlyPayment": 416666.67,
  "status": "PENDING",
  "purpose": "Vehicle purchase",
  "applicationDate": "2025-12-09T10:30:00"
}
```

**Validations:**
- Amount: $1,000,000 - $50,000,000
- Term: 6-60 months
- Debt-to-income ratio ≤ 50%
- Minimum membership: 6 months
- Maximum amount: 5x monthly salary
- No pending applications

#### POST /api/credit-applications/{applicationId}/evaluate
Evaluates an application with the risk service (ANALYST only).

**Headers:**
```
Authorization: Bearer {analyst-token}
```

**Response (200 OK):**
```json
{
  "id": 1,
  "status": "APPROVED",
  "creditScore": 725,
  "riskLevel": "LOW",
  "evaluationDate": "2025-12-09T10:35:00",
  "evaluationComments": "Credit Score: 725 | Risk Level: LOW | Recommendation: APPROVE | Factors: Excellent debt-to-income ratio, High income level"
}
```

**Possible statuses:**
- `APPROVED` - Score ≥ 700
- `REJECTED` - Score < 300
- `UNDER_REVIEW` - Score 300-699 (requires manual review)

#### GET /api/credit-applications/affiliates/{affiliateId}
Gets all applications for an affiliate.

**Headers:**
```
Authorization: Bearer {token}
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "status": "APPROVED",
    "requestedAmount": 10000000,
    "creditScore": 725,
    ...
  },
  {
    "id": 2,
    "status": "PENDING",
    "requestedAmount": 5000000,
    ...
  }
]
```

### 📊 Observability

#### GET /actuator/health
Application and dependencies health status.

#### GET /actuator/metrics
List of available metrics.

#### GET /actuator/metrics/{metric-name}
Detail of specific metric.

**Examples:**
```bash
# Authentication failures
curl http://localhost:8080/actuator/metrics/authentication.failures

# Applications created
curl http://localhost:8080/actuator/metrics/credit.applications.created

# Endpoint execution time
curl http://localhost:8080/actuator/metrics/endpoint.execution.time
```

#### GET /actuator/prometheus
Metrics in Prometheus format.

---

## 👥 Roles and Permissions

| Role | Description | Permissions |
|-----|-------------|----------|
| **AFFILIATE** | Cooperative member | • View own applications<br>• Create new applications<br>• Check own history |
| **ANALYST** | Credit analyst | • View all applications<br>• Evaluate pending applications<br>• Approve/reject credits |
| **ADMIN** | System administrator | • Full access to all resources<br>• User management<br>• System configuration |

### Authorization Flow

```
┌─────────────┐    Register    ┌──────────────┐
│    User     │───────────────▶│  AFFILIATE   │
│             │                 │   (default)  │
└─────────────┘                 └──────────────┘
                                       │
                                       │ Create Application
                                       ▼
┌──────────────┐                ┌──────────────┐
│   ANALYST    │──── Evaluate ──▶│  PENDING     │
│              │                 │ Application  │
└──────────────┘                 └──────────────┘
       │                                │
       │                                │
       ▼                                ▼
┌──────────────┐                ┌──────────────┐
│  APPROVED /  │                │  UNDER_      │
│   REJECTED   │                │   REVIEW     │
└──────────────┘                └──────────────┘
```

### Test Users (created by V3 migration)

| Username | Password | Role | Description |
|----------|----------|-----|-------------|
| `admin` | `admin123` | ADMIN | System administrator |
| `analyst` | `analyst123` | ANALYST | Credit analyst |
| `affiliate1` | `affiliate123` | AFFILIATE | Sample affiliate |

---

## 📈 Metrics and Observability

### Business Metrics

| Metric | Type | Description |
|---------|------|-------------|
| `credit.applications.created` | Counter | Total applications created |
| `credit.applications.approved` | Counter | Total applications approved |
| `credit.applications.rejected` | Counter | Total applications rejected |

### Technical Metrics

| Metric | Type | Description |
|---------|------|-------------|
| `endpoint.execution.time` | Timer | Execution time per endpoint |
| `authentication.failures` | Counter | Failed authentication attempts |
| `business.errors` | Counter | Business rule violations |
| `validation.errors` | Counter | DTO validation errors |

### Automatic Metrics (Spring Boot)

- `http.server.requests` - HTTP requests (latency, status codes)
- `jvm.memory.used` - JVM memory usage
- `hikaricp.connections.active` - Active DB connections
- `process.cpu.usage` - CPU usage

### Example Dashboard

```bash
# Approval rate
curl http://localhost:8080/actuator/metrics/credit.applications.approved
# Divide by credit.applications.created

# P95 evaluation latency
curl "http://localhost:8080/actuator/metrics/endpoint.execution.time?tag=endpoint:evaluate-application"

# Failed login attempts (last 5 min)
curl http://localhost:8080/actuator/metrics/authentication.failures
```

---

## 🧪 Testing

### Run Tests

```bash
# All tests
mvn test

# Unit tests only
mvn test -Dtest="*Test"

# Integration tests only
mvn test -Dtest="*IntegrationTest"

# With Testcontainers
mvn test -Dtest="*TestcontainersTest"

# With coverage report
mvn clean verify
# View report at: target/site/jacoco/index.html
```

### Test Coverage

- **Unit Tests (Domain):** 
  - `AffiliateTest` - Credit limit logic
  - `CreditApplicationTest` - Payment and debt ratio calculation
  - `BusinessValidatorTest` - Business validations

- **Unit Tests (Use Cases):**
  - `RegisterCreditApplicationUseCaseTest` - Repository mocking
  - `EvaluateCreditApplicationUseCaseTest` - RiskEvaluationPort mocking

- **Integration Tests:**
  - `AuthControllerIntegrationTest` - Complete flow with MockMvc
  - `AffiliateRepositoryAdapterTestcontainersTest` - PostgreSQL in container

### Example Execution

```bash
cd credit-application-service

# Fast unit tests
mvn test -Dspring.profiles.active=test

# Tests with Testcontainers (requires Docker)
mvn verify
```

---

## 📚 Technical Documentation

### Design Documents

- **[FASE-1-ANALISIS-Y-DISENO.md](FASE-1-ANALISIS-Y-DISENO.md)** 
  - Hexagonal architecture
  - Use case diagrams
  - Ports and adapters identification
  - Domain models

- **[FASE-2-PERSISTENCIA-AVANZADA.md](FASE-2-PERSISTENCIA-AVANZADA.md)**
  - JPA entities with relationships
  - Flyway migrations
  - Optimizations (EntityGraph, batch-size)
  - Repositories and adapters

- **[FASE-3-SEGURIDAD-Y-VALIDACIONES.md](FASE-3-SEGURIDAD-Y-VALIDACIONES.md)**
  - JWT implementation
  - Spring Security configuration
  - Business validations
  - RFC 7807 error handling
  - Structured logging

- **[FASE-4-MICROSERVICIOS-Y-OBSERVABILIDAD.md](FASE-4-MICROSERVICIOS-Y-OBSERVABILIDAD.md)**
  - Risk Central Service with hash-based scoring
  - REST integration between services
  - Required metrics
  - Actuator and Prometheus

### Scripts and Commands

- **[DOCKER-COMMANDS.md](DOCKER-COMMANDS.md)** - Useful Docker commands

### Database Migrations

```
src/main/resources/db/migration/
├── V1__create_schema.sql           # Tables, constraints, indexes
├── V2__create_relationships.sql    # Foreign keys, cascades
└── V3__insert_initial_data.sql     # Roles, test users
```

---

## 🔒 Security

### JWT Configuration

```properties
jwt.secret=CoopCreditSecretKeyForJWT2024MustBeLongEnoughForHS256Algorithm
jwt.expiration=86400000  # 24 hours
```

**Production recommendations:**
- Use environment variables for the secret
- Rotate secrets periodically
- Implement refresh tokens
- Configure mandatory HTTPS

### Public Endpoints

- `POST /api/auth/register`
- `POST /api/auth/login`
- `GET /actuator/health`

All others require JWT authentication.

### Authenticated Request Example

```bash
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"john.doe","password":"Secure123"}' \
  | jq -r '.token')

curl -H "Authorization: Bearer $TOKEN" \
  http://localhost:8080/api/credit-applications/affiliates/1
```

---

## 🐳 Docker

### Container Structure

```yaml
services:
  postgres:          # Database
    - Port: 5432
    - Persistent volume
    
  risk-central-service:
    - Port: 8081
    - Health check every 30s
    
  credit-application-service:
    - Port: 8080
    - Depends on: postgres, risk-central-service
    - Health check every 30s
```

### Useful Commands

```bash
# Rebuild without cache
docker-compose build --no-cache

# Scale services
docker-compose up -d --scale credit-application-service=2

# View resources
docker stats

# Clean everything
docker-compose down -v --rmi all

# Access container
docker exec -it credit-application-service sh

# View logs in real time
docker-compose logs -f --tail=100
```

---

## 🚧 Troubleshooting

### Error: Port already in use

```bash
# Check what's using the port
netstat -ano | findstr :8080

# Stop Docker services
docker-compose down
```

### Error: Database doesn't connect

```bash
# Check PostgreSQL status
docker-compose ps postgres

# View logs
docker-compose logs postgres

# Restart PostgreSQL only
docker-compose restart postgres
```

### Error: Tests fail with Testcontainers

```bash
# Verify Docker is running
docker info

# Clean test containers
docker rm -f $(docker ps -a -q --filter "label=org.testcontainers")
```

---

## 🎯 Postman Collection

### Import Collection

1. Open Postman
2. Import → Raw text
3. Paste the following JSON:

```json
{
  "info": {
    "name": "CoopCredit API",
    "description": "Complete collection of CoopCredit endpoints",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "variable": [
    {
      "key": "baseUrl",
      "value": "http://localhost:8080"
    },
    {
      "key": "token",
      "value": ""
    }
  ],
  "item": [
    {
      "name": "Auth",
      "item": [
        {
          "name": "Register Affiliate",
          "event": [
            {
              "listen": "test",
              "script": {
                "exec": [
                  "if (pm.response.code === 201) {",
                  "    pm.collectionVariables.set('token', pm.response.json().token);",
                  "}"
                ]
              }
            }
          ],
          "request": {
            "method": "POST",
            "header": [],
            "body": {
              "mode": "raw",
              "raw": "{\n  \"username\": \"john.doe\",\n  \"password\": \"Secure123\",\n  \"email\": \"john.doe@example.com\",\n  \"documentType\": \"CC\",\n  \"documentNumber\": \"1234567890\",\n  \"firstName\": \"John\",\n  \"lastName\": \"Doe\",\n  \"phone\": \"3001234567\",\n  \"salary\": 5000000\n}",
              "options": {
                "raw": {
                  "language": "json"
                }
              }
            },
            "url": {
              "raw": "{{baseUrl}}/api/auth/register",
              "host": ["{{baseUrl}}"],
              "path": ["api", "auth", "register"]
            }
          }
        },
        {
          "name": "Login",
          "event": [
            {
              "listen": "test",
              "script": {
                "exec": [
                  "if (pm.response.code === 200) {",
                  "    pm.collectionVariables.set('token', pm.response.json().token);",
                  "}"
                ]
              }
            }
          ],
          "request": {
            "method": "POST",
            "header": [],
            "body": {
              "mode": "raw",
              "raw": "{\n  \"username\": \"john.doe\",\n  \"password\": \"Secure123\"\n}",
              "options": {
                "raw": {
                  "language": "json"
                }
              }
            },
            "url": {
              "raw": "{{baseUrl}}/api/auth/login",
              "host": ["{{baseUrl}}"],
              "path": ["api", "auth", "login"]
            }
          }
        }
      ]
    },
    {
      "name": "Credit Applications",
      "item": [
        {
          "name": "Create Application",
          "request": {
            "method": "POST",
            "header": [
              {
                "key": "Authorization",
                "value": "Bearer {{token}}"
              }
            ],
            "body": {
              "mode": "raw",
              "raw": "{\n  \"requestedAmount\": 10000000,\n  \"termMonths\": 24,\n  \"interestRate\": 1.5,\n  \"monthlyIncome\": 5000000,\n  \"currentDebt\": 500000,\n  \"purpose\": \"Vehicle purchase\"\n}",
              "options": {
                "raw": {
                  "language": "json"
                }
              }
            },
            "url": {
              "raw": "{{baseUrl}}/api/credit-applications/affiliates/1",
              "host": ["{{baseUrl}}"],
              "path": ["api", "credit-applications", "affiliates", "1"]
            }
          }
        },
        {
          "name": "Evaluate Application",
          "request": {
            "method": "POST",
            "header": [
              {
                "key": "Authorization",
                "value": "Bearer {{token}}"
              }
            ],
            "url": {
              "raw": "{{baseUrl}}/api/credit-applications/1/evaluate",
              "host": ["{{baseUrl}}"],
              "path": ["api", "credit-applications", "1", "evaluate"]
            }
          }
        },
        {
          "name": "Get Applications by Affiliate",
          "request": {
            "method": "GET",
            "header": [
              {
                "key": "Authorization",
                "value": "Bearer {{token}}"
              }
            ],
            "url": {
              "raw": "{{baseUrl}}/api/credit-applications/affiliates/1",
              "host": ["{{baseUrl}}"],
              "path": ["api", "credit-applications", "affiliates", "1"]
            }
          }
        }
      ]
    },
    {
      "name": "Metrics",
      "item": [
        {
          "name": "Health Check",
          "request": {
            "method": "GET",
            "url": {
              "raw": "{{baseUrl}}/actuator/health",
              "host": ["{{baseUrl}}"],
              "path": ["actuator", "health"]
            }
          }
        },
        {
          "name": "All Metrics",
          "request": {
            "method": "GET",
            "url": {
              "raw": "{{baseUrl}}/actuator/metrics",
              "host": ["{{baseUrl}}"],
              "path": ["actuator", "metrics"]
            }
          }
        },
        {
          "name": "Applications Created",
          "request": {
            "method": "GET",
            "url": {
              "raw": "{{baseUrl}}/actuator/metrics/credit.applications.created",
              "host": ["{{baseUrl}}"],
              "path": ["actuator", "metrics", "credit.applications.created"]
            }
          }
        }
      ]
    }
  ]
}
```

### Recommended Test Flow

1. **Register** → Automatically saves the token
2. **Create Application** → Creates application with token
3. **Login as ANALYST** (username: `analyst`, password: `analyst123`)
4. **Evaluate Application** → Evaluates the created application
5. **Get Applications** → Verify the result

---

## 📞 Support and Contribution

### Report Bugs

Create an issue on GitHub with:
1. Problem description
2. Steps to reproduce
3. Relevant logs
4. Java and Docker version

### Contributing

1. Fork the repository
2. Create branch (`git checkout -b feature/new-feature`)
3. Commit changes (`git commit -am 'Add feature'`)
4. Push to branch (`git push origin feature/new-feature`)
5. Create Pull Request

---

## 📄 License

This project is private and confidential. All rights reserved.

---

## 👨‍💻 Author

**Emmanuel Rendon Goez**

For technical support and inquiries, contact the Emmanuel.

## Github: https://github.com/EmmanuelRendon01/CoopCredit

---

## 🎯 Implementation Checklist

- [x] Hexagonal Architecture
- [x] Microservices (Credit Application + Risk Central)
- [x] Stateless JWT Security
- [x] JPA Persistence with optimizations
- [x] Flyway Migrations
- [x] RFC 7807 Error Handling
- [x] Structured Logging
- [x] Business and Technical Metrics
- [x] Actuator + Prometheus
- [x] Unit Tests
- [x] Integration Tests
- [x] Testcontainers
- [x] Multi-stage Docker
- [x] Complete docker-compose
- [x] Professional Documentation

---

**Version:** 1.0.0  
**Date:** December 9, 2025  
**Status:** ✅ Production Ready
