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
