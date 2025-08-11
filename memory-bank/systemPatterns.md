# System Patterns

## Architecture:

The system follows a layered architecture, typical for Spring Boot applications, consisting of:

- **Controller Layer**: Handles incoming HTTP requests and responses.
- **Service Layer**: Contains business logic and orchestrates operations.
- **Repository Layer**: Interacts with the database for data persistence.

## Key Technologies and Their Roles:

- **Spring Boot**: Framework for building the RESTful API.
- **MySQL**: Primary database for persisting all notification records.
- **Redis**: Used for caching recent notifications and potentially for managing distributed locks or rate limiting (though not explicitly required for this homework).
- **RocketMQ**: Message broker for asynchronous processing of notifications. Notifications are pushed to a topic (`notification-topic`) for further processing (e.g., actual sending).

## Data Flow (Create Notification):

1.  `POST /notifications` request received by the Controller.
2.  Controller passes the request to the Service layer.
3.  Service saves the notification to MySQL via the Repository.
4.  Service pushes the notification message to RocketMQ.
5.  Service adds/updates the notification in Redis cache.
