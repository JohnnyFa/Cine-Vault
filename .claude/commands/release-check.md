---
description: Pre-flight check before pushing a release tag to Play Store or Firebase distribution
argument-hint: "<versionName> <versionCode>"
allowed-tools: Bash(./gradlew *), Bash(git *), Read, Grep, Glob
disable-model-invocation: true
---

Verify a release is ready before a tag push kicks off distribution. Arguments: `$ARGUMENTS` (versionName, then versionCode). Ask if either is missing.

## Tag format decides the destination

Both distribution workflows trigger on tag push, and the tag shape alone routes the build:

| Tag | Workflow | Goes to |
|---|---|---|
| `v1.3.9-dev+23` | `.github/workflows/firebase-distribution.yml` (`v*-dev[+]*`) | Firebase App Distribution |
| `v1.3.9+23` | `.github/workflows/playstore-distribution.yml` (`v*[+]*`) | Play Store |

The Play Store pattern `v*[+]*` also matches dev tags; a job-level `if` filters those out. Read both workflow files and confirm the tag you're about to recommend lands where the user expects — getting this wrong ships a dev build to production.

The part after `+` is the versionCode; the part after `v` (before `+`) is the versionName. They are passed to Gradle as `-PversionCode` / `-PversionName`.

## Checks

1. **Working tree is clean** and on the intended branch (`git status`, `git branch --show-current`).
2. **Tag doesn't already exist** — `git tag -l "v<versionName>+<versionCode>"`, and check the remote with `git ls-remote --tags origin`.
3. **versionCode is higher than the last released one.** List existing tags with `git tag -l --sort=-v:refname | head`. Play Store rejects a versionCode that isn't strictly increasing, and it fails after upload, not before.
4. **CI gate passes** — run `/check`, or at minimum:
   ```
   ./gradlew ktlintMainSourceSetCheck ktlintTestSourceSetCheck detekt lint testDevDebugUnitTest -PwarningsAsErrors=true
   ```
5. **Release build with the real version parameters:**
   ```
   ./gradlew assembleProdRelease -PversionCode=<code> -PversionName=<name>
   ```
   This exercises R8. Minification is on for release, so this is where missing keep rules surface — a debug build passing proves nothing about release. If R8 strips something (typically Kotlinx Serialization DTOs or Room), use the `r8-analyzer` skill rather than adding a broad `-keep class **`.
6. **Signing** — `signingConfigs.release` is populated from `KEYSTORE_FILE`/`KEYSTORE_PASSWORD`/`KEY_ALIAS`/`KEY_PASSWORD` via `getLocalOrEnv`, and is only applied when `storeFile != null`. Locally these are usually absent, so the release build is unsigned; that's expected — CI supplies them as secrets. Confirm the workflow references those secret names. **Never print, echo, or write these values.**

## Output

Report each check as pass/fail, state the exact tag string to push and which workflow it will trigger, and stop there. **Do not create or push the tag** — that's a release action and belongs to the user.
