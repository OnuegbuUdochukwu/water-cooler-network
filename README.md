# Water Cooler Network

A digital platform that recreates informal professional networking for remote and hybrid workers through instant "Coffee Chat" matches, topic lounges, gamification, and private corporate spaces.

## 🚀 Current Status: Core Backend Complete

**Component 1: Core Backend** ✅ **COMPLETED**

### What's Built

-   **User Authentication & Authorization**: JWT-based security with role-based access control
-   **User Management**: Complete CRUD operations for user profiles
-   **Database Layer**: Entity models, repositories, and data access patterns
-   **Security Framework**: Spring Security with JWT tokens and password encryption
-   **RESTful API**: Authentication and user management endpoints
-   **Frontend Foundation**: React-based UI with authentication flows
-   **Modern UI/UX**: Clean, responsive design with professional styling

### Backend Features

-   User registration and login with JWT authentication
-   Password encryption using BCrypt
-   Role-based access control (User, Admin, Corporate Admin)
-   Input validation and error handling
-   CORS configuration for frontend communication
-   H2 database for development, PostgreSQL ready for production

### Frontend Features

-   Modern React application with TypeScript
-   Authentication context and protected routes
-   Responsive design with mobile-first approach
-   User registration and login forms
-   Profile management interface
-   Dashboard with feature previews

### API Endpoints

#### Authentication

-   `POST /api/auth/register` - User registration
-   `POST /api/auth/login` - User login
-   `GET /api/auth/me` - Get current user profile

#### User Management

-   `GET /api/users/profile/{userId}` - Get user profile
-   `PUT /api/users/profile/{userId}` - Update user profile
-   `GET /api/users/industry/{industry}` - Get users by industry
-   `GET /api/users/all` - Get all users (Admin only)
-   `DELETE /api/users/{userId}` - Deactivate user (Admin only)

## 🛠️ Technology Stack

### Backend

-   **Framework**: Spring Boot 3.5.4
-   **Language**: Java 24
-   **Security**: Spring Security + JWT
-   **Database**: H2 (dev), PostgreSQL (prod)
-   **Build Tool**: Maven
-   **Validation**: Bean Validation (Hibernate Validator)

### Frontend

-   **Framework**: React 18 with TypeScript
-   **Routing**: React Router v6
-   **Styling**: CSS3 with modern design patterns
-   **HTTP Client**: Axios
-   **Build Tool**: Create React App

## 📋 Prerequisites

-   Java 24 or higher
-   Node.js 16 or higher
-   Maven 3.6 or higher
-   PostgreSQL (for production)

## 🚀 Quick Start

### Backend Setup

1. **Navigate to backend directory**:

    ```bash
    cd water-cooler-network/backend/backend
    ```

2. **Build the project**:

    ```bash
    ./mvnw clean install
    ```

3. **Run the application**:

    ```bash
    ./mvnw spring-boot:run
    ```

4. **Access the application**:
    - Backend API: http://localhost:8080
    - H2 Console: http://localhost:8080/h2-console
    - Database URL: `jdbc:h2:mem:watercooler`
    - Username: `sa`
    - Password: `password`

### Frontend Setup

1. **Navigate to frontend directory**:

    ```bash
    cd water-cooler-network/frontend
    ```

2. **Install dependencies**:

    ```bash
    npm install
    ```

3. **Start the development server**:

    ```bash
    npm start
    ```

4. **Access the frontend**:
    - Frontend: http://localhost:3000

## 🗄️ Database Schema

### Users Table

```sql
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    industry VARCHAR(100),
    skills TEXT,
    interests TEXT,
    company_id BIGINT,
    linkedin_url VARCHAR(500),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

### Companies Table

```sql
CREATE TABLE companies (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) UNIQUE NOT NULL,
    admin_id BIGINT,
    subscription_tier VARCHAR(20) NOT NULL DEFAULT 'FREE',
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

## 🔐 Security Features

-   **JWT Authentication**: Stateless token-based authentication
-   **Password Encryption**: BCrypt hashing with salt
-   **Role-Based Access Control**: User, Admin, and Corporate Admin roles
-   **Input Validation**: Comprehensive validation using Bean Validation
-   **CORS Configuration**: Secure cross-origin resource sharing
-   **Protected Endpoints**: Authentication required for sensitive operations

## 🎨 Frontend Features

-   **Responsive Design**: Mobile-first approach with modern CSS
-   **Authentication Flow**: Complete login/register with protected routes
-   **Profile Management**: Edit and view user profiles
-   **Modern UI Components**: Clean, professional interface
-   **TypeScript**: Type-safe development experience

## 🧪 Testing

### Backend Testing

```bash
cd water-cooler-network/backend/backend
./mvnw test
```

### Frontend Testing

```bash
cd water-cooler-network/frontend
npm test
```

## 📁 Project Structure

```
water-cooler-network/
├── backend/
│   └── backend/
│       ├── src/main/java/com/codewithudo/backend/
│       │   ├── entity/          # Data models
│       │   ├── repository/      # Data access layer
│       │   ├── service/         # Business logic
│       │   ├── controller/      # REST endpoints
│       │   ├── security/        # Authentication & authorization
│       │   └── dto/            # Data transfer objects
│       ├── src/main/resources/  # Configuration files
│       └── pom.xml             # Maven dependencies
├── frontend/
│   ├── src/
│   │   ├── components/         # React components
│   │   ├── contexts/           # React contexts
│   │   └── App.tsx            # Main application
│   └── package.json           # Node dependencies
└── README.md                  # This file
```

## 🔄 Next Steps

The Core Backend is now complete and ready for the next component:

**Component 2: Coffee Chat Matching + Video Chat** (Next in sequence)

-   Real-time matching algorithms
-   WebRTC video chat integration
-   Match preferences and filters
-   Chat history and scheduling

## 🤝 Contributing

1. Follow the established code structure and patterns
2. Ensure all tests pass before submitting
3. Use meaningful commit messages
4. Follow the component build order strictly

## 📞 Support

For questions or issues related to the Core Backend:

-   Check the API documentation at `/api/auth/**` endpoints
-   Review the security configuration in `SecurityConfig.java`
-   Test authentication flows in the frontend

---

**Status**: ✅ Core Backend Complete  
**Next Component**: Coffee Chat Matching + Video Chat  
**Build Order**: 1 of 10 components completed
