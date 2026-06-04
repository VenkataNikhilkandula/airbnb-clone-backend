# Airbnb Clone Backend - Spring Boot Assignment

This is a backend system similar to Airbnb that allows property owners (hosts) to list properties and users (guests) to search, book, and review properties.

## Technologies Used
- Spring Boot 3
- Java 17
- Spring Data JPA
- Hibernate
- MySQL
- Validation API
- Lombok
- Springdoc OpenAPI (Swagger)

## Prerequisites
- JDK 17
- Maven
- MySQL
src/main/java/com/example/airbnbclone

├── controller
│   ├── UserController
│   ├── PropertyController
│   ├── BookingController
│   └── ReviewController
│
├── service
│   ├── UserService
│   ├── PropertyService
│   ├── BookingService
│   └── ReviewService
│
├── repository
│   ├── UserRepository
│   ├── PropertyRepository
│   ├── BookingRepository
│   └── ReviewRepository
│
├── entity
│   ├── User
│   ├── Property
│   ├── Booking
│   └── Review
│
├── dto
├── exception
└── config


---

## ⚙️ Database Configuration

Update `application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/airbnb_db?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true

spring.datasource.username=root
spring.datasource.password=root

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
-------------------------------------
## API Documentation
Once the application is running, you can explore the APIs and test them via Swagger UI:
- **Swagger UI**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **API Docs**: [http://localhost:8080/api-docs](http://localhost:8080/api-docs)

## Postman Collection
A Postman collection is included in the project root: `Airbnb-Clone-Backend.postman_collection.json`. You can import this directly into Postman to test the endpoints.

## Core Features Implemented
- **User Management**: Role-based registration (HOST / GUEST).
- **Property Management**: Hosts can list properties and set availability ranges.
- **Booking Management**: Guests can book properties. The system prevents double bookings for overlapping dates.
- **Reviews**: Guests can leave reviews for properties they have successfully booked and completed their stay.
- **Exceptions**: Global exception handling with appropriate HTTP status codes (400, 404, 409).
