# 📬 CoopCredit Postman Collection

Complete Postman collection for testing the CoopCredit Credit Application API.

## 📋 Contents

- **CoopCredit-API-Collection.json** - Main API collection with all endpoints
- **CoopCredit-Local-Environment.json** - Environment for local development
- **CoopCredit-Docker-Environment.json** - Environment for Docker deployment

## 🚀 Quick Start

### 1. Import Collection

1. Open Postman
2. Click **Import** button
3. Select `CoopCredit-API-Collection.json`
4. Collection appears in left sidebar

### 2. Import Environment

1. Click **Environments** (left sidebar)
2. Click **Import**
3. Select `CoopCredit-Local-Environment.json` (or Docker version)
4. Select imported environment from dropdown (top-right)

### 3. Start Testing!

Run requests in this order:

1. **Register Affiliate** - Creates user and saves JWT token automatically
2. **Get Affiliate by ID** - Uses saved token
3. **Create Credit Application** - Saves application ID
4. **Evaluate Credit Application** - Uses saved application ID

## 📁 Collection Structure

```
CoopCredit - Credit Application API
│
├── 🔐 Authentication
│   ├── Register Affiliate      (POST /api/auth/register)
│   └── Login                   (POST /api/auth/login)
│
├── 👥 Affiliates
│   ├── Get Affiliate by ID     (GET /api/affiliates/{id})
│   ├── Get All Affiliates      (GET /api/affiliates)
│   └── Update Affiliate        (PUT /api/affiliates/{id})
│
├── 💰 Credit Applications
│   ├── Create Application      (POST /api/credit-applications/affiliates/{id})
│   ├── Evaluate Application    (POST /api/credit-applications/{id}/evaluate)
│   └── Get by Affiliate        (GET /api/credit-applications/affiliates/{id})
│
└── 🏥 Health Check
    ├── Actuator Health         (GET /actuator/health)
    └── Actuator Metrics        (GET /actuator/metrics)
```

## 🔑 Authentication

### Automatic Token Handling

The collection includes **automatic JWT token management**:

1. **Register** or **Login** → Token saved to `{{jwt_token}}`
2. All authenticated endpoints use `{{jwt_token}}` automatically
3. No manual copy-paste needed!

### Manual Token Setup (if needed)

If token expires or gets lost:

1. Run **Login** request
2. Token automatically saved to environment
3. Check **Environments** tab to verify

## 🧪 Test Scripts

All requests include automated tests:

### Authentication Requests
- ✅ Status code validation (200/201)
- ✅ Token extraction and saving
- ✅ Response structure validation

### CRUD Requests
- ✅ Status code checks
- ✅ Response data validation
- ✅ Business rule verification

### View Test Results
- Click **Test Results** tab after sending request
- All tests show ✅ or ❌
- Console logs show detailed information

## 🌐 Environments

### Local Environment
```json
{
  "base_url": "http://localhost:8080",
  "jwt_token": "<auto-filled>",
  "username": "<auto-filled>",
  "application_id": "<auto-filled>"
}
```

**Use when:**
- Running with `mvn spring-boot:run`
- Testing locally without Docker

### Docker Environment
```json
{
  "base_url": "http://localhost:8080",
  "jwt_token": "<auto-filled>",
  "username": "<auto-filled>",
  "application_id": "<auto-filled>"
}
```

**Use when:**
- Running with `docker-compose up`
- Port 8080 exposed from container

**Note:** Both use localhost:8080 because Docker Compose maps container port to host

## 📝 Sample Test Flow

### Complete End-to-End Test

```bash
# 1. Register new affiliate
POST /api/auth/register
{
  "documentType": "CC",
  "documentNumber": "1017654321",
  "firstName": "Juan",
  "lastName": "Pérez",
  "email": "juan.perez@example.com",
  "phone": "3001234567",
  "salary": 5000000,
  "username": "juanperez",
  "password": "SecurePass123"
}
→ Token saved automatically

# 2. View affiliate details
GET /api/affiliates/1
→ Uses saved token

# 3. Create credit application
POST /api/credit-applications/affiliates/1
{
  "requestedAmount": 10000000,
  "purpose": "Compra de vivienda",
  "monthlyIncome": 5000000,
  "requestedTermMonths": 120
}
→ Application ID saved automatically

# 4. Evaluate application (need ANALYST role)
POST /api/credit-applications/{application_id}/evaluate
→ Uses saved application_id

# 5. Check application status
GET /api/credit-applications/affiliates/1
→ See APPROVED/REJECTED status
```

## 🎭 Role-Based Testing

### Default Registered User
**Role:** `ROLE_AFILIADO`

**Can access:**
- ✅ Own affiliate details
- ✅ Create credit applications
- ✅ View own applications

**Cannot access:**
- ❌ All affiliates list
- ❌ Update affiliates
- ❌ Evaluate applications

### Testing as ANALYST

To test analyst functions, use database or create ANALYST user:

```sql
-- In PostgreSQL
UPDATE users SET role = 'ROLE_ANALISTA' WHERE username = 'juanperez';
```

Then re-login to get new token with updated role.

### Testing as ADMIN

```sql
-- In PostgreSQL
UPDATE users SET role = 'ROLE_ADMIN' WHERE username = 'juanperez';
```

## 🐛 Troubleshooting

### 401 Unauthorized
**Problem:** JWT token expired or invalid

**Solution:**
1. Run **Login** request again
2. Token auto-refreshes
3. Retry failed request

### 403 Forbidden
**Problem:** User doesn't have required role

**Solution:**
- Check endpoint documentation for required role
- Update user role in database
- Re-login to get new token

### Connection Refused
**Problem:** Backend not running

**Solution:**
```bash
# Local
mvn spring-boot:run

# Docker
docker-compose up -d
```

### 404 Not Found - Affiliate/Application
**Problem:** ID doesn't exist

**Solution:**
- Use **Get All Affiliates** to find valid IDs
- Ensure you created affiliate/application first

## 📊 Environment Variables

| Variable | Auto-Filled? | Description |
|----------|--------------|-------------|
| `base_url` | No | API base URL (localhost:8080) |
| `jwt_token` | ✅ Yes | JWT authentication token |
| `username` | ✅ Yes | Logged-in username |
| `application_id` | ✅ Yes | Last created application ID |

## 🔄 Request Examples

### Create Affiliate with All Fields
```json
{
  "documentType": "CC",
  "documentNumber": "1017654321",
  "firstName": "María",
  "lastName": "González",
  "email": "maria.gonzalez@example.com",
  "phone": "3109876543",
  "salary": 7500000,
  "username": "mariagonzalez",
  "password": "SecurePass456"
}
```

### Update Affiliate
```json
{
  "firstName": "María Isabel",
  "lastName": "González Rodríguez",
  "email": "maria.isabel@example.com",
  "phone": "3159876543",
  "salary": 8000000,
  "status": "ACTIVE"
}
```

### Create Credit Application - Housing
```json
{
  "requestedAmount": 150000000,
  "purpose": "Compra de vivienda nueva",
  "monthlyIncome": 7500000,
  "requestedTermMonths": 240
}
```

### Create Credit Application - Vehicle
```json
{
  "requestedAmount": 30000000,
  "purpose": "Compra de vehículo",
  "monthlyIncome": 5000000,
  "requestedTermMonths": 60
}
```

## 📈 Performance Testing

### Check Response Times

All requests log response time in test console:

```javascript
console.log('⏱️ Response time:', pm.response.responseTime, 'ms');
```

**Expected response times:**
- Authentication: < 500ms
- Simple queries: < 200ms
- Credit evaluation: < 1000ms (calls external service)

### Run Collection with Newman (CLI)

```bash
# Install Newman
npm install -g newman

# Run entire collection
newman run CoopCredit-API-Collection.json \
  -e CoopCredit-Local-Environment.json \
  --reporters cli,html

# Run with iterations
newman run CoopCredit-API-Collection.json \
  -e CoopCredit-Local-Environment.json \
  -n 10 \
  --reporters cli,html
```

## 🎯 Next Steps

1. ✅ **Import collection and environment**
2. ✅ **Run Register request**
3. ✅ **Test all endpoints**
4. 🔄 **Modify test data as needed**
5. 📊 **Check Swagger UI** at http://localhost:8080/swagger-ui.html
6. 🎨 **Customize pre-request scripts** for your needs

## 🤝 Contributing

To add new endpoints:

1. Add request to appropriate folder
2. Include test scripts:
   ```javascript
   pm.test('Status code is 2xx', function () {
       pm.response.to.have.status(200);
   });
   ```
3. Update this README
4. Export updated collection

## 📚 Additional Resources

- [Postman Documentation](https://learning.postman.com/)
- [Swagger UI](http://localhost:8080/swagger-ui.html) - Interactive API docs
- [Spring Security](https://spring.io/projects/spring-security) - Authentication details

---

**Created by:** CoopCredit Development Team  
**Last Updated:** January 2025  
**Collection Version:** 1.0.0
