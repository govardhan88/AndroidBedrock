# AndroidBedrock (or Your Chosen Name)

An enterprise-ready, fully-featured Android starter template engineered to bypass boilerplate setup and kickstart production-grade applications. Built on Clean Architecture and MVVM principles, this template consolidates essential foundation modules including dependency injection, advanced networking, secure token management, and comprehensive testing suites.

## 🛠️ Key Architectural Highlights

*   **Robust Network Layer:** Powered by Ktor/Retrofit, features standard API parsing models and a unified response handling mechanism.
*   **Dual-Execution Flows:** Implements a single-flow API strategy utilizing Kotlin Coroutines & Flow, natively supporting both **Safe** (guaranteed error/exception catching) and **Unsafe** (raw payload/direct exception propagation) execution tracks.
*   **Automated Auth Management:** Built-in interceptors for seamless OAuth2 token management, handling automatic token refresh, rotation, and session expiration gracefully.
*   **Dependency Injection:** Fully modularized dependency injection graph powered by Hilt for clean separation of concerns and effortless testing.
*   **Test-Driven Foundation:** Pre-configured with essential testing frameworks (MockK, JUnit5, Turbine) spanning across Unit, Integration, and Architecture verification tests.
