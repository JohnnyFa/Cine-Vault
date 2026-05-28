# Cine Vault

MyShowList (branded as **Cine Vault**) is a modern Android application for discovering and tracking movies and anime. It leverages the TMDB API for movie data and the Jikan API for anime information.

## Features

- **Movie Discovery**: Browse trending movies, "Show of the Day", recommended content, and upcoming releases.
- **Anime Catalog**: Explore anime using the Jikan API with genre-based filtering.
- **Unified Search**: Search movies and anime across the catalog with upcoming release highlights.
- **Favorites**: Save content to a persistent local favorites list.
- **Recents**: Automatically tracks recently viewed content.
- **Content Detail**: Full detail view with metadata, poster hero, and favorite toggle.
- **User Profile & Options**: View account info and log out from a dedicated options screen.
- **User Authentication**: Secure login via Firebase Authentication and Google Sign-In.
- **Multi-language Support**: English and Portuguese.
- **Shimmer Loading**: Skeleton loading placeholders across all content sections.
- **Modern UI**: Built entirely with Jetpack Compose following Material 3 guidelines, with edge-to-edge support and a custom floating bottom navigation bar.

## Tech Stack

| Layer | Library | Version |
|---|---|---|
| Language | [Kotlin](https://kotlinlang.org/) | 2.0.0 |
| UI | [Jetpack Compose](https://developer.android.com/compose) + Material 3 | BOM 2024.09 |
| Dependency Injection | [Koin](https://insert-koin.io/) | 4.1.1 |
| Networking | [Ktor](https://ktor.io/) + OkHttp | 3.0.1 |
| Serialization | [Kotlinx Serialization](https://github.com/Kotlin/kotlinx.serialization) | — |
| Local Database | [Room](https://developer.android.com/training/data-storage/room) | 2.8.4 |
| Image Loading | [Coil](https://coil-kt.github.io/coil/) | 2.5.0 |
| Navigation | [Compose Navigation](https://developer.android.com/jetpack/compose/navigation) | 2.9.6 |
| Authentication | [Firebase Auth](https://firebase.google.com/docs/auth) + Google Sign-In | — |
| Architecture | MVVM + Feature-Based Packaging | — |
| Static Analysis | [Detekt](https://detekt.dev/) + [ktlint](https://pinterest.github.io/ktlint/) | — |

## Project Structure

```text
app/src/main/java/com/fagundes/myshowlist/
├── components/              # Shared UI components (shimmer, bottom nav, error/empty states)
├── core/
│   ├── data/
│   │   ├── local/           # Room DAOs, entities, enums, mappers
│   │   ├── mapper/          # DTO → domain model mappers
│   │   └── remote/          # API interfaces, DTOs, response wrappers
│   ├── db/                  # AppDatabase configuration
│   ├── di/                  # Koin module (AppModule.kt)
│   ├── domain/              # Shared domain models (Movie, Anime, Content)
│   ├── navigation/          # AppNavGraph.kt + AppRoutes.kt
│   └── network/             # BaseHttpClient, TMDB/Jikan client configs
└── feat/
    ├── catalog/             # Upcoming releases, genre filtering, search
    ├── detail/              # Content detail view, favorite toggle use cases
    ├── home/                # Dashboard: trending, recommended, recents, favorites
    ├── login/               # Firebase/Google Sign-In flow
    └── options/             # User profile display and logout
```

Each feature follows the `data/ | ui/ | vm/` sub-structure with repositories returning `Result<T>`, ViewModels exposing `StateFlow<UiState>`, and use cases for cross-cutting domain logic.

## Requirements

- **Android Studio**: Ladybug or newer
- **JDK**: 17
- **Android SDK**: Min SDK 28 (Android 9), Target SDK 36
- **TMDB API Key**: Obtain from [The Movie Database](https://www.themoviedb.org/documentation/api)
- **Firebase**: A `google-services.json` file for authentication

## Setup & Run

1. **Clone the repository**:
   ```bash
   git clone https://github.com/JohnnyFa/MyShowList.git
   ```

2. **Add your TMDB API key** to `local.properties` in the root directory:
   ```properties
   TMDB_API_KEY=your_api_key_here
   ```

3. **Firebase Setup**: Place `google-services.json` in the `app/` directory.

4. **Build and Run**: Open the project in Android Studio and run the `app` module on an emulator or device.

### Build Flavors

| Flavor | App name | Logging | App ID suffix |
|---|---|---|---|
| `dev` | CINE VAULT (Dev) | enabled | `.dev` |
| `staging` | CINE VAULT (Staging) | enabled | `.staging` |
| `prod` | CINE VAULT | disabled | — |

## Gradle Commands

```bash
# Build
./gradlew assembleDebug
./gradlew assembleDevDebug

# Tests
./gradlew test                    # Unit tests
./gradlew connectedAndroidTest    # Instrumented tests

# Static analysis
./gradlew ktlintCheck
./gradlew detekt
./gradlew lint
```

## Testing

Unit tests live under `app/src/test/java/com/fagundes/myshowlist/feat/<feature>/vm/`.

- **Mocking**: [MockK](https://mockk.io/) (`mockk`, `coEvery`, `verify`)
- **Coroutines**: `StandardTestDispatcher` + `Dispatchers.setMain` / `resetMain`
- **Coverage**: Every ViewModel must have a test covering initial state, each public action, and error paths
- **Suite**: All `*ViewModelTest` classes are registered in `UnitTestSuite.kt`

Instrumented tests live under `app/src/androidTest/` and use Compose Test Rule and Espresso.

## Environment Variables

| Variable | Source |
|---|---|
| `TMDB_API_KEY` | `local.properties` |
| `TMDB_BASE_URL` | `build.gradle.kts` per flavor |
| `JIKAN_BASE_URL` | `build.gradle.kts` per flavor |
| `LOGGING_ENABLED` | `build.gradle.kts` per flavor |

## CI/CD (GitHub Actions)

### Workflow Structure

```text
.github/workflows/
├── android-ci.yml              # PR quality gates + flavor builds
└── firebase-distribution.yml   # Staging and production distribution
```

### PR CI Pipeline (`android-ci.yml`)

Runs on every pull request:
- `ktlintCheck`
- `detekt` (reporting mode)
- Android `lint` with `-PwarningsAsErrors=true`
- Unit tests (`test`)
- Instrumentation tests (`connectedCheck` on a headless API 35 emulator — independent job so lint failures don't skip it)
- Flavor builds: `assembleDevDebug`, `assembleStagingDebug`, `assembleProdDebug`

Key settings: Java 17 (Temurin), `android-actions/setup-android@v3`, `gradle/actions/setup-gradle@v4` with cache.

### Firebase App Distribution (`firebase-distribution.yml`)

| Trigger tag | Build | Destination |
|---|---|---|
| `v1.0.0-dev-1` | `assembleStagingRelease` | Firebase App Distribution (staging) |
| `v1.0.0-1` | `assembleProdRelease` | Firebase App Distribution (prod) |

Release notes are generated automatically from recent commit messages.

### Required GitHub Secrets

```
TMDB_API_KEY
FIREBASE_TOKEN
FIREBASE_APP_ID_STAGING
FIREBASE_APP_ID_PROD
```

Generate `FIREBASE_TOKEN`:
```bash
npm install -g firebase-tools
firebase login:ci
```

Retrieve `FIREBASE_APP_ID_*` from Firebase Console → Project settings → Your apps, or from `mobilesdk_app_id` in `google-services.json`.

### Branch Strategy

- `main` — stable, production-ready
- `develop` — integration branch for features
- `feat/*` — feature branches, PR into `develop`
- Tags drive distribution: `vX.Y.Z-dev-N` (staging), `vX.Y.Z-N` (prod)

## License

TODO: Add license information.

---
*Developed by [Johnny Fagundes](https://github.com/johnnyfagundes)*
