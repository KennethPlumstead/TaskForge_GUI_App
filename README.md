# TaskForge — Weekly Planner (Java GUI App)

![TaskForge Demo](assets/demo.gif)

**TaskForge 1.2.0** is a Java Swing-based GUI application developed as part of an academic assignment during my Mobile Web Development studies.  
This project is **fictional** and created purely for **educational purposes** and to **showcase programming skills**.

---

## ✨ Features
- 📅 **Weekly Planning** — Set a custom week start date
- ➕ **Task Management** — Add, edit, and delete tasks
- 🏷️ **Task Details** — Assign priority, due date, estimated hours, and tags
- 💾 **Local Save** — Keep your plans stored locally
- 📤 **Markdown Export** — Generate clean, shareable markdown reports
- 👀 **Live Preview** — Instantly view your week’s plan in a readable format
- 🎨 **Polished GUI** — User-friendly interface with clear layout

---

## 📹 Demo
The animation below demonstrates adding tasks, previewing the week plan, and exporting to Markdown:

![TaskForge Demo](assets/demo.gif)

---

## 🚀 How to Run

**Prerequisites:**
- Java 8 or higher installed (verify with `java -version` in Terminal).

**Steps:**
1. **Clone this repository:**
   ```bash
   git clone https://github.com/YourUsername/TaskForge_GUI_App.git
   ```
2. **Navigate to the project folder:**
   ```bash
   cd TaskForge_GUI_App
   ```
3. **Build the application:**
   ```bash
   ./build.sh
   ```
4. **Run the application:**
   ```bash
   java -jar dist/TaskForge_GUI_App.jar
   ```

💡 On macOS, you may need to give execution permissions:  
```bash
chmod +x build.sh run.command
```
Then you can double-click `run.command` to launch the app.

---

## 📂 Project Structure
```
TaskForge_GUI_App/
│── assets/                    # Screenshots, demo GIFs, and media
│── src/                       # Java source code
│   └── com/kenneth/taskforge  # Package namespace
│── dist/                       # Built JAR files
│── build.sh                    # Build script
│── run.command                 # Quick launch script for macOS
│── README.md                   # Project documentation
```
---

## 🛠 How It Works
1. When you launch TaskForge, set your desired **week start date**.
2. Choose how many tasks you want to plan for that week.
3. Fill in each task’s **title, priority, due date, estimated hours, and optional tags**.
4. Save your plan locally, or **export** it to a Markdown file for sharing.
5. Use the **Preview** button to see your weekly schedule in a clean, readable format.

---

## 📜 License & Notice
This application was created solely for **educational purposes** and is not intended for commercial use.  
All features, names, and design elements are fictional.

