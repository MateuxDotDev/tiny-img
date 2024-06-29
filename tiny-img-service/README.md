# TinyImg Service

![Quarkus](https://img.shields.io/badge/quarkus-%234794EB.svg?style=for-the-badge&logo=quarkus&logoColor=white)
![Kotlin](https://img.shields.io/badge/kotlin-%237F52FF.svg?style=for-the-badge&logo=kotlin&logoColor=white)
![Postgres](https://img.shields.io/badge/postgres-%23316192.svg?style=for-the-badge&logo=postgresql&logoColor=white)
![Swagger](https://img.shields.io/badge/-Swagger-%23Clojure?style=for-the-badge&logo=swagger&logoColor=white)
![RabbitMQ](https://img.shields.io/badge/Rabbitmq-FF6600?style=for-the-badge&logo=rabbitmq&logoColor=white)

This project is responsible for managing user images, sending them to the FFmpeg service (through message queue), and storing them in the database.

## Architecture

- Microservice

## Design pattern

- Hexagonal Architecture

## ADRs

- [Quarkus as Backend Framework](./adr/ADR1.md)
- [Kotlin as Backend Framework Language](./adr/ADR2.md)
- [RabbitMQ as Message Broker](./adr/ADR3.md)

## How to run

### Requirements

- Docker
- Java 21
- Quarkus CLI (optional)

### Configuration

Then, set up the configuration files in the `.env` file. You can use the `.env.example` file as a template.
You should also copy or generate the `publicKey.pem` and `privateKey.pem` from auth-service and place them in the `src/main/resources` folder.

### Running the application

To run in development mode, you can use the following command:

```shell
./mvnw quarkus:dev
```

### Running using Docker

To run the application using Docker, you can use the following command:

```shell
docker compose up
```

Be aware that are needed dependencies to run the application, such as the database and the message broker and database. You can find info about them in the `docker-compose.yml` file.
