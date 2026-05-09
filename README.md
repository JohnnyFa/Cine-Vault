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
