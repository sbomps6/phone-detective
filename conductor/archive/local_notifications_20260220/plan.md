# Implementation Plan: Local Notifications and Permission Handling

## Phase 1: Foundation & Permissions [checkpoint: 9dbe86b]
- [x] Task: Implement `PermissionManager` for `POST_NOTIFICATIONS`
    - [x] Write instrumented tests to verify permission request logic for API 33+ and API < 33.
    - [x] Create `PermissionManager` to handle the `POST_NOTIFICATIONS` request flow.
    - [x] Integrate `PermissionManager` check into `MainActivity` or `Scan` flow.
- [x] Task: Create `NotificationHelper` for Channel Initialization
    - [x] Write unit tests to verify Notification Channel creation with high importance and correct ID.
    - [x] Implement `NotificationHelper.createNotificationChannel()` using Material 3 and Android 13+ standards.
    - [x] Initialize the channel in the `Application` class or `MainActivity`.
- [x] Task: Conductor - User Manual Verification 'Foundation & Permissions' (Protocol in workflow.md)

## Phase 2: Notification Dispatch Logic [checkpoint: ffbbd1c]
- [x] Task: Implement `NotificationService` for Dispatching Alerts
    - [x] Write unit tests to verify `NotificationService` builds a notification with the correct icon, title, and "High" importance.
    - [x] Implement `NotificationService` to send a basic scan completion notification.
    - [x] Ensure the custom shield/magnifying glass icon is used.
- [x] Task: Implement Navigation Intent
    - [x] Write tests to verify the `PendingIntent` targets the correct navigation destination (Scan Results).
    - [x] Add the `PendingIntent` to the notification builder to handle clicks.
- [x] Task: Conductor - User Manual Verification 'Notification Dispatch Logic' (Protocol in workflow.md)

## Phase 3: Advanced Notification Content [checkpoint: ca41588]
- [x] Task: Implement Expandable Notification for Flagged Apps
    - [x] Write unit tests to verify `BigTextStyle` or `InboxStyle` is applied when multiple apps are flagged.
    - [x] Update `NotificationService` to dynamically change content based on scan results (flagged apps vs. clean scan).
- [x] Task: Integration with Scan Flow
    - [x] Write integration tests to verify that finishing a scan triggers the `NotificationService`.
    - [x] Trigger the notification from the `MainViewModel` or `Scan` repository upon completion.
- [x] Task: Conductor - User Manual Verification 'Advanced Notification Content' (Protocol in workflow.md)

## Phase 4: Finalization & Polish [checkpoint: c3539b1]
- [x] Task: Graceful Permission Denial Handling
    - [x] Implement a UI hint or Snackbar if the user denies notifications, explaining why they are useful.
- [x] Task: Final Verification & TDD Audit
    - [x] Run the full test suite (Unit and Instrumented).
    - [x] Verify notification appearance and behavior on an Android 13+ emulator/device.
- [x] Task: Conductor - User Manual Verification 'Finalization & Polish' (Protocol in workflow.md)
