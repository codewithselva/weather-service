# Weather Service

## Introduction
This project is a weather service application built using Spring Boot. It provides weather information for given postal codes and integrates various functionalities such as database operations, validation, retry mechanism, and Swagger/OpenAPI documentation. This will involve making API requests to a weather data provider.

## Prerequisites
- Java 21
- Maven 3.x

## Features
- Real-time weather data retrieval
- Historical weather data storage
- Postal code validation
- Swagger/OpenAPI documentation
  
## Getting Started
### Clone the repository

git clone https://github.com/codewithselva/weather-service.git
cd weather-service

## Build the project
mvn clean install

## Run the application
mvn spring-boot:run

## Configuration
### OpenWeatherMap
- API Key: Replace `YOUR_API_KEY` with your actual OpenWeatherMap API key in src/main/resources/application-test.yml
### Database
The application uses an in-memory H2 database by default. You can change the database configuration in src/main/resources/application-test.yml.

Example configuration:
spring:
  datasource:
    url: jdbc:h2:mem:testdb
    driverClassName: org.h2.Driver
    username: sa
    password: password
  h2:
    console:
      enabled: true
  jpa:
    database-platform: org.hibernate.dialect.H2Dialect

### Swagger/OpenAPI
Swagger UI is available at http://localhost:8080/swagger-ui.html after starting the application.

### API Endpoints
GET /api/v1/weather/history?postalCode={postalCode}&username={username}: Retrieve weather history for the given postal code.

## Testing
To run the tests:
    mvn test

## Building a Jar
To build an executable jar file:
    mvn clean package

The jar file will be created in the target directory.

## Additional Notes
Spring Boot DevTools: DevTools is included as an optional dependency for development purposes but will be excluded from the production build.

## Troubleshooting
If you encounter any issues, make sure that:

    All dependencies are correctly included in pom.xml.
    The database configuration is correct.
    You are using the correct version of Java and Maven.

## Contributing
Feel free to submit pull requests or open issues to improve the project.

## License
This project is licensed under the MIT License.

![Java](https://img.shields.io/badge/java-%3E%3D21-blue)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-green)
![License](https://img.shields.io/badge/license-MIT-blue)
