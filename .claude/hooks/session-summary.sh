#!/usr/bin/env bash
# Stop — if Kotlin was edited this session, remind that the CI-equivalent gate
# hasn't run yet, then clear the queue.
#
# Does not invoke Gradle: a Stop hook blocks the turn from ending, and a
# 30s build at the end of every turn is worse than the reminder.
#
# Fails open: any internal error exits 0.

set -uo pipefail

project_dir="${CLAUDE_PROJECT_DIR:-$(pwd)}"
queue="$project_dir/.claude/.kt-touched"

[ -s "$queue" ] || exit 0

files=$(sort -u "$queue" 2>/dev/null | sed "s|^$project_dir/||")
count=$(printf '%s\n' "$files" | grep -c . 2>/dev/null || echo 0)
: > "$queue" 2>/dev/null || true

shown=$(printf '%s\n' "$files" | head -10 | sed 's/^/  • /')
[ "$count" -gt 10 ] && shown="$shown"$'\n'"  … and $((count - 10)) more"

msg="Edited $count Kotlin file(s) this session:
$shown

Static analysis and tests have not run. Use /check to mirror the CI gate
(ktlint + detekt + lint + testDevDebugUnitTest -PwarningsAsErrors=true)."

jq -n --arg m "$msg" '{systemMessage: $m, continue: true}' 2>/dev/null

exit 0
