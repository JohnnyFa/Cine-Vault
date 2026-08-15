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
                                  ├──> Repository ──> ViewModel ──> Compose UI
Dao (Room) ──> LocalDataSource ───┘
```

- **Feature packaging**: `feat/<feature>/{data,domain,ui,vm}/`. Shared code in `core/`, shared composables in `components/`.
- **MVVM**: ViewModels expose `StateFlow` of a per-feature `sealed interface <Name>UiState` (`Idle`/`Loading`/`Success`/`Error`). Reference: `feat/home/vm/HomeViewModel.kt`.
- **Return types**: suspend one-shots return `Result<T>`; observation functions return `Flow<T>` unwrapped. Repositories return `core/domain` models — never DTOs or Room entities.
- **DI**: Koin, single `appModule` in `core/di/AppModule.kt`. A ViewModel that isn't registered there crashes at navigation time, not at build time.
- **Navigation**: string routes in `core/navigation/AppRoutes.kt` + `AppNavGraph.kt`. Not type-safe routes. `koinViewModel()` is called only inside `composable {}` blocks.
- **Room**: `AppDatabase` at version 5 with hand-written migrations. `fallbackToDestructiveMigration(false)` — a schema change without a migration crashes at startup.

## Non-negotiable: every ViewModel has a test

Add or change a ViewModel → create/update `app/src/test/java/com/fagundes/myshowlist/feat/<feature>/vm/<Name>ViewModelTest.kt` (MockK + `StandardTestDispatcher`) **and** register it in `UnitTestSuite.kt`. Delete a method → delete its test.

## Secrets

`local.properties` (`sdk.dir`, `TMDB_API_KEY`), `app/google-services.json`, and any keystore are off-limits: never read, print, or edit them. Build files reach secrets through `getLocalOrEnv(key)` only.

## Detailed conventions load automatically

`.claude/rules/` holds path-scoped rules that enter context when you touch matching files — no need to read them up front:

| Rule | Applies to |
|---|---|
| `viewmodel.md` | `feat/**/vm/*.kt` |
| `compose-ui.md` | `feat/**/ui/**`, `components/**` |
| `data-layer.md` | `**/data/**`, `core/db/**`, `core/network/**` |
| `testing.md` | `app/src/test/**`, `app/src/androidTest/**` |
| `gradle-build.md` | `*.gradle.kts`, `libs.versions.toml` |
| `di-and-navigation.md` | `core/di/*`, `core/navigation/*` |

Skills load on demand too — `add-remote-endpoint` and `room-migration` are project-specific; `edge-to-edge`, `navigation-3`, `testing-setup`, `styles`, `adaptive`, `r8-analyzer`, `agp-9-upgrade`, and others come from [android/skills](https://github.com/android/skills). Invoke them rather than improvising these migrations.

<!-- .skills/*.md holds an older flat copy of six Android skills, kept for Junie and other agents. Claude uses the installed skills instead. -->
