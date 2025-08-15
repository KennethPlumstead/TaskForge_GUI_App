
@echo off
if not exist out mkdir out
if not exist dist mkdir dist
dir /S /B src\*.java > sources.txt
javac -source 1.8 -target 1.8 -d out -cp resources @sources.txt
xcopy /E /I /Y resources out >NUL
jar cfm dist\TaskForge_GUI_App.jar MANIFEST.MF -C out .
echo Built dist\TaskForge_GUI_App.jar
