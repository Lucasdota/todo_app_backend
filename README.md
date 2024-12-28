# Todo Application with Spring Boot
[![NPM](https://img.shields.io/npm/l/react)](https://github.com/Lucasdota/todo_app_backend/blob/master/LICENSE)

This is a Todo application built with Java Spring Boot. The application allows users to register, log in using their email and password, and manage their todo items. Authentication is handled using JSON Web Tokens (JWT), which are stored in cookies for secure session management.

## Features

- User registration and login
- JWT-based authentication
- Secure cookie storage for JWT
- CRUD operations for todo items
- User-specific todo lists

## Technologies Used

- Java 21
- Spring Boot 2.x
- Spring Security
- JWT (JSON Web Tokens)
- Maven

## Prerequisites

- Java 21 or higher
- Maven
- IDE (e.g., IntelliJ IDEA, Eclipse)

## Getting Started

### Clone the Repository

```bash
git clone https://github.com/Lucasdota/todo_app_backend.git
cd todo_app_backend
```

### Build the Application
Make sure you have Maven installed. You can build the application using the following command:
```bash
mvn clean install
```

### Run the application
You can run the application using the following command:
```bash
mvn spring-boot:run
```
The application will start on http://localhost:8080

## API Endpoints

### User Registration
- #### POST: /auth/register
  - ```javascript
    { "email": "user@example.com", "password": "yourpassword"}
    ```

### User Login
- #### POST: /auth/login
    - ```javascript
      { "email": "user@example.com", "password": "yourpassword"}
      ```

### User Logout
- #### POST: /auth/logout

### Create Todo
  - #### POST: /todo
      - ```javascript
        { "userId": 1, "name": "task 1", "description": "do the dishes"}
        ```
### Toggle Todo Done
  - #### PUT: /todo
      - ```javascript
        { "todoId": 1 }
        ```
### Delete Todo
  - #### DELETE: /todo
      - ```javascript
        { "todoId": 1 }
        ```
### Get User
- #### GET: /user

### Delete User
- #### DELETE: /user

## Authentication 
Upon successful login, a JWT will be issued and stored in a secure cookie. This cookie will be sent with each request to authenticate the user.

## Acknowledgments
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [JWT.io](https://jwt.io/)
- [mySQL](https://www.mysql.com/)
