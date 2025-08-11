# Tech Context

## Technologies Used:

- **Java 21+**: Programming language.
- **Spring Boot**: Application framework.
- **MySQL**: Relational database.
- **Redis**: In-memory data store for caching.
- **RocketMQ**: Distributed messaging and streaming platform.

## Development Setup:

- **Docker Compose**: Used to spin up local instances of MySQL, Redis, RocketMQ Name Server, RocketMQ Broker, and RocketMQ Console.
  - MySQL Port: `3306`
  - Redis Port: `6379`
  - RocketMQ Namesrv Port: `9876`
  - RocketMQ Broker Port: `10911`
  - RocketMQ Console Port: `8088`
- **Maven**: Build automation tool.

## Dependencies:

- `Spring Web`
- `Spring Data JPA`
- `Spring Cache`
- `RocketMQ Spring Boot Starter`

## Configuration Notes:

- `application.yaml` needs to be updated with proper connection details for MySQL, Redis, and RocketMQ Name Server.
- `init.sql` can be edited for automatic table creation in MySQL.
