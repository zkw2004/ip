package friday.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * A task with a start and end date-time.
 */
public class Event extends Task{
    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd uuuu HH:mm", Locale.ENGLISH);
    private LocalDateTime from;
    private LocalDateTime to;

    /**
     * Creates an incomplete event task.
     *
     * @param description Text describing the event.
     * @param from Event start date-time.
     * @param to Event end date-time.
     */
    public Event(String description, LocalDateTime from, LocalDateTime to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    /**
     * Serializes this event for storage.
     *
     * @return A type marker followed by task fields and ISO date-times.
     */
    @Override
    public String toFileString() {
        return "E | " + super.toFileString() + " | "
                + from + " | " + to;
    }

    /**
     * Returns the user-facing event representation.
     *
     * @return The event type marker, status, description, and formatted times.
     */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + from.format(DISPLAY_FORMAT)
                + " to: " + to.format(DISPLAY_FORMAT) + ")";
    }
}
