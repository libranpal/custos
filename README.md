# Custos OAuth Server

A lightweight OAuth 2.1 authorization server implementation with a demo client application.

## Project Structure

The project is organized as a multi-module Maven project:

- `custos/` - Parent project containing common configuration
  - `oauth/` - OAuth 2.1 authorization server module
  - `demo/` - Demo client application module

## Features

### OAuth Server Module
- OAuth 2.1 compliant authorization server
- JWT-based tokens
- Dynamic client registration
- Multiple grant types support
- User authentication and authorization
- Secure password storage

### Demo Client Module
- Example OAuth client implementation
- Web interface for testing OAuth flows
- Client registration interface
- Authorization flow demonstration

## Prerequisites

- Java 21 or later (required for Spring Boot 3.2+)
- Maven 3.8 or later

## Building

To build the entire project (both modules):

```bash
# From the custos directory
mvn clean install
```

This will build both the OAuth server and demo client modules.

## Running the Applications

### OAuth Server (oauth module)
The OAuth server runs on port 9000 by default.

```bash
# From the custos directory
mvn spring-boot:run -pl oauth
```

### Demo Client (demo module)
The demo client runs on port 8000 by default.

```bash
# From the custos directory
mvn spring-boot:run -pl demo
```

## Development Workflow

To run both applications simultaneously:

1. Open two terminal windows in the `custos` directory
2. In the first terminal, start the OAuth server:
   ```bash
   mvn spring-boot:run -pl oauth
   ```
3. In the second terminal, start the demo client:
   ```bash
   mvn spring-boot:run -pl demo
   ```

## Default Users

The system comes with a default admin user:

- Username: admin
- Password: admin

## OAuth Endpoints

- Authorization: `http://localhost:9000/oauth2/authorize`
- Token: `http://localhost:9000/oauth2/token`
- Client Registration: `http://localhost:9000/oauth2/register`
- Token Introspection: `http://localhost:9000/oauth2/introspect`
- Token Revocation: `http://localhost:9000/oauth2/revoke`

## Demo Application

The demo application provides a simple web interface to:
- Register new OAuth clients
- Authorize OAuth clients
- Test OAuth flows

Access the demo application at: `http://localhost:8000`

## Security Configuration

The OAuth server uses:
- BCrypt for password hashing
- JWT for token generation
- H2 in-memory database for development

## License

This project is licensed under the Apache License 2.0 - see the LICENSE file for details.
