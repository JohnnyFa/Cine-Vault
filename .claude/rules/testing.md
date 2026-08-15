---
paths:
  - "app/src/test/**/*.kt"
  - "app/src/androidTest/**/*.kt"
---

# Testing rules

Reference implementation: `app/src/test/java/com/fagundes/myshowlist/feat/home/vm/HomeViewModelTest.kt`.

## Layout

Unit tests mirror the main source package exactly:
`app/src/test/java/com/fagundes/myshowlist/feat/<feature>/vm/<Name>ViewModelTest.kt`

Every new test class must be added to `UnitTestSuite.kt` — both the `import` and the `@Suite.SuiteClasses` list. A test not in the suite still runs under `testDevDebugUnitTest`, but the suite is the project's declared inventory; keep it accurate.

## Coroutine setup

Standard boilerplate — copy it:

```kotlin
@OptIn(ExperimentalCoroutinesApi::class)
class FooViewModelTest {
    private val repository: FooRepository = mockk(relaxed = true)
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: FooViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        // ViewModel init blocks run eagerly — stub every collaborator BEFORE construction
        every { repository.observeThings() } returns flowOf(emptyList())
        viewModel = FooViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }
}
```

- `StandardTestDispatcher` does not run coroutines eagerly. Call `testDispatcher.scheduler.runCurrent()` (or `advanceUntilIdle()`) before asserting, or the state will still be `Idle`.
- `Dispatchers.resetMain()` + `unmockkAll()` in `@After` are mandatory; leaking them makes later tests in the suite fail in ways that look unrelated.

## Mocking Android statics

`android.util.Log` is not implemented in unit tests and returns 0/throws. Any code path that logs needs:

```kotlin
mockkStatic(Log::class)
every { Log.d(any(), any()) } returns 0
```

## Minimum coverage per ViewModel

1. Initial state after construction.
2. Each public method / user action.
3. At least one failure path — stub the repository to return `Result.failure(...)` or throw, and assert the state becomes `UiState.Error`.

Use `coEvery` for `suspend` functions, `every` for the rest; `coVerify` / `verify` to assert calls.

## Running

```bash
./gradlew testDevDebugUnitTest
./gradlew testDevDebugUnitTest --tests "com.fagundes.myshowlist.feat.home.vm.HomeViewModelTest"
```

Note the flavor: plain `test` runs all flavors and is slower; `testDebugUnitTest` does not exist because every build has a flavor dimension.
