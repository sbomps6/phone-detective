# Tech Stack - PhoneDetective

## Core Platform
- **Target OS:** Android
- **Min SDK:** 26 (Android 8.0 Oreo)
- **Target SDK:** 34 (Android 14)
- **Programming Language:** Kotlin (v1.9.22)
- **Java Compatibility:** Java 17

## Build & Dependencies
- **Build System:** Gradle (Kotlin DSL preferred for new scripts)
- **Android Gradle Plugin (AGP):** 8.2.0

## Libraries & Frameworks
- **UI & Components:** Material Design 3 (com.google.android.material:material:1.11.0)
- **AndroidX Support:**
  - `androidx.core:core-ktx:1.12.0` (Kotlin extensions for Android core)
  - `androidx.appcompat:appcompat:1.6.1` (Backward compatibility for UI components)
  - `androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0` (ViewModel support)
  - `androidx.lifecycle:lifecycle-livedata-ktx:2.7.0` (LiveData support)
  - `androidx.activity:activity-ktx:1.8.2` (Activity extensions)
  - `androidx.cardview:cardview:1.0.0` (Card-based UI components)

## Data Storage
- **Preferences:** SharedPreferences for simple user settings (e.g., Theme mode).

## Core Utilities
- **Theme Management:** `ThemeHelper` for applying Material 3 theme modes (AppCompatDelegate).

## Testing
- **Unit Testing:** JUnit 4
- **Mocking:** MockK (io.mockk:mockk:1.13.9)
- **Architecture Testing:** androidx.arch.core:core-testing:2.2.0 (for LiveData)

## Development Standards
- **Architecture:** Recommended MVVM (Model-View-ViewModel) for maintainability and testability.
- **Dependency Injection:** TBD (Recommendation: Hilt or Koin for larger features).
