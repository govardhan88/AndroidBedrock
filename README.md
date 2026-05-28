# AndroidBedrock

An enterprise-ready, fully-featured Android starter template engineered to bypass boilerplate setup and kickstart production-grade applications. Built on Clean Architecture and MVVM principles, this template consolidates essential foundation modules including dependency injection, advanced networking, secure token management, and comprehensive testing suites.

---

## 🛠️ Key Architectural Highlights

*   **Modular Clean Architecture:** Strict separation of concerns across Data, Domain, and UI layers, utilizing Gradle modules to enforce boundaries.
*   **Robust Network Layer:** Powered by Retrofit/OkHttp, featuring standard API parsing models and a unified response handling mechanism.
*   **Safe API Execution:** Implements a single-flow API strategy utilizing Kotlin Coroutines & Flow, natively supporting both **Safe** (guaranteed error/exception catching) and **Unsafe** execution tracks.
*   **Secure Auth Management:** A dedicated `:core:auth` module handling OAuth2 token lifecycle, rotation, and session expiration using **EncryptedSharedPreferences** for hardware-backed security.
*   **Dependency Injection:** Fully modularized DI graph powered by **Hilt** with custom qualifiers for different networking and storage requirements.
*   **Centralized Configuration:** Zero hardcoded values policy; all timeouts, keys, and paths are managed in a central `Constants.kt`.
*   **Resource Providing:** Advanced `ResourceProvider` and `DeferredResource` system allowing ViewModels to return UI resources (Strings, Colors, Drawables) without context dependencies.

---

## 📦 Project Structure

| Module | Purpose |
| :--- | :--- |
| **`:app`** | The main Android application module containing UI, Navigation, and Feature logic. |
| **`:core`** | Shared foundational logic: Networking, Base Classes, Dispatchers, and common Utilities. |
| **`:core:auth`** | Specialized module for Authentication: Token management, Auth Interceptors, and Identity APIs. |

---

## 🚀 Tech Stack & Versions

- **Kotlin:** `1.9.23`
- **Jetpack Compose:** `BOM 2024.02.01`
- **Dependency Injection:** `Hilt 2.51.1` with `KSP`
- **Networking:** `Retrofit 2.11.0` & `OkHttp 4.12.0`
- **Serialization:** `Kotlinx Serialization 1.6.3`
- **Security:** `Android Security Crypto 1.1.0-alpha06` (EncryptedSharedPreferences)
- **Database:** `Room 2.6.1`
- **Testing:** `MockK`, `JUnit5`, `Turbine`, `Mockito-Kotlin`

---

## 🔧 Getting Started

### Prerequisites
- Android Studio Iguana (2023.2.1) or newer.
- Android SDK 34 (Compile/Target).
- Java 17.

### Build & Run
1. Clone the repository.
2. Sync Project with Gradle Files.
3. Build the `:app` module.
4. Run on an emulator or physical device (Min SDK 24).

### Running Tests
To run all unit tests across modules:
```bash
./gradlew test
```

To run tests for the auth module specifically:
```bash
./gradlew :core:auth:testDebugUnitTest
```

---

## 🏗️ Core Patterns

### Safe API Calls
Use `launchSafeApi` in your ViewModels to automatically handle loading states and catch network/http exceptions:
```kotlin
fun fetchData() {
    launchSafeApi(
        onSuccess = { data -> _uiState.value = UiState.Success(data) },
        onError = { error -> _uiState.value = UiState.Error(error) },
        block = { repository.getData() }
    )
}
```

### Secure Storage
Inject the `@SecureStorage SharedPreferences` for sensitive data:
```kotlin
class TokenManager @Inject constructor(
    @SecureStorage private val prefs: SharedPreferences
) { ... }
```
