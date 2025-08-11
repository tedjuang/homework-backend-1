# Active Context

## Current Work Focus:

- Implementing the "Create Notification" feature, specifically focusing on saving the notification to MySQL and sending the message to RocketMQ.

## Recent Changes:

- Initialized Memory Bank files (`projectbrief.md`, `productContext.md`, `systemPatterns.md`, `techContext.md`, `progress.md`).
- Created `NotificationMessage.java` DTO for RocketMQ messages.
- Modified `NotificationService.java` to send messages to RocketMQ when creating a notification.

## Next Steps:

- Continue with the implementation of the "Create Notification" endpoint (`POST /notifications`).
- Ensure notifications are properly persisted in MySQL.
- Ensure notifications are successfully pushed to the `notification-topic` in RocketMQ.
- Integrate Redis caching for recent notifications as part of the "Create Notification" flow.
- Fix Redis connection issue.

## Active Decisions and Considerations:

- The structure of the message to be sent to RocketMQ needs to be defined (DTO).

* Current focus: Fixing Redis connection issue.
* Recent changes: None.
* Next steps: Change the container name of the redis service to 'my-redis' and update the application.yaml file accordingly.
* Active decisions and considerations: The application attempts to connect to localhost:6379 instead of the configured 'redis:6379'. This might be due to the demo service not being able to resolve the 'redis' hostname.
