#!/usr/bin/env bash
# PreToolUse(Edit|Write) — refuse writes to files that hold credentials.
#
# These files are either gitignored secrets (local.properties holds TMDB_API_KEY)
# or signing material. A CLAUDE.md line is advice; this is enforcement.
#
# Fails open: any internal error exits 0 so a broken hook never wedges a session.

set -uo pipefail

payload=$(cat 2>/dev/null) || exit 0
[ -n "$payload" ] || exit 0

file_path=$(printf '%s' "$payload" | jq -r '.tool_input.file_path // .tool_input.notebook_path // empty' 2>/dev/null) || exit 0
[ -n "$file_path" ] || exit 0

base=$(basename "$file_path")

deny() {
  jq -n --arg reason "$1" '{
    hookSpecificOutput: {
      hookEventName: "PreToolUse",
      permissionDecision: "deny",
      permissionDecisionReason: $reason
    }
  }' 2>/dev/null
  exit 0
}

case "$base" in
  local.properties)
    deny "local.properties holds sdk.dir and TMDB_API_KEY and is gitignored. Edit it by hand — see CLAUDE.local.md for the required keys." ;;
  google-services.json)
    deny "google-services.json is Firebase configuration. Re-download it from the Firebase console rather than editing it." ;;
  .env|.env.*)
    deny "Environment files hold secrets. Edit them by hand." ;;
  *.jks|*.keystore|*.p12|*.pem|*.key)
    deny "Signing material must not be written by an agent. Generate or install it manually." ;;
esac

# keystore.properties, signing.properties, etc.
case "$file_path" in
  *keystore.properties|*signing.properties)
    deny "Signing configuration holds passwords. Edit it by hand." ;;
esac

exit 0
