#!/usr/bin/env bash
set -euo pipefail
mkdir -p out dist
# Compile everything targeting Java 8
find src -name "*.java" > sources.txt
javac -source 1.8 -target 1.8 -d out -cp resources @sources.txt
# Copy resources
mkdir -p out
cp -R resources/* out/ 2>/dev/null || true
# Package
jar cfm dist/TaskForge_GUI_App.jar MANIFEST.MF -C out .
echo "Built dist/TaskForge_GUI_App.jar"
