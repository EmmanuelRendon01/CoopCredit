# CoopCredit - Sistema de Gestión de Créditos

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-green)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue)
![Docker](https://img.shields.io/badge/Docker-Ready-blue)
![License](https://img.shields.io/badge/License-MIT-yellow)

Sistema integral de gestión y evaluación automática de solicitudes de crédito para CoopCredit, implementado con arquitectura hexagonal, microservicios y seguridad robusta con JWT.

---

## 📋 Tabla de Contenidos

- [Descripción](#descripción)
- [Arquitectura](#arquitectura)
- [Tecnologías](#tecnologías)
- [Requisitos Previos](#requisitos-previos)
- [Instalación y Ejecución](#instalación-y-ejecución)
- [Endpoints API](#endpoints-api)
- [Roles y Permisos](#roles-y-permisos)
- [Métricas y Observabilidad](#métricas-y-observabilidad)
- [Testing](#testing)
- [Documentación Técnica](#documentación-técnica)

---

## 🎯 Descripción

CoopCredit es una cooperativa de ahorro y crédito que requiere digitalizar su proceso de solicitud y evaluación de créditos. Este sistema proporciona:

- **Gestión de Afiliados**: Registro y administración de miembros
- **Solicitudes de Crédito**: Creación y seguimiento de solicitudes
- **Evaluación Automática**: Integración con servicio externo de scoring
- **Seguridad Robusta**: Autenticación JWT y autorización por roles
- **Observabilidad**: Métricas de negocio y técnicas con Prometheus
- **Alta Disponibilidad**: Arquitectura de microservicios con Docker

---

## 🏗️ Arquitectura

### Arquitectura Hexagonal (Ports & Adapters)

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

### Diagrama de Microservicios

```
┌──────────────┐         ┌─────────────────────┐         ┌──────────────────┐
│              │         │  Credit Application │         │                  │
│   Cliente    │◄───────▶│      Service        │◄───────▶│   PostgreSQL     │
│  (Postman)   │   HTTP  │   (Puerto 8080)     │  JDBC   │   (Puerto 5432)  │
│              │         │                     │         │                  │
└──────────────┘         └──────────┬──────────┘         └──────────────────┘
                                    │
                                    │ HTTP
                                    │
                         ┌──────────▼──────────┐
                         │  Risk Central       │
                         │  Mock Service       │
                         │  (Puerto 8081)      │
                         └─────────────────────┘
```

---

## 🛠️ Tecnologías

### Backend Framework
- **Java 17** - LTS version con records y pattern matching
- **Spring Boot 3.2.0** - Framework empresarial
- **Spring Security 6** - Autenticación y autorización
- **Spring Data JPA** - Persistencia con Hibernate
- **Flyway** - Migraciones de base de datos

### Seguridad
- **JWT (jjwt 0.12.3)** - Tokens stateless con HS256
- **BCrypt** - Hashing de contraseñas

### Persistencia
- **PostgreSQL 15** - Base de datos relacional
- **Hibernate 6** - ORM con optimizaciones (EntityGraph, batch-size)
- **HikariCP** - Connection pooling

### Observabilidad
- **Spring Boot Actuator** - Health checks y métricas
- **Micrometer** - Métricas personalizadas
- **Prometheus** - Exportación de métricas (opcional)
- **Logback** - Logging estructurado JSON

### Testing
- **JUnit 5** - Framework de testing
- **Mockito** - Mocking de dependencias
- **MockMvc** - Testing de APIs REST
- **Testcontainers** - PostgreSQL en contenedor para tests
- **H2** - Base de datos en memoria para tests rápidos

### DevOps
- **Docker** - Containerización
- **Docker Compose** - Orquestación de servicios
- **Maven** - Gestión de dependencias y build

---

## 📦 Requisitos Previos

- **Java 17+** (JDK)
- **Maven 3.8+**
- **Docker 20+** y Docker Compose
- **PostgreSQL 15** (si ejecutas sin Docker)
- **Git**

---

## 🚀 Instalación y Ejecución

### Opción 1: Con Docker (Recomendado)

```bash
# 1. Clonar repositorio
git clone <repository-url>
cd CoopCredit

# 2. Construir servicios
docker-compose build

# 3. Iniciar todos los servicios
docker-compose up -d

# 4. Verificar estado
docker-compose ps

# 5. Ver logs
docker-compose logs -f credit-application-service
```

**Servicios disponibles:**
- Credit Application Service: http://localhost:8080
- Risk Central Service: http://localhost:8081
- PostgreSQL: localhost:5432

### Opción 2: Ejecución Local (Sin Docker)

```bash
# 1. Iniciar PostgreSQL
# Asegúrate de tener PostgreSQL corriendo en puerto 5432

# 2. Crear base de datos
createdb coopcredit

# 3. Compilar Risk Central Service
cd risk-central-service
mvn clean package
java -jar target/*.jar &
cd ..

# 4. Compilar Credit Application Service
cd credit-application-service
mvn clean package
java -jar target/*.jar
```

### Verificar Instalación

```bash
# Health Check - Credit Application Service
curl http://localhost:8080/actuator/health

# Health Check - Risk Central Service
curl http://localhost:8081/actuator/health

# Respuesta esperada:
{
  "status": "UP",
  "components": {
    "db": { "status": "UP" },
    "diskSpace": { "status": "UP" },
    "ping": { "status": "UP" }
  }
}
```

---

## 📡 Endpoints API

### 🔐 Autenticación

#### POST /api/auth/register
Registra un nuevo afiliado con usuario.

**Request:**
```json
{
  "username": "juan.perez",
  "password": "Secure123",
  "email": "juan.perez@example.com",
  "documentType": "CC",
  "documentNumber": "1234567890",
  "firstName": "Juan",
  "lastName": "Pérez",
  "phone": "3001234567",
  "salary": 5000000
}
```

**Response (201 Created):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "username": "juan.perez",
  "email": "juan.perez@example.com",
  "roles": ["AFFILIATE"]
}
```

#### POST /api/auth/login
Autentica usuario y genera token JWT.

**Request:**
```json
{
  "username": "juan.perez",
  "password": "Secure123"
}
```

**Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "username": "juan.perez",
  "email": "juan.perez@example.com",
  "roles": ["AFFILIATE"]
}
```

### 💳 Solicitudes de Crédito

#### POST /api/credit-applications/affiliates/{affiliateId}
Crea una nueva solicitud de crédito.

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
  "purpose": "Compra de vehículo"
}
```

**Response (201 Created):**
```json
{
  "id": 1,
  "affiliateId": 1,
  "affiliateName": "Juan Pérez",
  "requestedAmount": 10000000,
  "termMonths": 24,
  "interestRate": 1.5,
  "monthlyPayment": 416666.67,
  "status": "PENDING",
  "purpose": "Compra de vehículo",
  "applicationDate": "2025-12-09T10:30:00"
}
```

**Validaciones:**
- Monto: $1,000,000 - $50,000,000
- Plazo: 6-60 meses
- Ratio deuda/ingreso ≤ 50%
- Afiliación mínima: 6 meses
- Monto máximo: 5x salario mensual
- Sin solicitudes pendientes

#### POST /api/credit-applications/{applicationId}/evaluate
Evalúa una solicitud con el servicio de riesgo (Solo ANALYST).

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

**Estados posibles:**
- `APPROVED` - Score ≥ 700
- `REJECTED` - Score < 300
- `UNDER_REVIEW` - Score 300-699 (requiere revisión manual)

#### GET /api/credit-applications/affiliates/{affiliateId}
Obtiene todas las solicitudes de un afiliado.

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

### 📊 Observabilidad

#### GET /actuator/health
Estado de la aplicación y dependencias.

#### GET /actuator/metrics
Lista de métricas disponibles.

#### GET /actuator/metrics/{metric-name}
Detalle de métrica específica.

**Ejemplos:**
```bash
# Fallos de autenticación
curl http://localhost:8080/actuator/metrics/authentication.failures

# Solicitudes creadas
curl http://localhost:8080/actuator/metrics/credit.applications.created

# Tiempos de endpoints
curl http://localhost:8080/actuator/metrics/endpoint.execution.time
```

#### GET /actuator/prometheus
Métricas en formato Prometheus.

---

## 👥 Roles y Permisos

| Rol | Descripción | Permisos |
|-----|-------------|----------|
| **AFFILIATE** | Afiliado de la cooperativa | • Ver sus propias solicitudes<br>• Crear nuevas solicitudes<br>• Consultar su historial |
| **ANALYST** | Analista de crédito | • Ver todas las solicitudes<br>• Evaluar solicitudes pendientes<br>• Aprobar/rechazar créditos |
| **ADMIN** | Administrador del sistema | • Acceso total a todos los recursos<br>• Gestión de usuarios<br>• Configuración del sistema |

### Flujo de Autorización

```
┌─────────────┐    Register    ┌──────────────┐
│   Usuario   │───────────────▶│  AFFILIATE   │
│             │                 │   (default)  │
└─────────────┘                 └──────────────┘
                                       │
                                       │ Crear Solicitud
                                       ▼
┌──────────────┐                ┌──────────────┐
│   ANALYST    │──── Evaluar ──▶│  PENDING     │
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

### Usuarios de Prueba (creados por V3 migration)

| Username | Password | Rol | Descripción |
|----------|----------|-----|-------------|
| `admin` | `admin123` | ADMIN | Administrador del sistema |
| `analyst` | `analyst123` | ANALYST | Analista de crédito |
| `affiliate1` | `affiliate123` | AFFILIATE | Afiliado ejemplo |

---

## 📈 Métricas y Observabilidad

### Métricas de Negocio

| Métrica | Tipo | Descripción |
|---------|------|-------------|
| `credit.applications.created` | Counter | Total de solicitudes creadas |
| `credit.applications.approved` | Counter | Total de solicitudes aprobadas |
| `credit.applications.rejected` | Counter | Total de solicitudes rechazadas |

### Métricas Técnicas

| Métrica | Tipo | Descripción |
|---------|------|-------------|
| `endpoint.execution.time` | Timer | Tiempo de ejecución por endpoint |
| `authentication.failures` | Counter | Intentos fallidos de autenticación |
| `business.errors` | Counter | Violaciones de reglas de negocio |
| `validation.errors` | Counter | Errores de validación de DTOs |

### Métricas Automáticas (Spring Boot)

- `http.server.requests` - Peticiones HTTP (latencia, status codes)
- `jvm.memory.used` - Uso de memoria JVM
- `hikaricp.connections.active` - Conexiones DB activas
- `process.cpu.usage` - Uso de CPU

### Dashboard Ejemplo

```bash
# Tasa de aprobación
curl http://localhost:8080/actuator/metrics/credit.applications.approved
# Dividir entre credit.applications.created

# Latencia P95 de evaluación
curl "http://localhost:8080/actuator/metrics/endpoint.execution.time?tag=endpoint:evaluate-application"

# Intentos de login fallidos (últimos 5 min)
curl http://localhost:8080/actuator/metrics/authentication.failures
```

---

## 🧪 Testing

### Ejecutar Tests

```bash
# Todos los tests
mvn test

# Solo tests unitarios
mvn test -Dtest="*Test"

# Solo tests de integración
mvn test -Dtest="*IntegrationTest"

# Con Testcontainers
mvn test -Dtest="*TestcontainersTest"

# Con reporte de cobertura
mvn clean verify
# Ver reporte en: target/site/jacoco/index.html
```

### Cobertura de Tests

- **Tests Unitarios (Domain):** 
  - `AffiliateTest` - Lógica de límites de crédito
  - `CreditApplicationTest` - Cálculo de cuota y ratio deuda
  - `BusinessValidatorTest` - Validaciones de negocio

- **Tests Unitarios (Use Cases):**
  - `RegisterCreditApplicationUseCaseTest` - Mock de repositorios
  - `EvaluateCreditApplicationUseCaseTest` - Mock de RiskEvaluationPort

- **Tests de Integración:**
  - `AuthControllerIntegrationTest` - Flujo completo con MockMvc
  - `AffiliateRepositoryAdapterTestcontainersTest` - PostgreSQL en contenedor

### Ejemplo de Ejecución

```bash
cd credit-application-service

# Tests unitarios rápidos
mvn test -Dspring.profiles.active=test

# Tests con Testcontainers (requiere Docker)
mvn verify
```

---

## 📚 Documentación Técnica

### Documentos de Diseño

- **[FASE-1-ANALISIS-Y-DISENO.md](FASE-1-ANALISIS-Y-DISENO.md)** 
  - Arquitectura hexagonal
  - Diagramas de casos de uso
  - Identificación de puertos y adaptadores
  - Modelos de dominio

- **[FASE-2-PERSISTENCIA-AVANZADA.md](FASE-2-PERSISTENCIA-AVANZADA.md)**
  - Entidades JPA con relaciones
  - Migraciones Flyway
  - Optimizaciones (EntityGraph, batch-size)
  - Repositorios y adaptadores

- **[FASE-3-SEGURIDAD-Y-VALIDACIONES.md](FASE-3-SEGURIDAD-Y-VALIDACIONES.md)**
  - Implementación JWT
  - Spring Security configuration
  - Validaciones de negocio
  - Manejo de errores RFC 7807
  - Logging estructurado

- **[FASE-4-MICROSERVICIOS-Y-OBSERVABILIDAD.md](FASE-4-MICROSERVICIOS-Y-OBSERVABILIDAD.md)**
  - Risk Central Service con hash-based scoring
  - Integración REST entre servicios
  - Métricas obligatorias
  - Actuator y Prometheus

### Scripts y Comandos

- **[DOCKER-COMMANDS.md](DOCKER-COMMANDS.md)** - Comandos Docker útiles

### Migraciones de Base de Datos

```
src/main/resources/db/migration/
├── V1__create_schema.sql           # Tablas, constraints, indexes
├── V2__create_relationships.sql    # Foreign keys, cascades
└── V3__insert_initial_data.sql     # Roles, usuarios de prueba
```

---

## 🔒 Seguridad

### Configuración JWT

```properties
jwt.secret=CoopCreditSecretKeyForJWT2024MustBeLongEnoughForHS256Algorithm
jwt.expiration=86400000  # 24 horas
```

**Recomendaciones para producción:**
- Usar variables de entorno para el secret
- Rotar secrets periódicamente
- Implementar refresh tokens
- Configurar HTTPS obligatorio

### Endpoints Públicos

- `POST /api/auth/register`
- `POST /api/auth/login`
- `GET /actuator/health`

Todos los demás requieren autenticación JWT.

### Ejemplo de Petición Autenticada

```bash
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"juan.perez","password":"Secure123"}' \
  | jq -r '.token')

curl -H "Authorization: Bearer $TOKEN" \
  http://localhost:8080/api/credit-applications/affiliates/1
```

---

## 🐳 Docker

### Estructura de Contenedores

```yaml
services:
  postgres:          # Base de datos
    - Puerto: 5432
    - Volumen persistente
    
  risk-central-service:
    - Puerto: 8081
    - Health check cada 30s
    
  credit-application-service:
    - Puerto: 8080
    - Depende de: postgres, risk-central-service
    - Health check cada 30s
```

### Comandos Útiles

```bash
# Reconstruir sin caché
docker-compose build --no-cache

# Escalar servicios
docker-compose up -d --scale credit-application-service=2

# Ver recursos
docker stats

# Limpiar todo
docker-compose down -v --rmi all

# Acceder a contenedor
docker exec -it credit-application-service sh

# Ver logs en tiempo real
docker-compose logs -f --tail=100
```

---

## 🚧 Troubleshooting

### Error: Puerto ya en uso

```bash
# Verificar qué usa el puerto
netstat -ano | findstr :8080

# Detener servicios Docker
docker-compose down
```

### Error: Base de datos no conecta

```bash
# Verificar estado de PostgreSQL
docker-compose ps postgres

# Ver logs
docker-compose logs postgres

# Reiniciar solo PostgreSQL
docker-compose restart postgres
```

### Error: Tests fallan con Testcontainers

```bash
# Verificar Docker está corriendo
docker info

# Limpiar contenedores de test
docker rm -f $(docker ps -a -q --filter "label=org.testcontainers")
```

---

## 🎯 Colección Postman

### Importar Colección

1. Abrir Postman
2. Import → Raw text
3. Pegar el JSON siguiente:

```json
{
  "info": {
    "name": "CoopCredit API",
    "description": "Colección completa de endpoints de CoopCredit",
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
              "raw": "{\n  \"username\": \"juan.perez\",\n  \"password\": \"Secure123\",\n  \"email\": \"juan.perez@example.com\",\n  \"documentType\": \"CC\",\n  \"documentNumber\": \"1234567890\",\n  \"firstName\": \"Juan\",\n  \"lastName\": \"Pérez\",\n  \"phone\": \"3001234567\",\n  \"salary\": 5000000\n}",
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
              "raw": "{\n  \"username\": \"juan.perez\",\n  \"password\": \"Secure123\"\n}",
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
              "raw": "{\n  \"requestedAmount\": 10000000,\n  \"termMonths\": 24,\n  \"interestRate\": 1.5,\n  \"monthlyIncome\": 5000000,\n  \"currentDebt\": 500000,\n  \"purpose\": \"Compra de vehículo\"\n}",
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

### Flujo de Prueba Recomendado

1. **Register** → Guarda el token automáticamente
2. **Create Application** → Crea solicitud con el token
3. **Login como ANALYST** (username: `analyst`, password: `analyst123`)
4. **Evaluate Application** → Evalúa la solicitud creada
5. **Get Applications** → Verifica el resultado

---

## 📞 Soporte y Contribución

### Reportar Bugs

Crea un issue en GitHub con:
1. Descripción del problema
2. Pasos para reproducir
3. Logs relevantes
4. Versión de Java y Docker

### Contribuir

1. Fork del repositorio
2. Crear branch (`git checkout -b feature/nueva-funcionalidad`)
3. Commit cambios (`git commit -am 'Agregar funcionalidad'`)
4. Push al branch (`git push origin feature/nueva-funcionalidad`)
5. Crear Pull Request

---

## 📄 Licencia

Este proyecto es privado y confidencial. Todos los derechos reservados.

---

## 👨‍💻 Autor

**CoopCredit Development Team**

Para sustentación técnica y consultas, contactar al equipo de desarrollo.

---

## 🎯 Checklist de Implementación

- [x] Arquitectura Hexagonal
- [x] Microservicios (Credit Application + Risk Central)
- [x] Seguridad JWT stateless
- [x] Persistencia JPA con optimizaciones
- [x] Migraciones Flyway
- [x] Manejo de errores RFC 7807
- [x] Logging estructurado
- [x] Métricas de negocio y técnicas
- [x] Actuator + Prometheus
- [x] Pruebas unitarias
- [x] Pruebas de integración
- [x] Testcontainers
- [x] Docker multi-stage
- [x] docker-compose completo
- [x] Documentación profesional

---

**Versión:** 1.0.0  
**Fecha:** Diciembre 9, 2025  
**Estado:** ✅ Producción Ready
