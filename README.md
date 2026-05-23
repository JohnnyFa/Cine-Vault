# Cine Vault

MyShowList (branded as **Cine Vault**) is a modern Android application for discovering and tracking movies and anime. It leverages the TMDB API for movie data and the Jikan API for anime information.

## 🚀 Features

- **Movie Discovery**: Browse trending movies, recommended content, and upcoming releases.
- **Anime Catalog**: Explore anime using the Jikan API.
- **Unified Search**: Search for movies across the catalog.
- **Personalized Lists**: Save your favorite shows to a local database.
- **User Authentication**: Secure login using Firebase Authentication and Google Sign-In.
- **Multi-language Support**: Support for English and Portuguese.
- **Modern UI**: Built entirely with Jetpack Compose following Material 3 guidelines.

## 🛠 Tech Stack

- **Language**: [Kotlin](https://kotlinlang.org/) (2.0.0)
- **UI Framework**: [Jetpack Compose](https://developer.android.com/compose) with Material 3
- **Dependency Injection**: [Koin](https://insert-koin.io/) (4.1.1)
- **Networking**: [Ktor](https://ktor.io/) (3.0.1) with Content Negotiation and Kotlinx Serialization
- **Local Database**: [Room](https://developer.android.com/training/data-storage/room) (2.8.4)
- **Authentication**: [Firebase Auth](https://firebase.google.com/docs/auth) & Google Sign-In
- **Image Loading**: [Coil](https://coil-kt.github.io/coil/)
- **Navigation**: Compose Navigation
- **Architecture**: MVVM with Feature-Based Packaging

## 📁 Project Structure

```text
app/src/main/java/com/fagundes/myshowlist/
├── components/          # Shared UI components
├── core/                # Core infrastructure (DI, DB, Network, Navigation)
│   ├── data/            # Global data mappers and API interfaces
│   ├── db/              # Room database configuration
│   ├── di/              # Koin modules
│   ├── domain/          # Shared domain models
│   ├── navigation/      # Navigation graph and routes
│   └── network/         # Ktor client configurations
└── feat/                # Feature-based modules
    ├── catalog/         # Search and discovery catalog
    ├── detail/          # Content detail view
    ├── home/            # Home dashboard
    └── login/           # Authentication flow
```

## ⚙️ Requirements

- **Android Studio**: Ladybug or newer.
- **JDK**: 17
- **Android SDK**: Min SDK 28 (Android 9), Target SDK 36 (Android 15 / "V").
- **TMDB API Key**: You need an API key from [The Movie Database](https://www.themoviedb.org/documentation/api).
- **Firebase Configuration**: A `google-services.json` file is required for authentication features.

## 🛠 Setup & Run

1. **Clone the repository**:
   ```bash
   git clone https://github.com/your-username/MyShowList.git
   ```

2. **Add API Keys**:
   Create a `local.properties` file in the root directory (if it doesn't exist) and add your TMDB API key:
   ```properties
   TMDB_API_KEY=your_api_key_here
   ```

3. **Firebase Setup**:
   Place your `google-services.json` file in the `app/` directory.

4. **Build and Run**:
   Open the project in Android Studio and run the `app` module on an emulator or physical device.

### Build Variants
The project uses product flavors for different environments:
- `dev`: Development environment with logging enabled.
- `staging`: Staging environment for pre-release testing.
- `prod`: Production environment.

## 📜 Scripts & Commands

Use the Gradle wrapper to run common tasks:

- **Build the project**: `./gradlew assembleDebug`
- **Run Unit Tests**: `./gradlew test`
- **Run Instrumented Tests**: `./gradlew connectedAndroidTest`
- **Lint check**: `./gradlew lint`

## 🧪 Testing

- **Unit Tests**: Located in `app/src/test/`. Uses [MockK](https://mockk.io/) for mocking.
- **Instrumented Tests**: Located in `app/src/androidTest/`. Uses Compose Test Rule and Espresso.

## 🔑 Environment Variables

The project uses `BuildConfig` and `local.properties` to manage secrets and configurations:
- `TMDB_API_KEY`: Defined in `local.properties`.
- `TMDB_BASE_URL`: Defined in `build.gradle.kts` flavors.
- `JIKAN_BASE_URL`: Defined in `build.gradle.kts` flavors.

## 📄 License

TODO: Add license information.

---
*Developed by [Johnny Fagundes](https://github.com/johnnyfagundes)*

## 🔄 CI/CD (GitHub Actions)

### Workflow Structure

```text
.github/workflows/
├── android-ci.yml                 # PR quality gates + flavor builds + instrumentation tests
└── firebase-distribution.yml      # Staging auto distribution + prod manual distribution
```

### PR CI Pipeline

`android-ci.yml` runs on every pull request and executes:
- `ktlintCheck`
- `detekt` (reporting mode during adoption)
- Android `lint`
- warnings verification (`-PwarningsAsErrors=true`)
- unit tests (`test`)
- instrumentation tests (`connectedCheck` on emulator, independent job to avoid being skipped when lint/static-analysis fails)
- flavor builds:
  - `assembleDevDebug`
  - `assembleStagingDebug`
  - `assembleProdDebug`

Key CI settings:
- Java 17 (Temurin)
- Android SDK setup via `android-actions/setup-android@v3`
- Gradle cache + configuration cache support via `gradle/actions/setup-gradle@v4`
- Headless emulator on API 35 with startup optimizations (`-no-window`, `-no-boot-anim`, animation disabled)

### Firebase App Distribution CD

`firebase-distribution.yml` handles deployments:

1. **Dev/Staging distribution by tag**
   - Trigger: git tag like `v1.0.0-dev-1`
   - Build: `assembleStagingRelease`
   - Uploads APK to Firebase App Distribution using `FIREBASE_APP_ID_STAGING`

2. **Production distribution by tag**
   - Trigger: git tag like `v1.0.0-1`
   - Build: `assembleProdRelease`
   - Uploads APK to Firebase App Distribution using `FIREBASE_APP_ID_PROD`

Release notes are generated automatically from recent commit messages.

### Required GitHub Secrets

Create the following secrets in `Settings > Secrets and variables > Actions`:

- `TMDB_API_KEY`
- `FIREBASE_TOKEN`
- `FIREBASE_APP_ID_STAGING`
- `FIREBASE_APP_ID_PROD`

Example values:

```text
TMDB_API_KEY=your_tmdb_api_key
FIREBASE_TOKEN=1//0gExampleFirebaseCLIToken
FIREBASE_APP_ID_STAGING=1:1234567890:android:abcdef123456staging
FIREBASE_APP_ID_PROD=1:1234567890:android:abcdef123456prod
```

### How to generate Firebase CLI token

```bash
npm install -g firebase-tools
firebase login:ci
```

Copy the generated token and save it as `FIREBASE_TOKEN`.

### How to retrieve Firebase App IDs

Option A (Firebase Console):
- Open Firebase project → Project settings → Your apps.
- Copy the **App ID** for staging/prod Android apps.

Option B (`google-services.json`):
- Find `mobilesdk_app_id` per flavor's Firebase config file.

### Firebase Testers & Groups

In Firebase Console → App Distribution:
- Create groups such as `internal-testers`, `qa`, `prod-beta`.
- Add tester emails to each group.
- Keep staging and prod groups separate.

### Secrets and Sensitive Configuration Strategy

- Keep local developer values in `local.properties` only for local runs.
- In CI/CD, generate `local.properties` from GitHub secrets at runtime.
- Never hardcode tokens/keys in Gradle files or workflow YAML.

### Production Recommendations

- **Gradle optimization**
  - Keep Gradle, AGP, Kotlin, and dependencies updated.
  - Enable `org.gradle.caching=true` and `org.gradle.configuration-cache=true` (if all plugins are compatible).
  - Use parallel workers based on runner cores.

- **CI speed improvements**
  - Split quality/unit tests and instrumentation tests into separate jobs.
  - Keep emulator job dependent on fast checks to fail early.
  - Consider path-based filters to skip Android jobs for docs-only changes.

- **Test strategy**
  - Run unit/static checks on every PR.
  - Run instrumentation tests on every PR for critical branches, or on labeled PRs if build minutes are constrained.
  - Add smoke instrumentation suite for PRs and full suite nightly.

- **Release minification in CI**
  - Yes, keep release minification enabled in CI for staging/prod to catch R8/proguard regressions early.

- **Branch strategy**
- `main`: stable production-ready branch
- `develop`: integration branch for features
- `staging`: optional pre-release hardening branch
- feature branches via PR into `develop`
- tag-driven distribution:
  - Dev/Staging: `vX.Y.Z-dev-N` (example: `v1.0.0-dev-1`)
  - Production: `vX.Y.Z-N` (example: `v1.0.0-1`)
