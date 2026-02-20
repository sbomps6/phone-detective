# Plan: Basic Settings Screen with Dark Mode Toggle

## Phase 1: Infrastructure & Navigation [checkpoint: a2724c9]
- [x] Task: Create `SettingsActivity` and register it in `AndroidManifest.xml`
- [x] Task: Add settings icon (gear icon) to `MainActivity` Top App Bar and implement navigation to `SettingsActivity`
- [x] Task: Conductor - User Manual Verification 'Infrastructure & Navigation' (Protocol in workflow.md)

## Phase 2: Theme Management Logic [checkpoint: 9049423]
- [x] Task: Implement `PreferenceManager` to handle saving and retrieving theme preferences (Light, Dark, System) using SharedPreferences
- [x] Task: Implement `ThemeHelper` to apply the selected theme globally using `AppCompatDelegate`
- [x] Task: Update `MainActivity` and `SettingsActivity` to observe and apply theme changes immediately
- [x] Task: Conductor - User Manual Verification 'Theme Management Logic' (Protocol in workflow.md)

## Phase 3: Settings UI Implementation [checkpoint: 07095e0]
- [x] Task: Design and implement the layout for `SettingsActivity` using Material 3 components
    - [x] Add RadioGroup for Theme Selection (Light, Dark, System)
    - [x] Add placeholder for Notification Settings
    - [x] Add "Reset All Settings" button
    - [x] Add "About" section with App Version and placeholder link
- [x] Task: Implement "Reset All Settings" functionality with a confirmation dialog
- [x] Task: Bind `SettingsActivity` UI components to `PreferenceManager` and `ThemeHelper`
- [x] Task: Conductor - User Manual Verification 'Settings UI Implementation' (Protocol in workflow.md)

## Phase 4: Finalization & Polish [checkpoint: e8bff43]
- [x] Task: Ensure the App Version is dynamically retrieved and displayed
- [x] Task: Verify theme persistence across app restarts
- [x] Task: Final UI polish and accessibility check (touch targets, contrast)
- [x] Task: Conductor - User Manual Verification 'Finalization & Polish' (Protocol in workflow.md)
