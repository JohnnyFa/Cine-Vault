---
paths:
  - "app/src/main/java/com/fagundes/myshowlist/feat/**/vm/*.kt"
  - "app/src/main/java/com/fagundes/myshowlist/feat/**/presentation/**/*ViewModel.kt"
  - "app/src/main/java/com/fagundes/myshowlist/feat/**/presentation/**/*UiState.kt"
---

# ViewModel rules

Reference implementation: `feat/home/presentation/home/HomeViewModel.kt`.

**Every feature uses the same layout**: `presentation/<screen>/` holds the screen, its ViewModel
and its `<Name>UiState.kt` (which also carries the feature's event interface, if it has one).
There is no `ui/` or `vm/` package left anywhere. Follow whichever layout the feature you
are editing already uses; see CLAUDE.md.

## Structure

- Constructor-inject dependencies. In migrated features that means **use cases only** — a ViewModel
  that injects a repository alongside use cases (as `DetailViewModel` and `HomeViewModel` once
  did) reaches past its own layer. Never `get()` from Koin inside the class.
- Never reference Android framework types (`Context`, `Resources`, `Intent`) **or third-party
  SDKs** (`FirebaseAuth`, `GoogleAuthProvider`, Ktor, Room) — they make the ViewModel untestable
  on the JVM. Pass primitives or domain models in. A test that needs `mockkStatic` or captures
  SDK callback listeners is telling you the ViewModel is reaching past its layer.
- Expose state as `StateFlow`, never `MutableStateFlow`:
  ```kotlin
  private val _trendingState = MutableStateFlow<HomeUiState<List<Movie>>>(HomeUiState.Idle)
  val trendingState: StateFlow<HomeUiState<List<Movie>>> = _trendingState.asStateFlow()
  ```
- Launch work with `viewModelScope.launch`. Never `GlobalScope`, never a raw `CoroutineScope`.
- Guard re-entrant refreshes with a `Job?` field (`if (job?.isActive == true) return`) — see `HomeViewModel.refreshHome()`.
- Signal navigation with a `SharedFlow` event the screen collects, never by taking a callback
  parameter — `OptionsViewModel.logout(onComplete)` used to do the latter. See
  `OptionsEvent.LoggedOut` and `LoginUiEvent.NavigateHome`.

## UI state

In migrated features (`presentation/`), the state contract lives in its own `<Name>UiState.kt`
beside the ViewModel — see `feat/catalog/presentation/catalog/CatalogUiState.kt`. In the older
layout it is declared in the same file as the ViewModel, below the class:

```kotlin
sealed interface HomeUiState<out T> {
    object Idle : HomeUiState<Nothing>
    object Loading : HomeUiState<Nothing>
    data class Success<T>(val data: T) : HomeUiState<T>
    data class Error(val message: String) : HomeUiState<Nothing>
}
```

Each feature owns its own `<Feature>UiState`. Don't reuse another feature's.

## Error handling

Repository suspend calls return `Result<T>`; wrap fallible work in `runCatching { }` and map failure to `UiState.Error`. Never let an exception escape a `viewModelScope.launch` — it crashes the app.

## Testing is mandatory

Every ViewModel has a matching `<Name>ViewModelTest.kt` in the test source set at the **same
package as the ViewModel itself** (`feat/<feature>/vm/` in the older layout,
`feat/<feature>/presentation/<screen>/` in migrated ones), registered in `UnitTestSuite.kt`.

When you add a public method, add a test for it. When you change the constructor, update the test's construction. When you delete a method, delete its test. See `.claude/rules/testing.md`.
