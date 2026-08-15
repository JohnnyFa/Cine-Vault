---
paths:
  - "app/src/main/java/com/fagundes/myshowlist/core/di/*.kt"
  - "app/src/main/java/com/fagundes/myshowlist/core/navigation/*.kt"
---

# Koin DI and navigation rules

## AppModule.kt

Single module (`appModule`), organised by commented sections in this order: Firebase → Auth → HttpClients → Database → APIs → RemoteDataSource → LocalDataSource → Repository → UseCases → ViewModels. Add new definitions to the matching section rather than the bottom of the file.

Scoping conventions actually in use:

| Kind | Definition |
|---|---|
| Repositories, DAOs, APIs, HttpClients, DB | `single<Interface> { Impl(get()) }` |
| Use cases | `factory { SomeUseCase(get()) }` |
| ViewModels | `viewModelOf(::SomeViewModel)` |
| ViewModels with runtime args | `viewModel { (id: Int, type: ContentType) -> DetailViewModel(id, type, get(), …) }` |

The two Ktor clients are disambiguated by qualifier:

```kotlin
val tmdbClient = named("TmdbClient")
val jikanClient = named("JikanClient")
single(tmdbClient) { provideTmdbHttpClient() }
single { MovieApi(get(tmdbClient)) }
```

Any new client must get its own `named(...)` qualifier, or Koin will resolve the wrong one at runtime with no compile error.

**A ViewModel that isn't registered here crashes at navigation time, not at build time.** Registering is part of creating one, not a follow-up.

## Navigation

Routes are **string-based**, not type-safe: `AppRoutes` holds `const val` route strings plus builder functions for parameterised routes.

```kotlin
const val DETAIL = "detail/{id}/{type}"
fun detail(id: Int, type: ContentType) = "detail/$id/${type.name}"
```

Adding a destination means three edits:
1. `AppRoutes.kt` — the route constant (and a builder if it takes arguments).
2. `AppNavGraph.kt` — a `composable(AppRoutes.X) { }` block.
3. `AppModule.kt` — register the screen's ViewModel.

Inside the `composable` block: obtain the ViewModel with `koinViewModel()`, then pass it plus navigation lambdas (`onBack`, `onOpenDetail`) into the screen. Screens never receive the `NavController`.

Argument extraction currently uses `backStackEntry.arguments!!.getString("id")!!` — when adding a destination, prefer `navArgument` declarations with typed defaults over more `!!`.

If migrating to Navigation 3 or type-safe routes, use the `navigation-3` skill first; don't hand-roll the migration.
