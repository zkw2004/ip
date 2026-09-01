package friday.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Tests the boundary behavior of {@link TaskList#remove(int)}.
 */
class TaskListTest {
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
}
