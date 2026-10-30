#!/usr/bin/env bash
# PreToolUse(Bash) — refuse `git commit` unless /review passed on exactly what is being committed.
#
# When /review reaches "Ready to commit" it records `git write-tree` (a hash of the index)
# in .claude/.review-passed. A commit is allowed only while the index still hashes to that
# value, so staging anything after the review means reviewing again. Commits that take
# content from outside the index (-a, -o/--only, -i/--include, -p, pathspecs) are refused,
# because the hash can't vouch for them, and so are commits that follow an index-changing git
# command (add, rm, reset, stash, ...) in the same Bash call: the hook runs before the whole
# command, so it would compare the index as it was before that command. A CLAUDE.md line is
# advice; this is enforcement.
#
# The command is parsed with shlex, so a quoted "git commit" in an echo or a PR body, or a
# commit message containing "-a", isn't mistaken for a real commit or flag. Commands nested in
# sh -c, eval, backticks and $(...), or behind wrappers like env/time/nohup/sudo/xargs, are
# checked too. Not covered: git aliases (`git ci`), commits in another worktree, and a `cd`
# earlier in the command (checked against the session's directory; that errs toward denying).
#
# Fails open: any internal error, or a missing python3, exits 0 so a broken hook never
# wedges a session.

command -v python3 >/dev/null 2>&1 || exit 0

read -r -d '' CHECK <<'PY'
import json, os, re, shlex, subprocess, sys

def allow():
    sys.exit(0)

def deny(reason):
    print(json.dumps({"hookSpecificOutput": {
        "hookEventName": "PreToolUse",
        "permissionDecision": "deny",
        "permissionDecisionReason": reason,
    }}))
    sys.exit(0)

try:
    payload = json.load(sys.stdin)
    command = payload.get("tool_input", {}).get("command") or ""
except Exception:
    allow()
if "commit" not in command:
    allow()

SHELLS = {"sh", "bash", "zsh", "dash", "ksh"}
# Wrappers that run the command after them, with the flags of theirs that take a value.
WRAPPERS = {
    "env": {"-u", "--unset", "-C", "--chdir"},
    "command": set(), "exec": {"-a"}, "time": set(), "nohup": set(),
    "nice": {"-n", "--adjustment"},
    "sudo": {"-u", "-g", "-C", "-D", "-h", "-p", "-r", "-t", "-U"},
    "xargs": {"-n", "-I", "-L", "-P", "-d", "-s", "-E", "-a"},
}
# Keywords that can open a simple command (`do git add`, `then git commit`, `{ git add x; }`).
SHELL_KEYWORDS = {"do", "then", "else", "elif", "if", "while", "until", "{", "!"}
GIT_OPTS_WITH_VALUE = {"-C", "-c", "--git-dir", "--work-tree", "--namespace", "--exec-path", "--super-prefix", "--config-env"}

# git commit's long options. git accepts any unambiguous prefix, so names are resolved below.
COMMIT_LONG = {
    **dict.fromkeys(["quiet", "verbose", "reset-author", "short", "branch", "porcelain", "long", "null",
                     "signoff", "no-signoff", "no-verify", "verify", "allow-empty", "allow-empty-message",
                     "edit", "no-edit", "amend", "no-post-rewrite", "pathspec-file-nul", "dry-run", "status",
                     "no-status", "no-gpg-sign", "ahead-behind", "no-ahead-behind"], "flag"),
    **dict.fromkeys(["file", "author", "date", "message", "reuse-message", "reedit-message", "fixup",
                     "squash", "template", "trailer", "cleanup"], "value"),
    # The value is optional and only ever attached with "=".
    **dict.fromkeys(["gpg-sign", "untracked-files"], "optional"),
    **dict.fromkeys(["all", "only", "include", "patch", "interactive", "pathspec-from-file"], "outside"),
}
SHORT_WITH_VALUE = set("mFCct")
SHORT_OPTIONAL_ATTACHED = set("Su")  # -S<keyid>, -u<mode>: whatever follows the letter is the value
OUTSIDE_INDEX_SHORT = {"a": "-a", "o": "-o", "i": "-i", "p": "-p"}


def strip_heredocs(cmd):
    # Heredoc bodies are data (often the commit message itself), not shell words.
    return re.sub(r"<<-?\s*(['\"]?)(\w+)\1[^\n]*\n.*?\n\s*\2\s*(?=\n|$)", "<<HEREDOC", cmd, flags=re.S)


def substitutions(cmd):
    """Bodies of `...` and $(...), which run as commands even inside double quotes."""
    found = re.findall(r"`([^`]*)`", cmd)
    i = 0
    while True:
        i = cmd.find("$(", i)
        if i < 0:
            return found
        depth, j = 1, i + 2
        while j < len(cmd) and depth:
            depth += {"(": 1, ")": -1}.get(cmd[j], 0)
            j += 1
        found.append(cmd[i + 2:j - 1])
        i = j


def split_segments(cmd):
    """Simple commands, split on ; & | ( ) and newlines."""
    lexer = shlex.shlex(cmd.replace("\n", " ; "), posix=True, punctuation_chars=";&|()")
    lexer.whitespace_split = True
    segments, current = [], []
    for tok in lexer:
        if tok and set(tok) <= set(";&|()"):
            segments.append(current)
            current = []
        else:
            current.append(tok)
    segments.append(current)
    return segments


def strip_wrappers(seg):
    i = 0
    while i < len(seg):
        word = seg[i]
        if re.match(r"^[A-Za-z_][A-Za-z0-9_]*=", word) or word in SHELL_KEYWORDS:
            i += 1
        elif os.path.basename(word) in WRAPPERS:
            takes_value = WRAPPERS[os.path.basename(word)]
            i += 1
            while i < len(seg) and seg[i].startswith("-"):
                i += 2 if seg[i] in takes_value else 1
        else:
            break
    return seg[i:]


def all_segments(cmd, depth=0):
    if depth > 4:
        return []
    cmd = strip_heredocs(cmd).replace("\\\n", " ")
    result = []
    for body in substitutions(cmd):
        result += all_segments(body, depth + 1)
    try:
        segments = split_segments(cmd)
    except ValueError:
        return result
    for seg in segments:
        seg = strip_wrappers(seg)
        if not seg:
            continue
        name = os.path.basename(seg[0])
        if name in SHELLS:
            # sh -c "...", also bundled as -lc / -ec.
            for k, word in enumerate(seg[1:], start=1):
                if word.startswith("-") and not word.startswith("--") and "c" in word and k + 1 < len(seg):
                    result += all_segments(seg[k + 1], depth + 1)
                    break
        elif name == "eval":
            result += all_segments(" ".join(seg[1:]), depth + 1)
        else:
            result.append(seg)
    return result


def git_invocation(seg):
    """Returns (repo dir from -C or None, subcommand, args) if seg runs git, else None."""
    if not seg or os.path.basename(seg[0]) != "git":
        return None
    i, repo = 1, None
    while i < len(seg) and seg[i].startswith("-"):
        opt = seg[i]
        if opt in GIT_OPTS_WITH_VALUE:
            if opt == "-C" and i + 1 < len(seg):
                repo = seg[i + 1] if repo is None else os.path.join(repo, seg[i + 1])
            i += 2
        else:
            i += 1
    if i >= len(seg):
        return None
    return repo, seg[i], seg[i + 1:]


# Subcommands that can change the index. The hook runs once, before the whole Bash command, so
# one of these ahead of a commit in the same command would change what gets committed after the
# index was already compared with the marker.
INDEX_MUTATING = {"add", "stage", "rm", "mv", "reset", "restore", "checkout", "switch", "apply",
                  "update-index", "read-tree", "stash", "merge", "cherry-pick", "revert", "pull",
                  "rebase", "am", "submodule", "mergetool"}


def resolve_long(name):
    if name in COMMIT_LONG:
        return name
    matches = [option for option in COMMIT_LONG if option.startswith(name)]
    return matches[0] if len(matches) == 1 else None


def outside_index_reason(args):
    i = 0
    while i < len(args):
        arg = args[i]
        if arg == "--":
            return "a pathspec after --" if i + 1 < len(args) else None
        if arg.startswith("--"):
            written = arg[2:].split("=", 1)[0]
            option = resolve_long(written)
            if option is None:
                return f"--{written}, which isn't a git commit option this hook recognises (write the full name)"
            kind = COMMIT_LONG[option]
            if kind == "outside":
                return "--" + option
            i += 2 if (kind == "value" and "=" not in arg) else 1
            continue
        if arg.startswith("-") and len(arg) > 1:
            for j, letter in enumerate(arg[1:]):
                if letter in OUTSIDE_INDEX_SHORT:
                    return OUTSIDE_INDEX_SHORT[letter]
                if letter in SHORT_OPTIONAL_ATTACHED:
                    i += 1
                    break
                if letter in SHORT_WITH_VALUE:
                    # The value is the rest of this token, or the next token.
                    i += 1 if j + 2 < len(arg) else 2
                    break
            else:
                i += 1
            continue
        redirect = re.match(r"^\d*(<<?|>>?)(.*)$", arg)
        if redirect:
            # A bare operator (`>`, `2>`) takes the next token as its target.
            i += 1 if redirect.group(2) else 2
            continue
        return "the pathspec " + arg
    return None


project_dir = os.environ.get("CLAUDE_PROJECT_DIR") or os.getcwd()
marker = os.path.join(project_dir, ".claude", ".review-passed")

def git(*args, cwd):
    return subprocess.run(["git", *args], cwd=cwd, capture_output=True, text=True, timeout=5)

try:
    project_top = git("rev-parse", "--show-toplevel", cwd=project_dir).stdout.strip()
except Exception:
    allow()

base_dir = payload.get("cwd") or project_dir

index_changed_by = None
for seg in all_segments(command):
    found = git_invocation(seg)
    if not found:
        continue
    repo, subcommand, args = found
    if subcommand != "commit" and subcommand not in INDEX_MUTATING:
        continue
    cwd = os.path.join(base_dir, repo) if repo else base_dir
    try:
        top = git("rev-parse", "--show-toplevel", cwd=cwd).stdout.strip()
    except Exception:
        continue
    if not top or top != project_top:
        continue  # another repository or a separate worktree; /review doesn't cover it

    if subcommand != "commit":
        index_changed_by = index_changed_by or subcommand
        continue
    if index_changed_by:
        deny(f"git {index_changed_by} runs before git commit in the same command, so the commit would include "
             "changes /review never saw. Run it on its own, run /review, then commit in a separate command.")

    reason = outside_index_reason(args)
    if reason:
        deny(f"git commit with {reason} commits content /review never checked: it isn't taken from the index. "
             "Stage the files with git add, run /review, then run a plain git commit.")

    try:
        current = git("write-tree", cwd=project_dir).stdout.strip()
    except Exception:
        allow()
    if not current:
        allow()
    if not os.path.isfile(marker):
        deny("No /review has passed for this change. Run /review; it records the staged tree when it says Ready to commit.")
    reviewed = open(marker).read().strip()
    if current != reviewed:
        deny("The staged changes differ from what /review approved. Run /review again before committing. "
             "If this command stages files and commits in one go, stage first, review, then commit.")

allow()
PY

python3 -c "$CHECK"
exit 0
