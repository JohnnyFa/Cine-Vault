# CLAUDE.md — MyShowList

Read this file before executing any task in this project.

---

## Architecture & Patterns

- **Feature-Based Packaging**: Features live under `app/src/main/java/com/fagundes/myshowlist/feat/`. Each feature contains:
  - `data/`: Repositories and DataSources.
  - `ui/`: Compose screens and components.
  - `vm/`: ViewModels and UI state definitions.
- **MVVM Pattern**: ViewModels expose UI state via `StateFlow`.
- **UI State**: Use `sealed interface` (e.g., `Idle`, `Loading`, `Success`, `Error`). See `HomeViewModel.kt` as reference.
- **Dependency Injection**: Koin. All definitions in `core/di/AppModule.kt`. Use `koinViewModel()` in Compose. Register new ViewModels with `viewModelOf(::YourViewModel)`.
- **Networking**: Ktor with `baseHttpClient`. Client configs (TMDB/Jikan) live in `AppModule.kt`.
- **Database**: Room — see `core/db/` and `core/data/local/`.
- **Auth**: Firebase Authentication.
- **Serialization**: Kotlinx Serialization.
- **Image Loading**: Coil.

---

## Data Flow

1. `MovieApi`/`AnimeApi` (Ktor) → `RemoteDataSource` → `Repository`
2. `ContentDao` (Room) → `LocalDataSource` → `Repository`
3. Repository merges sources, returns `Result<T>`
4. ViewModel calls repository, updates `MutableStateFlow`
5. UI observes `StateFlow`, renders from `UiState`

---

## Key Conventions

- **Return types**: Always use `Result<T>` for repository return types.
- **Naming**: PascalCase for classes, camelCase for functions/variables.
- **New features**: Follow `feat/<feature_name>/data|ui|vm` structure.
- **Shared components**: Go into `com.fagundes.myshowlist.components`.
- **Mappers**: Convert DTOs to domain models (e.g., `MovieMapper.kt`).
- **Navigation**: Type-safe Compose Navigation via `AppNavGraph.kt` and `AppRoutes.kt`.
- **Edge-to-edge**: Always apply `safeDrawingPadding()` or appropriate insets. Never ignore system bar overlaps.

---

## Key Files

| File | Purpose |
|------|---------|
| `core/di/AppModule.kt` | Central DI registry |
| `core/navigation/AppNavGraph.kt` | Navigation routing |
| `core/network/BaseHttpClient.kt` | Ktor configuration |
| `feat/home/vm/HomeViewModel.kt` | Reference for UI state pattern |

---

## Android Skills — When to Read `.skills/` Files

Read the relevant skill file **before** working on these tasks:

| Task | Skill File |
|------|-----------|
| Edge-to-edge UI, system bars, IME, `safeDrawingPadding()` | `.skills/edge-to-edge.md` |
| Upgrading Android Gradle Plugin to v9+ | `.skills/agp-9-upgrade.md` |
| Type-safe Compose Navigation or Navigation 3 migration | `.skills/navigation-3.md` |
| Migrating XML layouts to Jetpack Compose | `.skills/migrate-xml-views-to-jetpack-compose.md` |
| R8/ProGuard keep rules and app size optimization | `.skills/r8-analyzer.md` |
| Upgrading Google Play Billing Library | `.skills/play-billing-library-version-upgrade.md` |

### Quick Reference — Skill Triggers

**edge-to-edge**: Requires SDK 35+. Call `enableEdgeToEdge()` before `setContent`. Use `Scaffold` with `innerPadding`; apply `contentPadding` to lazy lists. Use `Modifier.safeDrawingPadding()` outside Scaffolds. For IME: prefer `Modifier.fitInside(WindowInsetsRulers.Ime.current)`. Never double-apply insets.

**agp-9-upgrade**: Run AGP Upgrade Assistant first. Then update KSP ≥ 2.3.6, migrate to built-in Kotlin, migrate to new DSL, handle kapt → KSP, update BuildConfig. Verify with `./gradlew help` and `./gradlew build --dry-run`.

**navigation-3**: Covers type-safe nav keys, `NavDisplay`, multiple back stacks, conditional navigation, Koin integration, dialogs, bottom sheets, and list-detail layouts. Full recipes in `.skills/navigation-3.md`.

**migrate-xml-views-to-jetpack-compose**: 10-step process: identify candidate → analyze → plan → capture UI → set up Compose deps → theming → migrate layout → replace usages → validate → remove XML. Do not migrate entire theme, only the minimum required.

**r8-analyzer**: Creates `R8_Configuration_Analysis.md`. Checks R8 config, evaluates keep rules, identifies redundant/broad rules, suggests narrower alternatives. Does not modify keep rule files directly.

**play-billing-library-version-upgrade**: Detects effective PBL version from code, plans direct or stepped migration path, applies all deprecation/breaking changes, runs `./gradlew assembleDebug` for verification.
