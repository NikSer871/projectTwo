package tasks;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class EpicTest {
    private final Epic epic = new Epic("s", "s", "s");
    private final InMemoryTaskManager manager = new InMemoryTaskManager();

    @Test
    void statusEpicChange() {
        epic.setStatus(Conditions.NEW.toString());

        manager.createEpic(epic);
        LocalDateTime start1 = LocalDateTime.of(2023, 11, 1, 3, 15);
        LocalDateTime start2 = LocalDateTime.of(2023, 12, 1, 3, 15);
        Subtask subtask1 = new Subtask("Nsd", "sdsd", "SdSD", start1, 5, 3);
        Subtask subtask2 = new Subtask("Nsd", "sdsd", "SdSD", start2, 5, 3);


        assertEquals(0, epic.getSubtasks().size(), "Epic содержит невидимые подзадачи!");
        manager.createSubTask(subtask1, 0);
        manager.createSubTask(subtask2, 0);

        assertEquals("NEW", epic.getStatus(), "СТАТУС НЕ NEW!!!");

        manager.updateSubTask(subtask1);
        assertEquals("IN_PROGRESS", epic.getStatus(), "СТАТУС НЕ IN PROGRESS!!!");

        manager.updateSubTask(subtask2);
        assertEquals("DONE", epic.getStatus(), "СТАТУС НЕ DONE!!!");


    }
}
