# Json Token Validator with JWT

A **Spring Boot** application implementing JWT-based authentication with refresh token support for secure API endpoints.

## 📋 Table of Contents
- [Description](#description)
- [Features](#features)
- [Technologies](#technologies-used)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Configuration](#configuration)
- [Usage](#usage)
- [API Endpoints](#api-endpoints)
- [Testing](#testing)
- [Project Structure](#project-structure)
- [Security](#security)
- [Contributing](#contributing)
- [License](#license)

## Description  
This project is a **Java 11** application built with **Spring Boot** and **Maven**, implementing authentication using **JWT (JSON Web Token)** with refresh token functionality.  

The application provides:  
- 🔐 JWT token generation for secure authentication
- ✅ Middleware for JWT validation and refresh
- 🛡️ Protected endpoints requiring valid tokens
- 👥 Role-based access control (RBAC)
- 🔄 Token refresh mechanism for long-lived sessions

## Features
- **JWT Authentication**: Stateless authentication using JSON Web Tokens
- **Refresh Token Support**: Extended session management with refresh tokens
- **Role-Based Access Control**: User roles and permissions
- **Custom Filters**: Authentication and authorization filters
- **Database Integration**: JPA/Hibernate with MySQL
- **Comprehensive Tests**: Unit and integration tests included
- **Spring Security**: Industry-standard security framework

## Technologies Used  
- **Java 11**  
- **Spring Boot 2.5.2**  
- **Spring Security**  
- **Spring Data JPA**  
- **Maven 3+**  
- **JWT (JSON Web Token)** - Auth0 library v3.18.1
- **MySQL** connector
- **Lombok** - Boilerplate reduction
- **Spring Boot DevTools** - Hot reload

## Prerequisites  
To run the application, you need:  
- **JDK 11** or higher
- **Maven 3.6+**  
- **MySQL 5.7+** (or configure another database)
- **Postman**, **curl**, or any HTTP client for testing

## Installation

### 1. Clone the repository
```bash
git clone <repository-url>
cd Project_JsonWebRefreshToken
```

### 2. Build the project
```bash
mvn clean install
```

### 3. Configure the database
Create a MySQL database:
```sql
CREATE DATABASE repliforce_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

## Configuration

### Database Configuration
Edit `src/main/resources/application.properties`:

```properties
# Database
spring.datasource.url=jdbc:mysql://localhost:3306/repliforce_db
spring.datasource.username=root
spring.datasource.password=your_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
spring.jpa.properties.hibernate.format_sql=true

# Server
server.port=8080
server.servlet.context-path=/api

# JWT Configuration
app.jwt.secret=your_secret_key_change_this_in_production
app.jwt.expiration-ms=3600000
app.jwt.refresh-expiration-ms=86400000
```

### Environment Variables (Production)
For production, use environment variables:
```bash
export DB_URL=jdbc:mysql://localhost:3306/repliforce_db
export DB_USERNAME=root
export DB_PASSWORD=secure_password
export JWT_SECRET=your_secure_secret_key
```

## Usage

### Running the Application

#### Option 1: Maven
```bash
mvn spring-boot:run
```

#### Option 2: JAR file
```bash
mvn clean package
java -jar target/RepliforceJsonValidator-0.0.2-SNAPSHOT.jar
```

#### Option 3: Custom port
```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=9090"
```

The application will start at `http://localhost:8080`

## API Endpoints

### Authentication Endpoints

#### 1. User Login
**POST** `/api/auth/login`

Request body:
```json
{
  "username": "john_doe",
  "password": "password123"
}
```

Response (200 OK):
```json
{
  "access_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refresh_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expires_in": 3600
}
```

cURL example:
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"john_doe","password":"password123"}'
```

#### 2. Refresh Token
**POST** `/api/auth/refresh`

Request body:
```json
{
  "refresh_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

Response (200 OK):
```json
{
  "access_token": "new_token_here",
  "expires_in": 3600
}
```

### Protected Endpoints

#### Access Protected Resource
**GET** `/api/users/profile`

Request header:
```bash
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

cURL example:
```bash
curl -H "Authorization: Bearer YOUR_TOKEN" \
  http://localhost:8080/api/users/profile
```

## Testing

### Run all tests
```bash
mvn test
```

### Run specific test class
```bash
mvn test -Dtest=UserServiceImplTest
```

### Run with coverage
```bash
mvn clean test jacoco:report
```

Test classes included:
- `UserResourceTest` - API endpoint tests
- `UserResourceRefreshTokenTest` - Token refresh functionality
- `CustomAuthenticationFilterTest` - Authentication filter validation
- `CustomAuthorizationFilterTest` - Authorization filter validation
- `UserServiceImplTest` - Service layer tests
- `UserRepositoryTest` - Repository/database tests

## Project Structure

```
src/
├── main/
│   ├── java/io/repliforce/RepliforceJsonValidator/
│   │   ├── api/                    # REST controllers
│   │   ├── configs/                # Application configurations
│   │   ├── domain/                 # Entity models (User, Role)
│   │   ├── filter/                 # JWT filters
│   │   ├── repositories/           # Data access layer
│   │   ├── security/               # Security configurations
│   │   ├── service/                # Business logic
│   │   └── RepliforceJsonValidatorApplication.java  # Main app
│   └── resources/
│       └── application.properties   # Configuration file
└── test/
    └── java/io/repliforce/...     # Unit & integration tests
```

## Security

### Best Practices Implemented
- ✅ Stateless JWT authentication
- ✅ Password encryption (BCrypt)
- ✅ Role-based access control
- ✅ CSRF protection
- ✅ SQL injection prevention (JPA)

### Production Checklist
- [ ] Change `app.jwt.secret` to a strong, unique value
- [ ] Use environment variables for sensitive data
- [ ] Enable HTTPS in production
- [ ] Set `spring.jpa.hibernate.ddl-auto=validate` in production
- [ ] Implement rate limiting for authentication endpoints
- [ ] Use secure cookies for token storage (if applicable)
- [ ] Implement token blacklist/revocation for logout
- [ ] Add request logging and monitoring

## Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

**Maintainer**: Repliforce Intelligence Forces  
**Last Updated**: January 2026
