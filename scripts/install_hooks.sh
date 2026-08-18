#!/usr/bin/env bash
set -euo pipefail

root="$(cd "$(dirname "$0")/.." && pwd)"
cd "$root"

if ! git rev-parse --is-inside-work-tree >/dev/null 2>&1; then
    echo "Not a git repository: $root" >&2
    exit 1
fi

git config core.hooksPath .githooks
chmod +x .githooks/*

echo "Installed Git hooks from .githooks (core.hooksPath=.githooks)"
echo "Pre-commit runs: spotless:check, compile, unit tests"
