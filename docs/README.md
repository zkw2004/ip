# Friday User Guide

// Update the title above to match the actual product name

// Product screenshot goes here

// Product intro goes here

## Archiving tasks

Move a task out of the active list while preserving it in `data/archive.txt`.

Archive one task using its one-based active-list number:

`archive 2`

Archive every active task:

`archive all`

Archived tasks are stored in archive order. The active-task file remains at
`data/friday.txt`, while archived tasks are stored in `data/archive.txt`.

## Viewing archived tasks

Use `list-archive` to display archived tasks. Archived task numbers are
one-based and are independent of active-task numbers.

Example: `list-archive`

## Restoring archived tasks

Restore one archived task to the end of the active list:

`unarchive 2`

Restore all archived tasks, in archive order, to the end of the active list:

`unarchive all`

If Friday cannot update either task file, it leaves the in-memory lists
unchanged and reports the error. A failed two-file update can leave a duplicate
record on disk; Friday favors retaining tasks over risking task loss.

## Adding deadlines

// Describe the action and its outcome.

// Give examples of usage

Example: `keyword (optional arguments)`

// A description of the expected outcome goes here

```
expected output
```

## Feature ABC

// Feature details


## Feature XYZ

// Feature details
