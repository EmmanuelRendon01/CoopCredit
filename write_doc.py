
import os

content = r"""# Hexagonal Architecture Diagram - CoopCredit

```mermaid
graph TB
    subgraph "EXTERNAL ACTORS"
        USER[User/Client]
        ADMIN[Administrator]
        RISK[Risk Central Service]
    end

    subgraph "INFRASTRUCTURE - Adapters IN (Controllers)"
        REST[REST Controllers<br/>AuthController<br/>AffiliateController<br/>CreditApplicationController]
        SEC[Security<br/>JwtAuthenticationFilter<br/>SecurityConfig]
    end

    subgraph "APPLICATION LAYER - Ports & Use Cases"
        PORTS_IN[Input Ports<br/>RegisterCreditApplicationUseCase<br/>EvaluateCreditApplicationUseCase<br/>GetCreditApplicationsUseCase<br/>ManageAffiliateUseCase<br/>AuthenticateUserUseCase]
        
        PORTS_OUT[Output Ports<br/>CreditApplicationRepositoryPort<br/>AffiliateRepositoryPort<br/>RiskEvaluationRepositoryPort<br/>RiskCentralPort<br/>UserRepositoryPort]
    end

    subgraph "DOMAIN LAYER - Business Logic"
        ENTITIES[Domain Models<br/>CreditApplication<br/>Affiliate<br/>RiskEvaluation]
        ENUMS[Enums<br/>ApplicationStatus<br/>AffiliateStatus<br/>RiskLevel]
        LOGIC[Business Rules<br/>calculateMonthlyPayment()<br/>exceedsDebtRatio()<br/>canRequestCredit()<br/>getMaxCreditAmount()]
    end

    subgraph "INFRASTRUCTURE - Adapters OUT (Persistence/External)"
        JPA[JPA Repositories<br/>AffiliateJpaRepository<br/>CreditApplicationJpaRepository<br/>RiskEvaluationJpaRepository<br/>UserJpaRepository]
        
        ENTITIES_JPA[JPA Entities<br/>AffiliateJpaEntity<br/>CreditApplicationJpaEntity<br/>RiskEvaluationJpaEntity<br/>UserJpaEntity]
        
        REST_CLIENT[REST Client<br/>RiskCentralRestAdapter<br/>Feign/RestTemplate]
        
        DB[(PostgreSQL<br/>Database)]
    end

    subgraph "CROSS-CUTTING"
        MAPPER[MapStruct Mappers<br/>AffiliateMapper<br/>CreditApplicationMapper<br/>RiskEvaluationMapper]
        DTO[DTOs<br/>Request/Response<br/>Objects]
        EXCEPTION[Exception Handling<br/>GlobalExceptionHandler<br/>BusinessException]
        OBSERVABILITY[Observability<br/>Prometheus Metrics<br/>Structured Logs<br/>Actuator]
    end

    %% User interactions
    USER --> REST
    ADMIN --> REST
    REST --> SEC
    SEC --> PORTS_IN

    %% Application to Domain
    PORTS_IN --> ENTITIES
    PORTS_IN --> LOGIC
    PORTS_IN --> PORTS_OUT

    %% Domain independence
    ENTITIES -.contains.-> ENUMS
    ENTITIES -.contains.-> LOGIC

    %% Output ports to adapters
    PORTS_OUT --> JPA
    PORTS_OUT --> REST_CLIENT

    %% Infrastructure adapters
    JPA --> ENTITIES_JPA
    ENTITIES_JPA --> DB
    REST_CLIENT --> RISK

    %% Mappers
    REST -.uses.-> MAPPER
    JPA -.uses.-> MAPPER
    MAPPER -.converts.-> DTO
    MAPPER -.converts.-> ENTITIES

    %% Exception handling
    REST --> EXCEPTION
    PORTS_IN --> EXCEPTION

    %% Observability
    REST -.logs/metrics.-> OBSERVABILITY
    PORTS_IN -.logs/metrics.-> OBSERVABILITY

    classDef domain fill:#FFE5B4,stroke:#FF8C00,stroke-width:3px
    classDef application fill:#E0F0FF,stroke:#0066CC,stroke-width:3px
    classDef infrastructure fill:#E0FFE0,stroke:#00AA00,stroke-width:3px
    classDef external fill:#FFE0E0,stroke:#CC0000,stroke-width:2px
    classDef crosscutting fill:#F0F0F0,stroke:#666666,stroke-width:2px

    class ENTITIES,ENUMS,LOGIC domain
    class PORTS_IN,PORTS_OUT application
    class REST,SEC,JPA,ENTITIES_JPA,REST_CLIENT,DB infrastructure
    class USER,ADMIN,RISK external
    class MAPPER,DTO,EXCEPTION,OBSERVABILITY crosscutting
```

## Layer Explanation

### 🟡 DOMAIN LAYER (Core - Business Logic)
- **Entities**: `CreditApplication`, `Affiliate`, `RiskEvaluation`
- **Enums**: `ApplicationStatus`, `AffiliateStatus`, `RiskLevel`
- **Business Logic**: 
  - Monthly payment calculation
  - Debt ratio validation
  - Affiliate eligibility
  - Credit limits

**Principle**: No external dependencies, pure business logic

### 🔵 APPLICATION LAYER (Use Cases)
- **Input Ports**: Use case interfaces
  - Register credit application
  - Evaluate application
  - Query applications
  - Manage affiliates
  - Authenticate users

- **Output Ports**: Interfaces for persistence and external services
  - Repositories (Affiliate, CreditApplication, RiskEvaluation, User)
  - REST Client for Risk Central

**Principle**: Defines WHAT the system does, not HOW

### 🟢 INFRASTRUCTURE LAYER (Technical Implementation)
**Adapters IN** (Input):
- REST Controllers (API endpoints)
- Security (JWT authentication)

**Adapters OUT** (Output):
- JPA Repositories (PostgreSQL persistence)
- REST Client (communication with Risk Central)
- JPA Entities (ORM mapping)

**Principle**: Implements HOW it is done, specific technology

### ⚙️ CROSS-CUTTING CONCERNS
- **MapStruct**: Domain ↔ DTO ↔ JPA Entity conversion
- **DTOs**: Request/Response for API
- **Exception Handling**: Global error handling
- **Observability**: Prometheus Metrics + Structured Logs

## Credit Application Flow

```
1. User → REST Controller (POST /api/credit-applications)
2. Controller → Security (validates JWT)
3. Controller → UseCase (RegisterCreditApplicationUseCase)
4. UseCase → Domain (validates business rules)
5. UseCase → Output Port (saves to repository)
6. Output Port → JPA Repository
7. JPA Repository → PostgreSQL
8. Response: JPA Entity → Mapper → DTO → Response
```

## Advantages of this Architecture

✅ **Testability**: Domain layer without dependencies  
✅ **Independence**: Changing DB or framework does not affect business logic  
✅ **Maintainability**: Clear separation of concerns  
✅ **Scalability**: Easy to add new adapters  
✅ **Clean Architecture**: Dependencies point towards the center (Domain)
"""

with open('/home/spring/IdeaProjects/CoopCredit/docs/diagrams/architecture-hexagonal.md', 'w') as f:
    f.write(content)
