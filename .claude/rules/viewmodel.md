---
paths:
  - "app/src/main/java/com/fagundes/myshowlist/feat/**/vm/*.kt"
---

# ViewModel rules

Reference implementation: `feat/home/vm/HomeViewModel.kt`.

## Structure

- Constructor-inject dependencies (repository, use cases). Never `get()` from Koin inside the class.
- Never reference Android framework types (`Context`, `Resources`, `Intent`) — they make the ViewModel untestable on the JVM. Pass primitives or domain models in.
- Expose state as `StateFlow`, never `MutableStateFlow`:
  ```kotlin
  private val _trendingState = MutableStateFlow<HomeUiState<List<Movie>>>(HomeUiState.Idle)
  val trendingState: StateFlow<HomeUiState<List<Movie>>> = _trendingState.asStateFlow()
  ```
- Launch work with `viewModelScope.launch`. Never `GlobalScope`, never a raw `CoroutineScope`.
- Guard re-entrant refreshes with a `Job?` field (`if (job?.isActive == true) return`) — see `HomeViewModel.refreshHome()`.

## UI state

Declare a `sealed interface` in the same file as the ViewModel, below the class:

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

Every ViewModel has a matching `<Name>ViewModelTest.kt` under
`app/src/test/java/com/fagundes/myshowlist/feat/<feature>/vm/`, registered in `UnitTestSuite.kt`.

When you add a public method, add a test for it. When you change the constructor, update the test's construction. When you delete a method, delete its test. See `.claude/rules/testing.md`.
