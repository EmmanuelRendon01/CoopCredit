# CoopCredit Frontend

Frontend application for CoopCredit credit management system. Built with Vanilla JavaScript and Tailwind CSS.

## 🚀 Features

- **Authentication**: Login and registration with JWT
- **Role-Based Access**: Different dashboards for AFFILIATE and ANALYST
- **Affiliate Dashboard**: View credit applications and statistics
- **Analyst Dashboard**: Evaluate pending credit applications
- **New Applications**: Create credit applications with real-time calculations
- **Application Evaluation**: Analysts can evaluate and approve/reject applications
- **Responsive Design**: Works on desktop and mobile devices
- **Modern UI**: Glassmorphism effects and smooth animations

## 🛠️ Technology Stack

- **HTML5**: Semantic markup
- **Tailwind CSS**: Utility-first CSS framework (via CDN)
- **Vanilla JavaScript**: No frameworks, pure JS
- **Nginx**: Static file server for production

## 📁 Project Structure

```
CoopCreditFront/
├── index.html              # Login page
├── pages/
│   ├── register.html       # Registration page
│   ├── dashboard.html      # Affiliate dashboard
│   ├── analyst-dashboard.html # Analyst dashboard
│   └── new-application.html # New credit application form
├── js/
│   ├── api.js             # API client
│   ├── auth.js            # Authentication utilities
│   ├── storage.js         # LocalStorage manager
│   ├── utils.js           # Utility functions
│   ├── login.js           # Login page logic
│   ├── register.js        # Register page logic
│   ├── dashboard.js       # Affiliate dashboard logic
│   ├── analyst-dashboard.js # Analyst dashboard logic
│   └── new-application.js # New application logic
├── css/
│   └── styles.css         # Custom styles
├── Dockerfile             # Docker configuration
└── nginx.conf             # Nginx configuration
```

## 🏃 Running Locally

### Option 1: Direct File Access (Simplest)

Just open `index.html` in your browser:

```bash
# Using Python's built-in server
cd CoopCreditFront
python3 -m http.server 3000

# Or using Node.js http-server
npx http-server -p 3000
```

Then open: http://localhost:3000

### Option 2: With Docker

```bash
# Build image
docker build -t coopcredit-frontend .

# Run container
docker run -d -p 3001:3001 --name coopcredit-frontend coopcredit-frontend

# Access at http://localhost:3001
```

Or using docker-compose:

```bash
docker-compose up -d

# Access at http://localhost:3001
```

## 🔧 Configuration

### API Base URL

The API base URL is configured in `js/api.js`:

```javascript
const API_BASE_URL = 'http://localhost:8080/api';
```

Change this if your backend is running on a different URL.

## 📝 Usage

### 1. Login

Use one of the test users:
- **Admin**: `admin` / `admin123`
- **Analyst**: `analyst` / `analyst123`
- **Affiliate**: `affiliate1` / `affiliate123`

### 2. Register

Create a new affiliate account with:
- Username (min 3 characters)
- Email
- Password (min 6 characters)
- Personal information
- Document details
- Phone and salary

### 3. Dashboard

**For Affiliates:**
View your credit applications:
- Total applications
- Pending applications
- Approved applications
- Application history

**For Analysts:**
Evaluate pending applications:
- View all pending applications from all affiliates
- See detailed application information
- Evaluate applications (calls risk-central-service)
- View evaluation results (score, risk level, decision)
- Applications automatically update status

### 4. Analyst Workflow

1. Login as analyst (`analyst` / `analyst123`)
2. View all pending applications
3. Click "Evaluar" on any application
4. System calls risk-central-service
5. View evaluation result with:
   - Credit score (300-950)
   - Risk level (LOW/MEDIUM/HIGH)
   - Decision (APPROVED/REJECTED/UNDER_REVIEW)
   - Evaluation comments
6. Application status updates automatically

### 4. New Application

Create a credit application:
- Requested amount ($1M - $50M COP)
- Term (6-60 months)
- Interest rate
- Monthly income
- Current debt
- Purpose

The form includes real-time calculation of:
- Monthly payment
- Total payment
- Debt-to-income ratio

## 🐳 Docker Deployment

### Build and Run

```bash
# Build
docker build -t coopcredit-frontend .

# Run
docker run -d \
  -p 3000:80 \
  --name coopcredit-frontend \
  coopcredit-frontend
```

### With Docker Compose

Add to your main `docker-compose.yml`:

```yaml
services:
  frontend:
    build: ./CoopCreditFront
    ports:
      - "3000:80"
    depends_on:
      - credit-application-service
```

## 📊 Resource Usage

- **Image Size**: ~30-40 MB (nginx:alpine)
- **RAM Usage**: ~10-20 MB
- **Build Time**: < 10 seconds

## 🎨 Design Features

- **Glassmorphism**: Modern frosted glass effect
- **Gradient Backgrounds**: Vibrant color schemes
- **Smooth Animations**: Micro-interactions
- **Responsive Layout**: Mobile-first design
- **Dark Mode Ready**: Easy to implement

## 🔒 Security

- JWT token stored in localStorage
- Automatic token refresh on API calls
- Protected routes (redirect to login if not authenticated)
- Input validation on all forms
- XSS protection headers in nginx

## 🌐 Browser Support

- Chrome/Edge (latest)
- Firefox (latest)
- Safari (latest)
- Mobile browsers

## 📱 Responsive Breakpoints

- Mobile: < 768px
- Tablet: 768px - 1024px
- Desktop: > 1024px

## 🐛 Troubleshooting

### Cannot connect to backend

Make sure the backend is running on `http://localhost:8080`:

```bash
# Check if backend is running
curl http://localhost:8080/actuator/health
```

### CORS errors

The backend must allow CORS from the frontend origin. Check Spring Boot CORS configuration.

### LocalStorage not working

Make sure you're not in private/incognito mode, as some browsers restrict localStorage in this mode.

## 📄 License

MIT License - See main project README

## 👥 Authors

CoopCredit Development Team
