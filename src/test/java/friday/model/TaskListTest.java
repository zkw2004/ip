package friday.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Tests the public operations and boundary behavior of {@link TaskList}.
 */
class TaskListTest {
    @Test
    void defaultConstructor_createsEmptyTaskList() {
        TaskList tasks = new TaskList();

        assertEquals(0, tasks.size());
        assertEquals(List.of(), tasks.asList());
    }

    @Test
    void listConstructor_copiesSuppliedTasks() {
        Task first = new ToDo("first");
        List<Task> source = new ArrayList<>(List.of(first));
        TaskList tasks = new TaskList(source);

        source.add(new ToDo("outside task"));

        assertEquals(1, tasks.size());
        assertSame(first, tasks.get(0));
    }

    @Test
    void add_tasksAppendedInInsertionOrder_andSizeUpdated() {
        Task first = new ToDo("first");
        Task second = new ToDo("second");
        TaskList tasks = new TaskList();

        tasks.add(first);
        tasks.add(second);

        assertEquals(2, tasks.size());
        assertSame(first, tasks.get(0));
        assertSame(second, tasks.get(1));
    }

    @Test
    void get_validIndex_returnsTaskAtThatIndex() {
        Task expected = new ToDo("expected");
        TaskList tasks = new TaskList(List.of(new ToDo("first"), expected));

        assertSame(expected, tasks.get(1));
    }

    @Test
    void get_negativeIndex_throwsIndexOutOfBoundsException() {
        TaskList tasks = new TaskList(List.of(new ToDo("task")));

        assertThrows(IndexOutOfBoundsException.class, () -> tasks.get(-1));
    }

    @Test
    void getIndexAtSize_throwsIndexOutOfBoundsException() {
        TaskList tasks = new TaskList(List.of(new ToDo("task")));

        assertThrows(IndexOutOfBoundsException.class, () -> tasks.get(tasks.size()));
    }

    @Test
    void remove_validIndex_returnsRemovedTaskAndPreservesOrder() {
        Task first = new ToDo("first");
        Task removed = new ToDo("removed");
        Task last = new ToDo("last");
        TaskList tasks = new TaskList(List.of(first, removed, last));

        Task result = tasks.remove(1);

        assertSame(removed, result);
        assertEquals(2, tasks.size());
        assertSame(first, tasks.get(0));
        assertSame(last, tasks.get(1));
    }

    @Test
    void remove_negativeIndex_throwsIndexOutOfBoundsException() {
        TaskList tasks = new TaskList(List.of(new ToDo("task")));

        assertThrows(IndexOutOfBoundsException.class, () -> tasks.remove(-1));
        assertEquals(1, tasks.size());
    }

    @Test
    void removeIndexAtSize_throwsIndexOutOfBoundsException() {
        TaskList tasks = new TaskList(List.of(new ToDo("task")));

        assertThrows(IndexOutOfBoundsException.class, () -> tasks.remove(tasks.size()));
        assertEquals(1, tasks.size());
    }

    @Test
    void remove_emptyList_throwsIndexOutOfBoundsException() {
        TaskList tasks = new TaskList();

        assertThrows(IndexOutOfBoundsException.class, () -> tasks.remove(0));
        assertEquals(0, tasks.size());
    }

    @Test
    void size_afterAddAndRemove_reportsCurrentNumberOfTasks() {
        TaskList tasks = new TaskList();

        tasks.add(new ToDo("first"));
        tasks.add(new ToDo("second"));
        tasks.remove(0);

        assertEquals(1, tasks.size());
    }

    @Test
    void asList_returnsReadOnlyView_thatReflectsTaskListChanges() {
        Task first = new ToDo("first");
        Task second = new ToDo("second");
        TaskList tasks = new TaskList(List.of(first));
        List<Task> view = tasks.asList();

        tasks.add(second);

        assertEquals(List.of(first, second), view);
        assertThrows(UnsupportedOperationException.class, () -> view.add(new ToDo("blocked")));
    }
}
