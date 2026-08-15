#!/usr/bin/env bash
# PostToolUse(Edit|Write) — format and sanity-check edited Kotlin.
#
# Deliberately does NOT invoke Gradle. `./gradlew ktlintFormat` is a 10-30s
# round trip; running it after every file write makes editing unusable.
# Gradle-based formatting and detekt belong in /check.
#
# If the standalone ktlint CLI is installed it formats the file in ~1s
# (honouring .editorconfig). Otherwise it falls back to instant grep checks.
#
# Fails open: any internal error exits 0.

set -uo pipefail

payload=$(cat 2>/dev/null) || exit 0
[ -n "$payload" ] || exit 0

file_path=$(printf '%s' "$payload" | jq -r '.tool_input.file_path // empty' 2>/dev/null) || exit 0
[ -n "$file_path" ] || exit 0

case "$file_path" in
  *.kt|*.kts) ;;
  *) exit 0 ;;
esac

[ -f "$file_path" ] || exit 0

project_dir="${CLAUDE_PROJECT_DIR:-$(pwd)}"
notes=()

# --- format ---------------------------------------------------------------
if command -v ktlint >/dev/null 2>&1; then
  before=$(cksum < "$file_path" 2>/dev/null)
  ktlint_out=$(cd "$project_dir" && ktlint --format --relative --log-level=error "$file_path" 2>&1)
  after=$(cksum < "$file_path" 2>/dev/null)
  [ "$before" != "$after" ] && notes+=("ktlint reformatted this file — re-read it before making further edits.")
  # Anything ktlint could not auto-fix.
  if [ -n "$ktlint_out" ]; then
    remaining=$(printf '%s' "$ktlint_out" | head -5)
    notes+=("ktlint could not auto-fix: ${remaining}")
  fi
else
  notes+=("ktlint CLI not installed — only fast heuristic checks ran. \`brew install ktlint\` enables real auto-formatting on edit.")
fi

# --- fast heuristics ------------------------------------------------------
grep -qE '^import .*\*$' "$file_path" 2>/dev/null &&
  notes+=("Wildcard import present; ktlint's no-wildcard-imports rule will fail CI.")

# BSD grep (macOS) has no -P, so match a literal tab.
grep -q "$(printf '\t')" "$file_path" 2>/dev/null &&
  notes+=("Tab characters present; this project indents with spaces.")

grep -qE ' +$' "$file_path" 2>/dev/null &&
  notes+=("Trailing whitespace present.")

[ -n "$(tail -c 1 "$file_path" 2>/dev/null)" ] &&
  notes+=("File does not end with a newline.")

grep -qE '(^|[^.[:alnum:]_])println\(' "$file_path" 2>/dev/null &&
  notes+=("println() found; this project logs through android.util.Log gated on BuildConfig.LOGGING_ENABLED.")

grep -qE 'GlobalScope' "$file_path" 2>/dev/null &&
  notes+=("GlobalScope found; use viewModelScope (ViewModels) or an injected scope.")

grep -qE 'collectAsState\(\)' "$file_path" 2>/dev/null &&
  notes+=("collectAsState() found; this project uses collectAsStateWithLifecycle().")

# --- mandatory ViewModel test --------------------------------------------
case "$file_path" in
  *"/feat/"*"/vm/"*ViewModel.kt)
    vm_name=$(basename "$file_path" .kt)
    feature=$(printf '%s' "$file_path" | sed -n 's|.*/feat/\([^/]*\)/vm/.*|\1|p')
    test_file="$project_dir/app/src/test/java/com/fagundes/myshowlist/feat/${feature}/vm/${vm_name}Test.kt"
    if [ ! -f "$test_file" ]; then
      notes+=("No test for ${vm_name}. CLAUDE.md requires app/src/test/java/com/fagundes/myshowlist/feat/${feature}/vm/${vm_name}Test.kt, registered in UnitTestSuite.kt.")
    elif [ "$file_path" -nt "$test_file" ]; then
      notes+=("${vm_name} changed after ${vm_name}Test.kt was last touched — check the test still covers its public surface.")
    fi
    ;;
esac

# --- queue for the Stop hook ---------------------------------------------
if [ -d "$project_dir/.claude" ]; then
  printf '%s\n' "$file_path" >> "$project_dir/.claude/.kt-touched" 2>/dev/null || true
fi

[ ${#notes[@]} -eq 0 ] && exit 0

context=$(printf '%s\n' "${notes[@]}")
jq -n --arg ctx "$context" '{
  hookSpecificOutput: {
    hookEventName: "PostToolUse",
    additionalContext: $ctx
  }
}' 2>/dev/null

exit 0
