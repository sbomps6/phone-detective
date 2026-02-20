# Specification: Basic Settings Screen with Dark Mode Toggle

## Overview
This track involves creating a dedicated Settings screen for the PhoneDetective application. The primary feature is a Dark Mode toggle, along with several other standard app utility items.

## Functional Requirements
- **Access Point:** Add a settings icon (gear icon) to the Top App Bar (ActionBar) of the main activity/screen.
- **Settings Screen Layout:**
    - **Theme Selection:** A list or radio group to choose between "Light", "Dark", and "Follow System".
    - **Notification Settings:** A placeholder item for future notification configurations.
    - **Reset All Settings:** A button that, when clicked, triggers a confirmation dialog ("Are you sure?"). Upon confirmation, it resets all user-configurable settings to defaults and shows a Toast confirmation.
    - **About Section:** A section containing "About PhoneDetective" (placeholder link/text) and the current "App Version".
- **Theme Persistence:** The selected theme preference must be saved (e.g., via SharedPreferences or DataStore) and applied immediately upon change and on subsequent app launches.

## Non-Functional Requirements
- **Consistency:** Use Material Design 3 components and styling as defined in the Tech Stack.
- **Responsiveness:** The layout should adapt correctly to different screen sizes.

## Acceptance Criteria
- [ ] Settings icon is visible and functional in the Top App Bar.
- [ ] Settings screen displays all required items: Theme Selection, Notification placeholder, Reset button, and About section.
- [ ] Changing the theme setting (Light/Dark/System) immediately updates the app's appearance.
- [ ] Theme preference persists after closing and reopening the app.
- [ ] "Reset All Settings" shows a confirmation dialog before resetting.
- [ ] App Version is dynamically or statically displayed in the About section.

## Out of Scope
- Implementation of actual notification logic.
- External links to a live privacy policy website (placeholders are acceptable).
