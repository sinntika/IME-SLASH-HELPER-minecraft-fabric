#!/usr/bin/env bash
set -eu
name="$1"
log="$2"
out="report-${name}.md"
if [ ! -f "$log" ]; then
  echo "no log file: $log" > "$log"
fi
{
  echo "Run: ${GITHUB_SERVER_URL}/${GITHUB_REPOSITORY}/actions/runs/${GITHUB_RUN_ID}"
  echo
  echo "## What went wrong"
  echo '```'
  sed -n '/FAILURE: Build failed with an exception/,/^\* Exception is:/p' "$log" | head -n 150
  echo '```'
  echo
  echo "## Error lines"
  echo '```'
  grep -n -E 'error:|^e: |FAILED|Caused by:|Unresolved|cannot find|Could not' "$log" | head -n 100 || true
  echo '```'
  echo
  echo "## Tail"
  echo '```'
  tail -n 150 "$log"
  echo '```'
} > "$out"
gh issue create --title "CI failure (${name}) run ${GITHUB_RUN_ID}" --body-file "$out"
