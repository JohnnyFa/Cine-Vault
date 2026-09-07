# CLAUDE.md — MyShowList (CINE VAULT)

Android app: Jetpack Compose, Koin, Ktor, Room, Firebase Auth. Package root `com.fagundes.myshowlist`.

## Build & test commands

Every variant carries a flavor (`dev`/`staging`/`prod` on the `environment` dimension). **`assembleDebug` and `testDebugUnitTest` do not exist.**

```bash
./gradlew assembleDevDebug          # compile
./gradlew testDevDebugUnitTest      # unit tests
./gradlew installDevDebug           # install on device/emulator
./gradlew ktlintFormat              # auto-fix formatting
```

Run `/check` before pushing — it mirrors the CI gate, including `-PwarningsAsErrors=true`, which is the usual reason a green local build fails on the PR.

## Architecture

```
Api (Ktor) ──> RemoteDataSource ──┐
                                  ├──> Repository ──> UseCase ──> ViewModel ──> Compose UI
Dao (Room) ──> LocalDataSource ───┘        (data)      (domain)      (presentation)
```

Dependencies point inward: **presentation → domain ← data**. `domain` knows nothing about Ktor,
Room or Compose. A repository *interface* is a domain contract and lives in `domain/repository/`;
only its implementation lives in `data/repository/`.

- **Feature packaging** — `feat/catalog` is the reference for the target layout:

  ```
  feat/<feature>/
  ├── data/{remote,local,repository}/     # DTO mapping, caching, <Name>RepositoryImpl
  ├── domain/{model,repository,usecase}/  # pure Kotlin: models, contracts, use cases
  └── presentation/
      ├── <screen>/                       # <Name>Screen.kt + <Name>ViewModel.kt + <Name>UiState.kt
      └── components/                     # feature-local composables
  ```

  **Migration in progress**: `catalog`, `detail` and `home` use this layout. `login` and `options`
  still use the older `feat/<feature>/{data,domain,ui,vm}/` split. Follow whichever layout the
  feature you are editing already uses; don't half-convert a feature. Shared code in `core/`,
  shared composables in `components/`.
- **No feature imports another feature — this currently holds app-wide, keep it that way.**
  Anything two features need lives in `core/`: `core/domain/ContentItem.kt`,
  `core/domain/repository/{Favorite,Recent}Repository.kt` (consumed by home, options and detail),
  their impls in `core/data/repository/`, and the datasources in `core/data/local/datasource/`.
  A use case belongs to the feature that *calls* it, not the one that owns the data — which is why
  `SaveRecentMovieUseCase` lives in `feat/detail` while `ObserveRecentsUseCase` lives in `feat/home`.
  `core` never imports `feat/` — except `core/di/AppModule.kt` and `core/navigation/AppNavGraph.kt`,
  which are composition roots and must see everything.
- **MVVM**: ViewModels expose `StateFlow` of a per-feature `sealed interface <Name>UiState` (`Idle`/`Loading`/`Success`/`Error`). Reference: `feat/home/presentation/home/HomeViewModel.kt`.
- **Return types**: suspend one-shots return `Result<T>`; observation functions return `Flow<T>` unwrapped. Repositories return `core/domain` models — never DTOs or Room entities.
- **DI**: Koin, single `appModule` in `core/di/AppModule.kt`. A ViewModel that isn't registered there crashes at navigation time, not at build time.
- **Navigation**: string routes in `core/navigation/AppRoutes.kt` + `AppNavGraph.kt`. Not type-safe routes. `koinViewModel()` is called only inside `composable {}` blocks.
- **Room**: `AppDatabase` at version 5 with hand-written migrations. `fallbackToDestructiveMigration(false)` — a schema change without a migration crashes at startup.

## Non-negotiable: every ViewModel has a test

Add or change a ViewModel → create/update its test under `app/src/test/java/` **in the same package as the ViewModel** (`feat/<feature>/presentation/<screen>/` in migrated features, `feat/<feature>/vm/` in the rest), using MockK + `StandardTestDispatcher`, **and** register it in `UnitTestSuite.kt`. Delete a method → delete its test.

Use cases that carry real logic (a branch, a guard, a mapping) get a test too, under `feat/<feature>/domain/usecase/`; thin one-line forwards to a repository do not.

## Secrets

`local.properties` (`sdk.dir`, `TMDB_API_KEY`), `app/google-services.json`, and any keystore are off-limits: never read, print, or edit them. Build files reach secrets through `getLocalOrEnv(key)` only.

## Detailed conventions load automatically

`.claude/rules/` holds path-scoped rules that enter context when you touch matching files — no need to read them up front:

| Rule | Applies to |
|---|---|
| `viewmodel.md` | `feat/**/vm/*.kt`, `feat/**/presentation/**/*ViewModel.kt`, `**/*UiState.kt` |
| `compose-ui.md` | `feat/**/ui/**`, `feat/**/presentation/**`, `components/**` |
| `data-layer.md` | `**/data/**`, `core/db/**`, `core/network/**` |
| `testing.md` | `app/src/test/**`, `app/src/androidTest/**` |
| `gradle-build.md` | `*.gradle.kts`, `libs.versions.toml` |
| `di-and-navigation.md` | `core/di/*`, `core/navigation/*` |

Skills load on demand too — `add-remote-endpoint` and `room-migration` are project-specific; `edge-to-edge`, `navigation-3`, `testing-setup`, `styles`, `adaptive`, `r8-analyzer`, `agp-9-upgrade`, and others come from [android/skills](https://github.com/android/skills). Invoke them rather than improvising these migrations.

<!-- .skills/*.md holds an older flat copy of six Android skills, kept for Junie and other agents. Claude uses the installed skills instead. -->
