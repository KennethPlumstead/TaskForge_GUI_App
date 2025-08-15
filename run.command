#!/usr/bin/env bash
DIR="$(cd "$(dirname "$0")" && pwd)"
JAR="$DIR/dist/TaskForge_GUI_App.jar"
if [ ! -f "$JAR" ]; then
  echo "Building..."
  (cd "$DIR" && ./build.sh)
fi
exec java -jar "$JAR"
