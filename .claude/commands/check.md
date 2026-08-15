---
description: Run the full CI-equivalent gate locally (ktlint, detekt, lint, unit tests, build)
argument-hint: "[--fast]"
allowed-tools: Bash(./gradlew *), Read, Edit, Grep, Glob
disable-model-invocation: true
---

Mirror the `quality-checks` and `build-validation` jobs from `.github/workflows/android-ci.yml` locally, so failures surface here instead of on the PR.

Arguments: `$ARGUMENTS` — if it contains `--fast`, skip the final `assembleDevDebug` step.

## Steps

1. **Auto-format first**, so formatting noise doesn't show up as failures:
   ```
   ./gradlew ktlintFormat
   ```
   If it changed files, say which ones.

2. **Run the gate exactly as CI does** — note `-PwarningsAsErrors=true`, which turns Kotlin compiler warnings into errors and is the most common local/CI divergence:
   ```
   ./gradlew ktlintMainSourceSetCheck ktlintTestSourceSetCheck detekt lint testDevDebugUnitTest -PwarningsAsErrors=true
   ```

3. **Build validation** (skip if `--fast`):
   ```
   ./gradlew assembleDevDebug
   ```

## On failure

Fix the failures rather than reporting them and stopping — that's the point of the command.

- **ktlint** — usually auto-fixable; re-run `ktlintFormat`. If it persists it's a real rule violation (wildcard imports, naming). Remember `.editorconfig` exempts `@Composable` from function naming, so PascalCase composables are correct.
- **detekt** — `ignoreFailures = true` locally, so detekt won't fail the build; it still reports. Read `app/build/reports/detekt/` and fix genuine findings.
- **Compiler warnings-as-errors** — unused variables, deprecations, unchecked casts. Fix the warning; do not add `@Suppress` unless there's no alternative, and say so if you do.
- **Unit tests** — read the failing test report under `app/build/reports/tests/testDevDebugUnitTest/`. Common causes are documented in `.claude/rules/testing.md` (missing `runCurrent()`, unstubbed `Log`, missing `Dispatchers.resetMain()`).
- **Android lint** — report at `app/build/reports/lint-results-*.html`.

Re-run the failed step after fixing. Report the final state plainly: which steps passed, what you changed, and anything you could not fix.
