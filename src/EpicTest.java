import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class EpicTest {
    private final Epic epic = new Epic("s", "s", "s");
    private final InMemoryTaskManager manager = new InMemoryTaskManager();

    @Test
    void statusEpicChange() {
        epic.status = Conditions.NEW.toString();

        manager.createEpic(epic);
        LocalDateTime start1 = LocalDateTime.of(2023, 11, 1, 3, 15);
        LocalDateTime start2 = LocalDateTime.of(2023, 12, 1, 3, 15);
        Subtask subtask1 = new Subtask("Nsd", "sdsd", "SdSD", start1, 5, 3);
        Subtask subtask2 = new Subtask("Nsd", "sdsd", "SdSD", start2, 5, 3);


        assertEquals(0, epic.subtasks.size(), "Epic содержит невидимые подзадачи!");
        manager.createSubTask(subtask1, 0);
        manager.createSubTask(subtask2, 0);

        assertEquals("NEW", epic.status, "СТАТУС НЕ NEW!!!");

        manager.updateSubTask(subtask1);
        assertEquals("IN_PROGRESS", epic.status, "СТАТУС НЕ IN PROGRESS!!!");

        manager.updateSubTask(subtask2);
        assertEquals("DONE", epic.status, "СТАТУС НЕ DONE!!!");


    }
}
