# Custos OAuth Server

A Spring Boot-based OAuth 2.0 server implementation with custom password authentication.

## Features

- OAuth 2.0 Authorization Server
- Custom password authentication
- User registration and login
- JWT token support
- Spring Security integration

## Prerequisites

- Java 17 or higher
- Maven 3.8 or higher
- PostgreSQL (for production)

## Building the Project

### Step 1: Clone the Repository
```bash
# Clone the repository
git clone https://github.com/your-org/custos.git

# Navigate to the oauth project directory
cd custos/oauth
```

### Step 2: Build the Project
```bash
# Clean and build the project
mvn clean install

# If you want to skip tests
mvn clean install -DskipTests
```

The build process will:
1. Clean the target directory
2. Compile the source code
3. Run the test suite
4. Package the application into a JAR file
5. Install the artifact in your local Maven repository

## Running the Application

### Development Mode

1. Start the application:
```bash
# Run the application with Maven
mvn spring-boot:run

# Run with specific profile (e.g., dev)
mvn spring-boot:run -Dspring.profiles.active=dev
```

2. Access the application:
- Home page: http://localhost:8080
- Login page: http://localhost:8080/login
- Register page: http://localhost:8080/register

### Production Mode

1. Build the JAR:
```bash
# Create the production JAR
mvn clean package -Pprod
```

2. Run the JAR:
```bash
# Basic run
java -jar target/oauth-3.2.3.jar

# Run with specific profile
java -jar target/oauth-3.2.3.jar --spring.profiles.active=prod

# Run with custom properties
java -jar target/oauth-3.2.3.jar --server.port=9090 --spring.datasource.url=jdbc:postgresql://localhost:5432/oauth
```

## Configuration

### Configuration Files

The application uses several configuration files located in `src/main/resources`:

1. `application.properties` - Main configuration file
2. `application-dev.properties` - Development-specific settings
3. `application-prod.properties` - Production-specific settings

### Common Configuration Properties

#### Server Configuration
```properties
# Server port
server.port=8080

# Server context path
server.servlet.context-path=/oauth

# Server SSL configuration
server.ssl.enabled=false
server.ssl.key-store=classpath:keystore.p12
server.ssl.key-store-password=your-password
server.ssl.key-store-type=PKCS12
```

#### Database Configuration
```properties
# PostgreSQL configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/oauth
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.datasource.driver-class-name=org.postgresql.Driver

# Hibernate/JPA configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

#### JWT Configuration
```properties
# JWT settings
jwt.secret=your-secret-key
jwt.expiration=86400000 # 24 hours in milliseconds
jwt.issuer=custos-oauth
jwt.audience=custos-clients
```

#### Security Configuration
```properties
# Security settings
spring.security.user.name=admin
spring.security.user.password=admin
spring.security.user.roles=ADMIN

# CORS configuration
spring.security.cors.allowed-origins=http://localhost:3000
spring.security.cors.allowed-methods=GET,POST,PUT,DELETE
spring.security.cors.allowed-headers=*
```

### Environment Variables

You can override any property using environment variables:
```bash
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/oauth
export SPRING_DATASOURCE_USERNAME=your_username
export SPRING_DATASOURCE_PASSWORD=your_password
export JWT_SECRET=your-secret-key
```

### Profile-Specific Configuration

1. Development Profile (`application-dev.properties`):
```properties
# Development-specific settings
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true
logging.level.com.custos=DEBUG
```

2. Production Profile (`application-prod.properties`):
```properties
# Production-specific settings
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false
logging.level.com.custos=INFO
```

## Security Features

- Custom password authentication using PBKDF2
- Password hashing and salting
- CSRF protection
- Session management
- Role-based access control

## API Endpoints

- `POST /register` - Register a new user
- `POST /login` - Authenticate a user
- `GET /oauth/authorize` - OAuth authorization endpoint
- `POST /oauth/token` - OAuth token endpoint
- `POST /oauth/token/introspect` - Token introspection
- `POST /oauth/token/revoke` - Token revocation

## Testing

### Running Tests
```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=UserServiceTest

# Run tests with specific profile
mvn test -Ptest
```

### Test Configuration
Test-specific properties can be configured in `src/test/resources/application-test.properties`.

## Contributing

1. Fork the repository
2. Create your feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details. 