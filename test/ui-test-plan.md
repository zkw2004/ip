# UI Test Plan

Record chatbot console UI tests here for the `test-ui` skill.

## Test Case: greet-and-exit
Aim:
Verify that the chatbot starts, shows the greeting, and exits cleanly when the user enters `bye`.

Inputs:
```text
bye
```

Expected Output:
```text
____________________________________________________________
 _____    _     _
|  ___| _(_) __| | __ _ _   _
| |_ | '__| |/ _` |/ _` | | | |
|  _|| |  | | (_| | (_| | |_| |
|_|  |_|  |_|\__,_|\__,_|\__, |
                         |___/

Hello! I'm Friday.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## Test Case: reject-empty-todo
Aim:
Verify that the chatbot shows a friendly error when the user enters a todo without a description.

Inputs:
```text
todo
bye
```

Expected Output:
```text
____________________________________________________________
 _____    _     _
|  ___| _(_) __| | __ _ _   _
| |_ | '__| |/ _` |/ _` | | | |
|  _|| |  | | (_| | (_| | |_| |
|_|  |_|  |_|\__,_|\__,_|\__, |
                         |___/

Hello! I'm Friday.
What can I do for you?
____________________________________________________________
____________________________________________________________
 OOPS!!! The description of a todo cannot be empty.
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## Test Case: reject-unknown-command
Aim:
Verify that the chatbot rejects unsupported commands without crashing.

Inputs:
```text
blah
bye
```

Expected Output:
```text
____________________________________________________________
 _____    _     _
|  ___| _(_) __| | __ _ _   _
| |_ | '__| |/ _` |/ _` | | | |
|  _|| |  | | (_| | (_| | |_| |
|_|  |_|  |_|\__,_|\__,_|\__, |
                         |___/

Hello! I'm Friday.
What can I do for you?
____________________________________________________________
____________________________________________________________
 OOPS!!! I'm sorry, but I don't know what that means :-(
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## Test Case: reject-invalid-deadline-format
Aim:
Verify that the chatbot groups malformed deadline commands into one format error.

Inputs:
```text
deadline return book
bye
```

Expected Output:
```text
____________________________________________________________
 _____    _     _
|  ___| _(_) __| | __ _ _   _
| |_ | '__| |/ _` |/ _` | | | |
|  _|| |  | | (_| | (_| | |_| |
|_|  |_|  |_|\__,_|\__,_|\__, |
                         |___/

Hello! I'm Friday.
What can I do for you?
____________________________________________________________
____________________________________________________________
 OOPS!!! Use this format: deadline <description> /by <date>
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## Test Case: add-mark-unmark-task
Aim:
Verify that the chatbot can add a task and update its completion status.

Inputs:
```text
todo read book
mark 1
unmark 1
bye
```

Expected Output:
```text
____________________________________________________________
 _____    _     _
|  ___| _(_) __| | __ _ _   _
| |_ | '__| |/ _` |/ _` | | | |
|  _|| |  | | (_| | (_| | |_| |
|_|  |_|  |_|\__,_|\__,_|\__, |
                         |___/

Hello! I'm Friday.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read book
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
 [T][X] read book
____________________________________________________________
____________________________________________________________
 OK, I've marked this task as not done yet:
 [T][ ] read book
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## Test Case: invalid-mark-does-not-change-state
Aim:
Verify that an invalid mark command does not corrupt the existing task list and that later valid commands still work.

Inputs:
```text
todo read book
mark two
list
mark 1
list
bye
```

Expected Output:
```text
____________________________________________________________
 _____    _     _
|  ___| _(_) __| | __ _ _   _
| |_ | '__| |/ _` |/ _` | | | |
|  _|| |  | | (_| | (_| | |_| |
|_|  |_|  |_|\__,_|\__,_|\__, |
                         |___/

Hello! I'm Friday.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read book
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 OOPS!!! Please enter a valid task number.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1. [T][ ] read book
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
 [T][X] read book
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1. [T][X] read book
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## Test Case: invalid-add-does-not-change-state
Aim:
Verify that malformed task-creation commands do not add tasks and do not affect later valid additions.

Inputs:
```text
todo revise notes
event /from Monday 2pm /to Monday 3pm
list
deadline submit report /by Friday
list
bye
```

Expected Output:
```text
____________________________________________________________
 _____    _     _
|  ___| _(_) __| | __ _ _   _
| |_ | '__| |/ _` |/ _` | | | |
|  _|| |  | | (_| | (_| | |_| |
|_|  |_|  |_|\__,_|\__,_|\__, |
                         |___/

Hello! I'm Friday.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] revise notes
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 OOPS!!! Use this format: event <description> /from <start> /to <end>
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1. [T][ ] revise notes
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [D][ ] submit report (by: Friday)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1. [T][ ] revise notes
 2. [D][ ] submit report (by: Friday)
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## Test Case: reject-command-prefix-collision
Aim:
Verify that inputs which only share a command prefix are rejected instead of being parsed as valid commands.

Inputs:
```text
todo read chapter
todoagain
deadlinereport /by Sunday
eventmeeting /from Mon 1pm /to 2pm
list
bye
```

Expected Output:
```text
____________________________________________________________
 _____    _     _
|  ___| _(_) __| | __ _ _   _
| |_ | '__| |/ _` |/ _` | | | |
|  _|| |  | | (_| | (_| | |_| |
|_|  |_|  |_|\__,_|\__,_|\__, |
                         |___/

Hello! I'm Friday.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read chapter
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 OOPS!!! I'm sorry, but I don't know what that means :-(
____________________________________________________________
____________________________________________________________
 OOPS!!! I'm sorry, but I don't know what that means :-(
____________________________________________________________
____________________________________________________________
 OOPS!!! I'm sorry, but I don't know what that means :-(
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1. [T][ ] read chapter
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## Test Case: delete-task-and-check-state
Aim:
Verify that deleting a valid task removes only that task and that the remaining tasks keep the correct order.

Inputs:
```text
todo read book
deadline return book /by June 6th
event project meeting /from Aug 6th 2pm /to 4pm
list
delete 2
list
bye
```

Expected Output:
```text
____________________________________________________________
 _____    _     _
|  ___| _(_) __| | __ _ _   _
| |_ | '__| |/ _` |/ _` | | | |
|  _|| |  | | (_| | (_| | |_| |
|_|  |_|  |_|\__,_|\__,_|\__, |
                         |___/

Hello! I'm Friday.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] read book
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [D][ ] return book (by: June 6th)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [E][ ] project meeting (from: Aug 6th 2pmto: 4pm)
 Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1. [T][ ] read book
 2. [D][ ] return book (by: June 6th)
 3. [E][ ] project meeting (from: Aug 6th 2pmto: 4pm)
____________________________________________________________
____________________________________________________________
 Noted. I've removed this task:
   [D][ ] return book (by: June 6th)
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1. [T][ ] read book
 2. [E][ ] project meeting (from: Aug 6th 2pmto: 4pm)
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## Test Case: invalid-delete-does-not-change-state
Aim:
Verify that an invalid delete command leaves the task list unchanged and that a later valid delete still works.

Inputs:
```text
todo borrow book
todo return notes
delete two
list
delete 1
list
bye
```

Expected Output:
```text
____________________________________________________________
 _____    _     _
|  ___| _(_) __| | __ _ _   _
| |_ | '__| |/ _` |/ _` | | | |
|  _|| |  | | (_| | (_| | |_| |
|_|  |_|  |_|\__,_|\__,_|\__, |
                         |___/

Hello! I'm Friday.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] borrow book
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] return notes
 Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
 OOPS!!! Please enter a valid task number.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1. [T][ ] borrow book
 2. [T][ ] return notes
____________________________________________________________
____________________________________________________________
 Noted. I've removed this task:
   [T][ ] borrow book
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1. [T][ ] return notes
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## Test Case: out-of-range-delete-on-empty-and-nonempty-list
Aim:
Verify that out-of-range delete commands fail safely both before and after valid additions.

Inputs:
```text
delete 1
todo plan trip
delete 2
list
bye
```

Expected Output:
```text
____________________________________________________________
 _____    _     _
|  ___| _(_) __| | __ _ _   _
| |_ | '__| |/ _` |/ _` | | | |
|  _|| |  | | (_| | (_| | |_| |
|_|  |_|  |_|\__,_|\__,_|\__, |
                         |___/

Hello! I'm Friday.
What can I do for you?
____________________________________________________________
____________________________________________________________
 OOPS!!! Please enter a valid task number.
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] plan trip
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 OOPS!!! Please enter a valid task number.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1. [T][ ] plan trip
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```

## Test Case: invalid-unmark-does-not-change-state
Aim:
Verify that an invalid unmark command does not affect an already completed task.

Inputs:
```text
todo submit quiz
mark 1
unmark 5
list
unmark 1
list
bye
```

Expected Output:
```text
____________________________________________________________
 _____    _     _
|  ___| _(_) __| | __ _ _   _
| |_ | '__| |/ _` |/ _` | | | |
|  _|| |  | | (_| | (_| | |_| |
|_|  |_|  |_|\__,_|\__,_|\__, |
                         |___/

Hello! I'm Friday.
What can I do for you?
____________________________________________________________
____________________________________________________________
 Got it. I've added this task:
   [T][ ] submit quiz
 Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
 Nice! I've marked this task as done:
 [T][X] submit quiz
____________________________________________________________
____________________________________________________________
 OOPS!!! Please enter a valid task number.
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1. [T][X] submit quiz
____________________________________________________________
____________________________________________________________
 OK, I've marked this task as not done yet:
 [T][ ] submit quiz
____________________________________________________________
____________________________________________________________
 Here are the tasks in your list:
 1. [T][ ] submit quiz
____________________________________________________________
____________________________________________________________
 Bye. Hope to see you again soon!
____________________________________________________________
```
