---
description: Scaffold a new feature package with ViewModel, DI registration, route, and unit test
argument-hint: "<feature-name>"
allowed-tools: Read, Write, Edit, Grep, Glob, Bash(./gradlew *)
disable-model-invocation: true
---

Scaffold a new feature named `$ARGUMENTS` following this project's structure. If no name was given, ask for one before doing anything.

Use lowercase for the package (`watchlist`), PascalCase for classes (`WatchlistViewModel`).

## What to create

Under `app/src/main/java/com/fagundes/myshowlist/feat/<name>/`:

- `data/repository/<Name>Repository.kt` — interface. Suspend one-shots return `Result<T>`; observers return `Flow<T>`.
- `data/repository/<Name>RepositoryImpl.kt` — implementation. Map DTOs/entities to `core/domain` models; never leak either outward.
- `domain/usecase/` — only if there is real logic beyond a pass-through. Don't create empty use cases.
- `vm/<Name>ViewModel.kt` — constructor-injected deps, `StateFlow` state, and a `<Name>UiState` sealed interface in the same file (`Idle`/`Loading`/`Success`/`Error`).
- `ui/<Name>Screen.kt` — the `Screen` + `ScreenContent` split, `collectAsStateWithLifecycle()`, a `@Preview` for the content composable.

Read `feat/home/` first and match its idioms rather than inventing new ones.

## Wiring — none of this is optional

1. **`core/di/AppModule.kt`** — add the repository as `single<Repo> { RepoImpl(get()) }` in the Repository section, use cases as `factory { }`, and the ViewModel as `viewModelOf(::<Name>ViewModel)` in the ViewModels section. An unregistered ViewModel crashes at navigation time, not build time.
2. **`core/navigation/AppRoutes.kt`** — add the route constant, plus a builder function if it takes arguments.
3. **`core/navigation/AppNavGraph.kt`** — add a `composable(AppRoutes.<NAME>) { }` block that calls `koinViewModel()` and passes the ViewModel plus navigation lambdas into the screen. Screens never receive the `NavController`.
4. **`app/src/test/java/com/fagundes/myshowlist/feat/<name>/vm/<Name>ViewModelTest.kt`** — MockK + `StandardTestDispatcher`, following `HomeViewModelTest.kt`. Cover initial state, each public method, and an error path.
5. **`app/src/test/java/com/fagundes/myshowlist/UnitTestSuite.kt`** — add the import and the entry in `@Suite.SuiteClasses`.

## Verify

```
./gradlew ktlintFormat
./gradlew assembleDevDebug testDevDebugUnitTest
```

Report what you created and confirm all five wiring points are done.
