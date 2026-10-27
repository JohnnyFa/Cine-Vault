---
description: Review uncommitted changes before committing — branch, hygiene, architecture, tests, and the CI gate
argument-hint: "[--fast]"
allowed-tools: Read, Grep, Glob, Agent, Bash(git *), Bash(gh pr list:*), Bash(./gradlew *)
---

Check the work in progress **before it is committed**. Nothing gets committed until this review says it is ready. This command reports and fixes what the gate auto-fixes; it does not commit, push, or open a PR on its own.

Arguments: `$ARGUMENTS` — if it contains `--fast`, skip the final `assembleDevDebug` step of the gate.

## 1. Branch

```
git branch --show-current
```

- On `main` → **stop**. Nothing is committed to `main` directly. Create a branch first (`feat/<name>`, `fix/<name>`, `refactor/<name>`, `ci/<name>`, `docs/<name>`) and carry the changes over with `git switch -c <branch>`.
- On a branch whose PR is already merged (`gh pr list --head <branch> --state merged`) → **stop**. That branch should have been deleted (see "After the merge" below). Start a new branch from an up-to-date `main`.

## 2. What is about to be committed

```
git status --short
git diff HEAD
```

Also read every untracked file that will be added. If there is nothing to commit, say so and stop.

Review the whole change, not just the staged part: an unstaged edit that the staged one depends on is a broken commit.

## 3. Commit hygiene — blockers

Any of these blocks the commit:

- **Secrets.** `local.properties`, `*.jks`, `*.keystore`, `*.p12`, `keystore.properties`, or a literal key/token in code or Gradle files (`api_key=`, `TMDB_API_KEY = "…"`, `eyJ…` JWTs, 32-char hex strings). Secrets reach the build only through `getLocalOrEnv(key)`. Do not open these files to check them; `git status` showing them as staged is enough. `app/google-services.json` is already tracked on purpose — only flag it if it changed.
- **Build output and local junk.** `build/`, `.kotlin/`, `.idea/`, `*.apk`, `*.aab`, `captures/`, `.DS_Store`, `.claude/settings.local.json`.
- **Leftovers.** `println`, debug `Log.d` calls added for investigation, commented-out code, `TODO` without context.
- **Unrelated changes.** Edits that don't belong to this commit's purpose go to their own commit or branch.

## 4. Code review

Hand the diff to the `android-code-reviewer` agent: it holds the architecture and convention checklist (`.claude/rules/*.md`). Tell it to review the uncommitted diff (`git diff HEAD` plus untracked files), not `main...HEAD`.

On top of that, check these points, which have slipped through before:

- **Every ViewModel has a test.** Added or changed ViewModel → its test in the same package under `app/src/test/java/`, registered in `UnitTestSuite.kt`. A use case with real logic (a branch, a guard, a mapping) gets a test too.
- **A test must fail without the change it covers.** If you can't explain why it would fail without the fix, it doesn't test the fix (e.g. a collector that never runs because `runCurrent()` is missing).
- **User actions during `Loading`.** A button that starts a flow (sign-in, save, delete) is disabled while another flow is in flight, and the ViewModel guards the same state. Firebase and network calls can't always be cancelled, so a second action can race the first.
- **Strings exist in both `values/` and `values-pt/`.** No hardcoded user-facing text in new composables.
- **Feature boundaries.** No `feat/` imports another `feat/`; `core/` never imports `feat/` (except `AppModule.kt` and `AppNavGraph.kt`); no `domain/` imports Room, DAOs, or entities.
- **Wiring.** New ViewModel or use case registered in `AppModule.kt`; new route in `AppRoutes.kt` and `AppNavGraph.kt`; Room schema change has a version bump and a migration.

## 5. CI gate

The same steps as `/check`, run here directly (`/check` is user-only, so it can't be invoked from this command):

```
./gradlew ktlintFormat
./gradlew ktlintMainSourceSetCheck ktlintTestSourceSetCheck detekt lint testDevDebugUnitTest -PwarningsAsErrors=true
./gradlew assembleDevDebug        # skip with --fast
```

`-PwarningsAsErrors=true` is what CI uses; it is the usual reason a green local build fails on the PR. If `ktlintFormat` changed files, those changes are part of the commit. Detekt runs with `ignoreFailures = true`, so read `app/build/reports/detekt/detekt.txt` and only count findings in the touched files. On a failure, follow the "On failure" section of `.claude/commands/check.md`.

A change that touches no Kotlin, Gradle, or resource file (docs, `.claude/`, workflow comments) can't change the gate's result: say so and skip it.

## 6. Verdict

Report in this order:

1. **Blocked** or **Ready to commit**, in one line.
2. Blockers, most severe first, each with `file:line`, what is wrong, and the consequence.
3. Non-blocking notes (convention, cleanups).
4. Gate result per step (ktlint, detekt, lint, unit tests, build).
5. When ready: the files to stage and a commit message in the repo's style (`feat(scope): …`, `fix(scope): …`, `ci: …`, `build: …`), imperative, with a body that says why.

Don't commit unless the user asks.

## After the merge — delete the branch

Every branch is deleted as soon as its PR is merged. A merged branch left behind gets reused by mistake and drifts from `main`.

Merge and delete in one step:

```
gh pr merge <number> --merge --delete-branch
```

Or, if the PR was merged on GitHub:

```
git push origin --delete <branch>
git switch main
git pull --ff-only
git branch -d <branch>
```

`git branch -d` (lowercase) refuses to delete a branch that isn't merged. If it refuses, check before reaching for `-D`.
