# Auth Service

![Quarkus](https://img.shields.io/badge/quarkus-%234794EB.svg?style=for-the-badge&logo=quarkus&logoColor=white)
![Kotlin](https://img.shields.io/badge/kotlin-%237F52FF.svg?style=for-the-badge&logo=kotlin&logoColor=white)
![Postgres](https://img.shields.io/badge/postgres-%23316192.svg?style=for-the-badge&logo=postgresql&logoColor=white)
![Swagger](https://img.shields.io/badge/-Swagger-%23Clojure?style=for-the-badge&logo=swagger&logoColor=white)

This project is responsible for managing user authentication and authorization.

## Architecture

- Microservice

## Design pattern

- Hexagonal Architecture

## ADRs

- [Quarkus as Backend Framework](./adr/ADR1.md)
- [Kotlin as Backend Framework Language](./adr/ADR2.md)
- [Swagger as API documentation tool](./adr/ADR3.md)

## Routes

### OpenAPI

You can either use the Swagger UI or the following routes to interact with the service:

```http
GET /auth
```

### Endpoints
- `POST /auth/register`
  - Request body:
    ```json
    {
      "username": "string",
      "email": "string",
      "password": "string"
    }
    ```
- `POST /auth/login`
  - Request body:
    ```json
    {
      "email": "string",
      "password": "string"
    }
    ```

## Generate JWT keys

To generate the keys, go to the resources folder by running the following command:

```shell
cd src/main/resources
```

Then, run the following commands:

```shell
openssl genrsa -out rsaPrivateKey.pem 2048

openssl rsa -pubout -in rsaPrivateKey.pem -out publicKey.pem
```

## SOLID

### Single Responsibility Principle

Due to Hexagonal Architecture, the code is divided into layers, where each layer has a single responsibility.
For example, the `UserRepositoryImpl.findByUsername` only queries the database to find a user by username.

### Open/Closed Principle

The code is open for extension and closed for modification. For example, the `UserRepository` interface can be extended to add new methods without changing the existing code.

### Liskov Substitution Principle

The code is designed to allow the use of derived classes without changing the behavior of the base class. For example, the `UserRepositoryImpl` class can be used in place of the `UserRepository` interface.

### Interface Segregation Principle

The code is divided into interfaces that are specific to the needs of the client. For example, the `UserRepository` interface only contains methods that are needed by the client.

### Dependency Inversion Principle

The code is designed to depend on abstractions rather than concrete implementations. For example, the `UserRepositoryImpl` class depends on the `UserRepository` interface.