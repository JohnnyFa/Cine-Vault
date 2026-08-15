# MyShowList - Project Guidelines

This document provides a comprehensive overview of the MyShowList Android project to help AI agents and developers understand the architecture, tech stack, and best practices.

> **Canonical source:** `CLAUDE.md` plus the path-scoped rules in `.claude/rules/`.
> This file is a summary; if the two disagree, `.claude/rules/` wins.

## 🏗 Project Architecture & Patterns
- **Feature-Based Packaging**: Code is organized by feature under `app/src/main/java/com/fagundes/myshowlist/feat/`. Each feature typically contains:
    - `data/`: Repositories and DataSources.
    - `ui/`: Compose screens and components.
    - `vm/`: ViewModels and UI state definitions.
- **MVVM Pattern**: ViewModels use `StateFlow` to expose UI state to the UI layer.
- **UI State**: Consistent use of `sealed interface` for UI states (e.g., `HomeUiState` with `Idle`, `Loading`, `Success`, `Error`).
- **Dependency Injection**: Powered by **Koin**. Definitions are in `core/di/AppModule.kt`. Use `koinViewModel()` in Compose for injection.
- **Networking**: Uses **Ktor** with `baseHttpClient`. Specific clients (TMDB/Jikan) are configured in `AppModule.kt`.
- **Database**: Room for local persistence.

## 🛠 Tech Stack
- **UI**: Jetpack Compose.
- **Navigation**: Type-safe Compose Navigation via `AppNavGraph.kt` and `Routes.kt`.
- **Serialization**: Kotlinx Serialization.
- **Image Loading**: Coil.
- **Dependency Injection**: Koin.
- **Database**: Room.
- **Networking**: Ktor.
- **Auth**: Firebase Authentication.

## 🌟 Android Skills & Best Practices
The `.skills/` directory contains AI-optimized instructions for specific Android features and upgrades. **AI agents MUST read these files** when working on relevant tasks:
- `edge-to-edge.md`: Guidelines for implementing edge-to-edge UI.
- `agp-9-upgrade.md`: Instructions for upgrading the Android Gradle Plugin.
- `navigation-3.md`: Best practices for Type-safe Compose Navigation.
- `migrate-xml-views-to-jetpack-compose.md`: Patterns for migrating legacy XML views to Compose.
- `r8-analyzer.md`: Using the R8 analyzer for app size and performance optimization.
- `play-billing-library-version-upgrade.md`: Steps for upgrading the Play Billing Library.

## 🔄 Data Flow
1. **Remote**: `MovieApi`/`AnimeApi` (Ktor) -> `RemoteDataSource` -> `Repository`.
2. **Local**: `ContentDao` (Room) -> `LocalDataSource` -> `Repository`.
3. **Repository**: Merges data sources and returns `Result<T>`.
4. **ViewModel**: Calls repository, updates `MutableStateFlow` with `UiState`.
5. **UI**: Observes `StateFlow` and renders based on the current `UiState`.

## ⚠️ Key Conventions
- **Return Types**: Use `Result<T>` from Kotlin for repository return types.
- **Naming**: Follow existing file naming conventions (PascalCase for classes, camelCase for functions/variables).
- **Components**: Shared components go into `com.fagundes.myshowlist.components`.
- **Edge-to-Edge**: Always consider edge-to-edge implementation using `safeDrawingPadding()` or appropriate insets.
