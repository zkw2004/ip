# FRIDAY User Guide

![FRIDAY logo](../F.R.I.D.A.Y.png)

FRIDAY is a personal task assistant for managing to-dos, deadlines, and events.
She is composed, precise, and quietly witty—efficient without being cold.

> [!TIP]
> Start with the graphical interface for a compact chat experience, or use the
> console interface when you need archive commands and file-backed persistence.

## Quick start

### Prerequisites

- [JDK 25](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk)
- macOS, Linux, or Windows
- A terminal, or IntelliJ IDEA

### Run the GUI

From the project root:

```bash
./gradlew run
```

On Windows, use `gradlew.bat run`.

The GUI opens a resizable FRIDAY window. Type a command in the input field and
press **Enter** or select **Send**.

### Run the console

In IntelliJ IDEA, open `src/main/java/friday/Friday.java` and run its `main`
method. Alternatively, create the self-contained JAR and run it:

```bash
./gradlew shadowJar
java -jar build/libs/friday-all.jar
```

On Windows, replace `./gradlew` with `gradlew.bat`.

## Command reference

### Create tasks

| Command | Format | Example |
| --- | --- | --- |
| To-do | `todo <description>` | `todo review pull request` |
| Deadline | `deadline <description> /by <yyyy-MM-dd>` | `deadline submit report /by 2026-09-30` |
| Event | `event <description> /from <yyyy-MM-dd HH:mm> /to <yyyy-MM-dd HH:mm>` | `event design review /from 2026-10-01 14:00 /to 2026-10-01 15:30` |

### View and search tasks

| Command | Description | Example |
| --- | --- | --- |
| `list` | Displays every active task in order. | `list` |
| `find <keyword>` | Finds active tasks whose descriptions contain the keyword. | `find report` |

### Update tasks

Task numbers are one-based: the first task is task `1`.

| Command | Description | Example |
| --- | --- | --- |
| `mark <task number>` | Marks a task as complete. | `mark 1` |
| `unmark <task number>` | Marks a task as incomplete. | `unmark 1` |
| `delete <task number>` | Removes a task. | `delete 2` |

### Archive tasks (console only)

Archive commands require the console interface because they update both the
active and archive data files.

| Command | Description | Example |
| --- | --- | --- |
| `archive <task number>` | Moves one active task to the archive. | `archive 2` |
| `archive all` | Moves every active task to the archive. | `archive all` |
| `list-archive` | Displays archived tasks. | `list-archive` |
| `unarchive <archive number>` | Restores one archived task. | `unarchive 1` |
| `unarchive all` | Restores every archived task. | `unarchive all` |

### End a console session

```text
bye
```

FRIDAY responds with “Standing by.” and ends the console session.

## Example session

```text
Good day, Mr Stark. I am FRIDAY.
What can I assist you with?

> todo prepare investor presentation
Understood. I've added this task.

> deadline send slides /by 2026-09-30
Understood. I've added this task.

> list
Here are the tasks in your list:
1. [T][ ] prepare investor presentation
2. [D][ ] send slides (by: Sep 30 2026)

> mark 1
Confirmed. I've marked this task as done.

> bye
Standing by.
```

## Input and error handling

FRIDAY accepts leading/trailing whitespace and normalizes repeated whitespace
between command parts. Invalid input is rejected without changing your task
list.

Examples of handled errors:

```text
todo
mark 0
event meeting /from 2026-10-01 15:00 /to 2026-10-01 14:00
deadline report /by 2026-02-30
todo prepare report
todo prepare report
```

The last command is rejected as a duplicate. Error messages explain the
correction needed and begin with FRIDAY’s calm warning, “That presents a
complication.”

> [!WARNING]
> Dates use strict ISO formats. February 30, invalid times, and events whose
> end is not later than their start are not accepted.

## Data and recovery

The console stores active tasks in `data/friday.txt` and archived tasks in
`data/archive.txt`. These files are created when needed.

- A missing file starts with an empty list.
- Malformed records are skipped with a line-specific warning so valid records
  remain available.
- Duplicate records are skipped rather than loaded twice.
- If a file cannot be read or written, FRIDAY reports the problem and keeps the
  in-memory session running where possible.

The GUI currently uses an in-memory task session; archive commands and file
persistence are console-only.

## Personality and interface

FRIDAY’s responses are concise and professional, with occasional understated
wit. Common acknowledgements include **Understood**, **Confirmed**, and **Noted**.
The GUI reinforces this identity with a deep-navy and electric-cyan palette,
compact asymmetric message cards, circular participant images, and distinct
error cards.

For the implementation design, see [`docs/ui-design.md`](ui-design.md).

## Troubleshooting

If the application does not start:

1. Confirm that Java 25 is selected.
2. Run `./gradlew clean build` from the project root.
3. If using IntelliJ IDEA, reload the Gradle project and run
   `friday.Launcher` for the GUI or `friday.Friday` for the console.

For development and contribution information, see the repository
[`README.md`](../README.md).
