# Expense Tracker Backend

A RESTful Expense Tracker API built using **Java, Spring Boot, Spring Security, JWT, and PostgreSQL**. The application enables users to securely manage their expenses, filter and paginate records, and analyze their spending through summary and category-wise analytics.

## Features

- **User Authentication:** User registration and login using JWT-based authentication.
- **Password Security:** Secure password hashing using Spring Security's password encoder.
- **Expense Management:** Create, retrieve, update, and delete expenses.
- **User-Level Authorization:** Users can access and manage only their own expenses.
- **Pagination and Sorting:** Retrieve expense records in manageable pages with sorting support.
- **Expense Filtering:** Filter expenses by category and date.
- **Spending Summary:** Calculate total expenses and expense count.
- **Category-Wise Analytics:** Analyze spending across different categories.
- **Monthly Analytics:** View expense summaries grouped by month.
- **Input Validation:** Validate incoming request data.
- **Global Exception Handling:** Handle application errors with appropriate HTTP responses.
- **API Documentation:** Explore and test endpoints using Swagger UI.
- **Automated Testing:** Unit tests using JUnit 5 and Mockito.

## Tech Stack

| Technology | Purpose |
|---|---|
| Java | Programming language |
| Spring Boot | Backend application framework |
| Spring Security | Authentication and authorization |
| JWT | Stateless authentication |
| Spring Data JPA | Database persistence |
| Hibernate | ORM |
| PostgreSQL | Relational database |
| Maven | Dependency management and build |
| JUnit 5 | Automated testing |
| Mockito | Mocking dependencies in tests |
| Swagger / OpenAPI | API documentation |
| Postman | API testing |
| Git and GitHub | Version control |

## Architecture

The application follows a layered architecture:

- **Controller Layer:** Handles HTTP requests and responses.
- **Service Layer:** Contains business logic and ownership checks.
- **Repository Layer:** Communicates with PostgreSQL through Spring Data JPA.
- **Entity Layer:** Defines database entities and relationships.
- **DTO Layer:** Controls request and response data.
- **Security Layer:** Handles JWT authentication and endpoint access.
- **Exception Layer:** Provides consistent error handling.

## Prerequisites

Install the following before running the project:

- Java version compatible with your Spring Boot configuration
- PostgreSQL
- Maven, or use the included Maven Wrapper
- Git
- Postman (optional, for API testing)

## Getting Started

### 1. Clone the repository

```bash
git clone https://github.com/YOUR_USERNAME/expense-tracker.git
cd expense-tracker
```

### 2. Create the PostgreSQL database

Create a database in PostgreSQL:

```sql
CREATE DATABASE expense_tracker;
```

Use the database name and credentials configured for your application.

### 3. Configure environment variables

Configure the following environment variables in your IDE run configuration or operating system:

```text
DB_URL=jdbc:postgresql://localhost:5432/expense_tracker
DB_USERNAME=your_database_username
DB_PASSWORD=your_database_password
JWT_SECRET=your_secure_random_secret
JWT_EXPIRATION=86400000
```

Update your `application.properties` to reference these variables, using the property names expected by your application. For example:

```properties
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}

jwt.secret=${JWT_SECRET}
jwt.expiration=${JWT_EXPIRATION:86400000}
```

Configure any additional properties required by your existing application.

**Security note:** Never commit real database credentials or JWT secrets to GitHub. Use environment variables for sensitive configuration.

### 4. Run the application

On Windows, use the Maven Wrapper if it is included in the repository:

```bash
mvnw.cmd spring-boot:run
```

Alternatively, if Maven is installed:

```bash
mvn spring-boot:run
```

The application will start on the configured port. If no custom port is configured, Spring Boot normally uses `8080`.

### 5. Access Swagger UI

Once the application is running, open:

```text
http://localhost:8080/swagger-ui/index.html
```

OpenAPI specification:

```text
http://localhost:8080/v3/api-docs
```

Swagger UI allows you to inspect and test your API endpoints. For protected endpoints, authenticate with a valid JWT using the authorization controls.

## API Overview

The following are the main API areas. Refer to Swagger UI for the exact endpoint paths, HTTP methods, parameters, and request bodies.

| Module | Operations |
|---|---|
| Authentication | Register and log in |
| Expenses | Create, retrieve, update, and delete expenses |
| Expense Queries | Pagination, sorting, and filtering |
| Spending Summary | Total spending and expense count |
| Category Analytics | Spending grouped by category |
| Monthly Analytics | Spending grouped by month |

### Authentication Flow

1. Register a user.
2. Log in with valid credentials.
3. Receive a JWT.
4. Include the token in the `Authorization` header for protected requests:

```text
Authorization: Bearer <YOUR_JWT_TOKEN>
```

5. The application validates the token and enforces authorization rules.

## Running Tests

Run the automated test suite using:

```bash
mvn test
```

The tests use JUnit 5 and Mockito to verify service behavior, including registration, duplicate email handling, expense operations, ownership enforcement, and analytics.

## Error Handling

The application handles common error scenarios, including:

- Invalid request data
- Duplicate email registration
- Missing resources
- Unauthorized requests
- Forbidden access to another user's resources

HTTP status codes depend on the specific endpoint and exception handler.

## Future Improvements

Potential enhancements include:

- React-based frontend
- Deployment of the backend and database
- Production logging and monitoring
- Database schema migrations
- CI/CD automation using GitHub Actions

## Author

**Samatham Harish**

- GitHub: https://github.com/SamathamHarish

## License

Add a license if you intend to distribute or reuse this project under specific terms.
