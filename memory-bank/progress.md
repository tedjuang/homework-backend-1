## Progress

- What works: Basic application setup, MySQL and RocketMQ integration.
- What's left to build:
  - [ ] Create Notification: `POST /notifications`
  - [ ] Get Notification by ID: `GET /notifications/{id}`
  - [ ] Get Recent Notifications: `GET /notifications/recent`
  - [ ] Update Notification: `PUT /notifications/{id}`
  - [ ] Delete Notification: `DELETE /notifications/{id}`
- Current status: Implementing Create Notification endpoint.
- Known issues: Spring application fails to connect to Redis on startup, attempts to connect to localhost:6379. Verify Redis connection after changing the container name.
