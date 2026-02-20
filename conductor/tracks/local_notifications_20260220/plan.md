# Implementation Plan: Local Notifications and Permission Handling

## Phase 1: Foundation & Permissions [checkpoint: ]
- [ ] Task: Implement `PermissionManager` for `POST_NOTIFICATIONS`
    - [ ] Write instrumented tests to verify permission request logic for API 33+ and API < 33.
    - [ ] Create `PermissionManager` to handle the `POST_NOTIFICATIONS` request flow.
    - [ ] Integrate `PermissionManager` check into `MainActivity` or `Scan` flow.
- [ ] Task: Create `NotificationHelper` for Channel Initialization
    - [ ] Write unit tests to verify Notification Channel creation with high importance and correct ID.
    - [ ] Implement `NotificationHelper.createNotificationChannel()` using Material 3 and Android 13+ standards.
    - [ ] Initialize the channel in the `Application` class or `MainActivity`.
- [ ] Task: Conductor - User Manual Verification 'Foundation & Permissions' (Protocol in workflow.md)

## Phase 2: Notification Dispatch Logic [checkpoint: ]
- [ ] Task: Implement `NotificationService` for Dispatching Alerts
    - [ ] Write unit tests to verify `NotificationService` builds a notification with the correct icon, title, and "High" importance.
    - [ ] Implement `NotificationService` to send a basic scan completion notification.
    - [ ] Ensure the custom shield/magnifying glass icon is used.
- [ ] Task: Implement Navigation Intent
    - [ ] Write tests to verify the `PendingIntent` targets the correct navigation destination (Scan Results).
    - [ ] Add the `PendingIntent` to the notification builder to handle clicks.
- [ ] Task: Conductor - User Manual Verification 'Notification Dispatch Logic' (Protocol in workflow.md)

## Phase 3: Advanced Notification Content [checkpoint: ]
- [ ] Task: Implement Expandable Notification for Flagged Apps
    - [ ] Write unit tests to verify `BigTextStyle` or `InboxStyle` is applied when multiple apps are flagged.
    - [ ] Update `NotificationService` to dynamically change content based on scan results (flagged apps vs. clean scan).
- [ ] Task: Integration with Scan Flow
    - [ ] Write integration tests to verify that finishing a scan triggers the `NotificationService`.
    - [ ] Trigger the notification from the `MainViewModel` or `Scan` repository upon completion.
- [ ] Task: Conductor - User Manual Verification 'Advanced Notification Content' (Protocol in workflow.md)

## Phase 4: Finalization & Polish [checkpoint: ]
- [ ] Task: Graceful Permission Denial Handling
    - [ ] Implement a UI hint or Snackbar if the user denies notifications, explaining why they are useful.
- [ ] Task: Final Verification & TDD Audit
    - [ ] Run the full test suite (Unit and Instrumented).
    - [ ] Verify notification appearance and behavior on an Android 13+ emulator/device.
- [ ] Task: Conductor - User Manual Verification 'Finalization & Polish' (Protocol in workflow.md)
