# Water Cooler Network

A digital platform that recreates informal professional networking for remote and hybrid workers through instant "Coffee Chat" matches, topic lounges, gamification, and private corporate spaces.

## 🚀 Current Status: Component 3 Complete!

**Component 1: Core Backend** ✅ **COMPLETED**  
**Component 2: Coffee Chat Matching + Video Chat** ✅ **COMPLETED**  
**Component 3: Topic Lounges** ✅ **COMPLETED**

### What's Built

**Component 1: Core Backend**

-   **User Authentication & Authorization**: JWT-based security with role-based access control
-   **User Management**: Complete CRUD operations for user profiles
-   **Database Layer**: Entity models, repositories, and data access patterns
-   **Security Framework**: Spring Security with JWT tokens and password encryption
-   **RESTful API**: Authentication and user management endpoints
-   **Frontend Foundation**: React-based UI with authentication flows
-   **Modern UI/UX**: Clean, responsive design with professional styling

**Component 2: Coffee Chat Matching + Video Chat**

-   **Matching System**: Intelligent algorithm based on industry, skills, and interests
-   **Match Management**: Request, accept, reject, and schedule coffee chats
-   **User Preferences**: Customizable matching criteria and availability
-   **Chat History**: Complete conversation logging and management
-   **Frontend Components**: Match discovery, management, and preferences
-   **WebSocket Ready**: Infrastructure for real-time communication

**Component 3: Topic Lounges**

-   **Lounge System**: Topic-based conversation spaces with customizable settings
-   **Real-time Messaging**: Live chat with reply threads and system messages
-   **Participation Management**: Join/leave lounges with role-based access
-   **Search & Discovery**: Find lounges by topic, category, or keywords
-   **Frontend Interface**: Complete lounge discovery, creation, and chat experience
-   **Modern UI/UX**: Beautiful, responsive design with professional styling

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

#### Coffee Chat Matching

-   `POST /api/matches/request` - Create match request
-   `POST /api/matches/{matchId}/respond` - Respond to match request
-   `GET /api/matches/available` - Get available matches
-   `GET /api/matches/my-matches` - Get user's matches
-   `GET /api/matches/my-matches/{status}` - Get matches by status
-   `GET /api/matches/preferences` - Get user preferences
-   `PUT /api/matches/preferences` - Update user preferences

#### Topic Lounges

-   `POST /api/lounges` - Create new lounge
-   `GET /api/lounges` - Get all lounges
-   `GET /api/lounges/{loungeId}` - Get lounge by ID
-   `GET /api/lounges/search?q={query}` - Search lounges
-   `GET /api/lounges/topic/{topic}` - Get lounges by topic
-   `GET /api/lounges/category/{category}` - Get lounges by category
-   `GET /api/lounges/featured` - Get featured lounges
-   `GET /api/lounges/user` - Get user's lounges
-   `POST /api/lounges/{loungeId}/join` - Join lounge
-   `POST /api/lounges/{loungeId}/leave` - Leave lounge
-   `GET /api/lounges/{loungeId}/messages` - Get lounge messages
-   `POST /api/lounges/{loungeId}/messages` - Send message to lounge
-   `DELETE /api/lounges/{loungeId}` - Delete lounge (Creator only)

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

### Matches Table

```sql
CREATE TABLE matches (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user1_id BIGINT NOT NULL,
    user2_id BIGINT NOT NULL,
    match_type VARCHAR(20) NOT NULL DEFAULT 'COFFEE_CHAT',
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    match_time TIMESTAMP,
    scheduled_time TIMESTAMP,
    duration_minutes INTEGER DEFAULT 30,
    compatibility_score DOUBLE,
    match_reason TEXT,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

### User Preferences Table

```sql
CREATE TABLE user_preferences (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    preferred_industries TEXT,
    preferred_roles TEXT,
    preferred_experience_level VARCHAR(20),
    max_match_distance_km INTEGER,
    preferred_chat_duration INTEGER DEFAULT 30,
    availability_start_time VARCHAR(5),
    availability_end_time VARCHAR(5),
    preferred_timezone VARCHAR(50),
    is_available_for_matching BOOLEAN NOT NULL DEFAULT TRUE,
    auto_accept_matches BOOLEAN NOT NULL DEFAULT FALSE,
    notification_preferences TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

### Lounges Table

```sql
CREATE TABLE lounges (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    description TEXT,
    topic VARCHAR(100) NOT NULL,
    category VARCHAR(100),
    tags TEXT,
    created_by BIGINT NOT NULL,
    visibility VARCHAR(20) NOT NULL DEFAULT 'PUBLIC',
    max_participants INTEGER,
    current_participants INTEGER DEFAULT 0,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    is_featured BOOLEAN DEFAULT FALSE,
    last_activity TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

### Lounge Messages Table

```sql
CREATE TABLE lounge_messages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    lounge_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    message_type VARCHAR(20) NOT NULL DEFAULT 'TEXT',
    reply_to_message_id BIGINT,
    is_edited BOOLEAN DEFAULT FALSE,
    edited_at TIMESTAMP,
    is_deleted BOOLEAN DEFAULT FALSE,
    deleted_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

### Lounge Participants Table

```sql
CREATE TABLE lounge_participants (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    lounge_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'MEMBER',
    joined_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_activity TIMESTAMP,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    is_muted BOOLEAN DEFAULT FALSE,
    muted_until TIMESTAMP,
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

Components 1-3 are now complete and ready for the next phase:

**Component 4: Gamification (Streaks, Badges)** (Next in sequence)

-   Achievement system for active participation
-   Streak tracking for consistent engagement
-   Badge rewards for various activities
-   Leaderboards and progress tracking
-   Points system and gamified elements

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

**Status**: ✅ Components 1-3 Complete  
**Next Component**: Gamification (Streaks, Badges)  
**Build Order**: 3 of 10 components completed
