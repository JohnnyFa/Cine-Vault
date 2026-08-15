# AI Coding Agent Guide - MyShowList

Guidelines for AI agents to be productive in this Android codebase.

> **Canonical source:** `CLAUDE.md` plus the path-scoped rules in `.claude/rules/`.
> This file is a summary for agents that don't read those; if the two disagree, `.claude/rules/` wins.

### 🏗 Architecture & Patterns
- **Feature-Based Packaging**: Code is organized by feature under `feat/`. Each feature typically contains `data`, `ui`, and `vm` sub-packages.
- **MVVM Pattern**: ViewModels use `StateFlow` to expose UI state.
- **UI State**: Consistent use of `sealed interface` for UI states (e.g., `HomeUiState` with `Idle`, `Loading`, `Success`, `Error`).
- **Dependency Injection**: Powered by **Koin**. Definitions are in `core/di/AppModule.kt`. Use `koinViewModel()` in Compose for injection.
- **Networking**: Uses **Ktor** with `baseHttpClient`. Specific clients (TMDB/Jikan) are configured in `AppModule.kt`.

### 🛠 Tech Stack
- **UI**: Jetpack Compose.
- **Navigation**: Compose Navigation via `AppNavGraph.kt` and `Routes.kt`.
- **Database**: Room (see `core/db/` and `core/data/local/`).
- **Auth**: Firebase Authentication.
- **Serialization**: Kotlinx Serialization.

### 🌟 Android Skills & Best Practices
The `.skills/` directory contains AI-optimized instructions for specific Android features and upgrades. **AI agents MUST read these files** when working on relevant tasks:
- `edge-to-edge.md`: Guidelines for implementing edge-to-edge UI, handling system bars, and using `safeDrawingPadding()`.
- `agp-9-upgrade.md`: Instructions for upgrading the Android Gradle Plugin to version 9.0+.
- `navigation-3.md`: Best practices for Type-safe Compose Navigation.
- `migrate-xml-views-to-jetpack-compose.md`: Patterns for migrating legacy XML views to Compose.
- `r8-analyzer.md`: Using the R8 analyzer for app size and performance optimization.
- `play-billing-library-version-upgrade.md`: Steps for upgrading the Play Billing Library.

### 🔄 Data Flow
1. **Remote**: `MovieApi` (Ktor) -> `HomeRemoteDataSource` -> `HomeRepository`.
2. **Local**: `ContentDao` (Room) -> `HomeLocalDataSource` -> `HomeRepository`.
3. **Repository**: Merges data sources and returns `Result<T>`.
4. **ViewModel**: Calls repository, updates `MutableStateFlow`.
5. **UI**: Observes `StateFlow` and renders based on `UiState`.

### 📝 Common Patterns
- **Mappers**: Convert DTOs to Domain models (e.g., `MovieMapper.kt`).
- **Repository Implementation**: Often split into interface (`HomeRepository`) and implementation (`HomeRepositoryImpl`).
- **Koin ViewModels**: Register new ViewModels in `AppModule.kt` using `viewModelOf(::YourViewModel)`.

### 🚀 Key Files
- `app/src/main/java/com/fagundes/myshowlist/core/di/AppModule.kt`: Central DI registry.
- `app/src/main/java/com/fagundes/myshowlist/core/navigation/AppNavGraph.kt`: Navigation routing logic.
- `app/src/main/java/com/fagundes/myshowlist/core/network/BaseHttpClient.kt`: Ktor configuration.
- `app/src/main/java/com/fagundes/myshowlist/feat/home/vm/HomeViewModel.kt`: Reference for UI state pattern.

### ⚠️ Conventions
- Use `Result<T>` from Kotlin for repository return types.
- Follow the `feat/<feature_name>/...` structure for new features.
- Components shared across features go into `com.fagundes.myshowlist.components`.
