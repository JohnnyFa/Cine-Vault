---
paths:
  - "app/src/main/java/com/fagundes/myshowlist/feat/**/ui/**/*.kt"
  - "app/src/main/java/com/fagundes/myshowlist/components/**/*.kt"
  - "app/src/main/java/com/fagundes/myshowlist/ui/**/*.kt"
---

# Compose UI rules

Reference implementation: `feat/home/ui/HomeScreen.kt`.

## Screen / Content split

Every screen is two composables:

```kotlin
@Composable
fun HomeScreen(viewModel: HomeViewModel, onOpenDetail: (Int, ContentType) -> Unit) {
    val trendingState by viewModel.trendingState.collectAsStateWithLifecycle()
    HomeScreenContent(trendingState = trendingState, onOpenDetail = onOpenDetail)
}

@Composable
private fun HomeScreenContent(trendingState: HomeUiState<List<Movie>>, ...) { ... }
```

- `<Name>Screen` is the only composable that touches the ViewModel. It collects state and forwards plain values and lambdas.
- `<Name>ScreenContent` takes no ViewModel, so it is previewable and testable.
- Always collect with `collectAsStateWithLifecycle()`, never `collectAsState()` — the latter keeps collecting in the background.

## ViewModel injection

`koinViewModel()` is called **in `AppNavGraph.kt`**, inside the `composable(...)` block, and the instance is passed down as a parameter. Do not call `koinViewModel()` inside a screen or a component.

## Component conventions

- `modifier: Modifier = Modifier` is the first optional parameter, and the caller's modifier is applied to the outermost layout node.
- Hoist state: components take values and `on<Event>` lambdas, never a ViewModel.
- Shared components go in `com.fagundes.myshowlist.components`; feature-local ones in `feat/<feature>/ui/components/`.
- Add a `@Preview` composable wrapped in `MyShowListTheme { }` for `ScreenContent` and non-trivial components.
- Use `Modifier.testTag(...)` on nodes that instrumented tests need to find.

## Insets and edge-to-edge

The app is edge-to-edge (`enableEdgeToEdge()` in `MainActivity`, `compileSdk`/`targetSdk` 36).

- Apply insets **once**. Never let a parent and a child both consume the same inset.
- Inside a `Scaffold`, use the supplied `innerPadding`; pass it to a lazy list via `contentPadding`, not `Modifier.padding`, so items scroll under the bars.
- Outside a `Scaffold`, use `Modifier.safeDrawingPadding()`, or a specific inset such as `WindowInsets.statusBars.asPaddingValues()` as `HomeScreen` does.
- For IME, prefer `Modifier.imePadding()` on the scrollable container.

For anything beyond this — inset rulers, system bar legibility, keyboard animation — use the `edge-to-edge` skill.

## Images

Coil via `AsyncImage`. Always set `contentDescription` (or `null` for decorative images) and a `placeholder`/`error` for poster loads.

## Detekt / ktlint

`ktlint_function_naming_ignore_when_annotated_with=Composable` is set in `.editorconfig`, so PascalCase composable names are correct and must not be "fixed" to camelCase.
