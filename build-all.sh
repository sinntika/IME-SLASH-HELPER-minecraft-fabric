#!/usr/bin/env bash
# 全バージョン（1.20.x / 1.21.x / 26.x）を順番にビルドして dist/ に集める。
# ※ legacy と modern は共通 src/ を書き換えながらビルドするため、必ず逐次実行すること。
set -euo pipefail
cd "$(dirname "$0")"

JDK21="${JDK21:-$HOME/.jdks/temurin-21}"
JDK25="${JDK25:-$HOME/.jdks/temurin-25}"

rm -rf dist
mkdir -p dist

echo "==> [1/2] legacy (1.20.x / 1.21.x) with JDK 21"
(cd legacy && ./gradlew --no-daemon -Dorg.gradle.java.home="$JDK21" collectJars)

echo "==> [2/2] modern (26.x) with JDK 25"
(cd modern && ./gradlew --no-daemon -Dorg.gradle.java.home="$JDK25" collectJars)

echo
echo "==> done. jars in dist/:"
ls -1 dist
