#!/usr/bin/env bash
set -eu
name="$1"
out="success-${name}.md"
{
  echo "Run: ${GITHUB_SERVER_URL}/${GITHUB_REPOSITORY}/actions/runs/${GITHUB_RUN_ID}"
  echo
  echo "## Produced jars"
  echo '```'
  ls -1 dist 2>/dev/null || echo "no dist directory"
  echo '```'
} > "$out"
url=$(gh issue create --title "CI success (${name}) run ${GITHUB_RUN_ID}" --body-file "$out")
gh issue close "$url"
