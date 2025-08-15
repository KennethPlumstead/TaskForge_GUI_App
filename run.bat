@echo off
set SCRIPT_DIR=%~dp0
if not exist "%SCRIPT_DIR%dist\TaskForge_GUI_App.jar" (
  echo Building...
  call "%SCRIPT_DIR%build.bat"
)
java -jar "%SCRIPT_DIR%dist\TaskForge_GUI_App.jar"
