# TaskForge_GUI_App — Java Swing Desktop App

A polished **local desktop app** (pure Java Swing) that lets you plan your week with style:
- Add/edit/delete tasks with a modern dark UI
- Set the week start date and **export Markdown** in one click
- **Local storage** in `~/TaskForge/data.json`
- Plans saved to `~/TaskForge/plans/`
- Double-click to run via provided launchers or the JAR

## Quickstart

```bash
./build.sh
java -jar dist/TaskForge_GUI_App.jar
```

## Build
- macOS/Linux:
  ```bash
  ./build.sh
  ```
- Windows:
  ```bat
  build.bat
  ```

## Run
- Terminal:
  ```bash
  java -jar dist/TaskForge_GUI_App.jar
  ```
- Double-click:
  - macOS: `run.command` (Right-click → Open once, or `xattr -d com.apple.quarantine run.command`)
  - Windows: `run.bat`

## Where files go
- Data: `~/TaskForge/data.json`
- Plans: `~/TaskForge/plans/Weekly_Plan_YYYY-MM-DD.md`

## Notes
- No external libraries (Java-only), Swing UI.
- You can set a **dock/window icon** (already included).
