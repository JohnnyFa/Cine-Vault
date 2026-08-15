---
paths:
  - "**/*.gradle.kts"
  - "gradle/libs.versions.toml"
  - "gradle.properties"
  - "**/proguard-rules.pro"
---

# Gradle / build rules

## Version catalog is the only place versions live

Every dependency and plugin comes from `gradle/libs.versions.toml` via `libs.*`. Never write a hardcoded coordinate or version string in a `build.gradle.kts`. To add a library: add the version to `[versions]`, the coordinate to `[libraries]`, then reference `libs.<alias>`.

Compose artifacts come from the BOM (`platform(libs.androidx.compose.bom)`) — don't pin their versions individually.

## Flavors — there is no plain `debug` variant

`flavorDimensions += "environment"` with `dev`, `staging`, `prod`. Every task name carries the flavor:

| Intent | Task |
|---|---|
| Compile | `./gradlew assembleDevDebug` |
| Unit tests | `./gradlew testDevDebugUnitTest` |
| Install | `./gradlew installDevDebug` |
| Release build | `./gradlew assembleProdRelease` |

`assembleDebug` and `testDebugUnitTest` do **not** exist. `dev` and `staging` add an `applicationIdSuffix`, so all three flavors can be installed side by side.

Flavor-specific config goes in `buildConfigField` / `resValue` inside the flavor block: `TMDB_BASE_URL`, `JIKAN_BASE_URL`, `LOGGING_ENABLED`, `app_name`.

## Secrets

Read through `getLocalOrEnv(key)`, which falls back from `local.properties` to the environment — that's what lets CI supply values via secrets. Never commit a key, and never add a default value that is a real key.

`local.properties` is gitignored and holds `sdk.dir` + `TMDB_API_KEY`. `app/google-services.json` and any `*.jks` must never be read into a prompt or echoed into a file.

## Versioning stays CI-overridable

```kotlin
versionCode = providers.gradleProperty("versionCode").orNull?.toInt() ?: 1
versionName = providers.gradleProperty("versionName").orNull ?: "1.0"
```

The tag-based release workflows pass `-PversionCode` / `-PversionName`. Don't replace these with literals.

## Static analysis

- ktlint (`ktlint-gradle`) with `android.set(true)`; `androidTest` is excluded. `.editorconfig` disables `ktlint_standard_naming` and exempts `@Composable` from function naming.
- detekt with `buildUponDefaultConfig = true`, config at `config/detekt/detekt.yml`, `ignoreFailures = true` locally — but **CI runs with `-PwarningsAsErrors=true`**, which flips `allWarningsAsErrors` on the Kotlin compiler. Code that compiles locally with warnings will fail CI.

Mirror the CI gate locally with `/check` before pushing.

## Toolchain

Java 17, `compileSdk`/`targetSdk` 36, `minSdk` 28, Kotlin 2.0.0, AGP 8.13.2, KSP for Room. If you touch AGP, use the `agp-9-upgrade` skill.
