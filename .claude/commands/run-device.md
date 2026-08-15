---
description: Build, install, and launch the app on a connected device or emulator, then screenshot it
argument-hint: "[dev|staging|prod]"
allowed-tools: Bash(./gradlew *), Bash(android *), Bash(adb *), Read
disable-model-invocation: true
---

Build and run CINE VAULT on a device or emulator and visually confirm it came up.

Flavor: `$ARGUMENTS` — default to `dev` if not specified.

## Steps

1. **Find a target.** `android emulator list` for AVDs, `adb devices` for what's attached. If nothing is running, boot an AVD with `android emulator start <name>` and wait for it. If there are no AVDs at all, say so and stop — don't create one unasked.

2. **Install.** Task name is flavor-specific:
   ```
   ./gradlew install<Flavor>Debug
   ```
   e.g. `installDevDebug`. There is no `installDebug`.

3. **Launch.** The application id carries a suffix for non-prod flavors:

   | Flavor | Application id |
   |---|---|
   | dev | `com.fagundes.myshowlist.dev` |
   | staging | `com.fagundes.myshowlist.staging` |
   | prod | `com.fagundes.myshowlist` |

   ```
   adb shell am start -n <applicationId>/com.fagundes.myshowlist.MainActivity
   ```

4. **Verify it actually rendered.** Take a screenshot with `android screenshot` and look at it. The app opens on the login screen unless a Firebase session is cached, so a Google sign-in screen is the expected first frame, not a failure.

5. If it crashed, pull the stack trace:
   ```
   adb logcat -d -t 200 *:E
   ```
   Report the actual exception rather than guessing.

## Notes

- `TMDB_API_KEY` must be present in `local.properties` or network calls silently return empty lists — the build prints a warning when it's missing. Do not read or print the key itself.
- Don't sign in on the user's behalf. If the flow needs credentials, stop and hand it back to them.
