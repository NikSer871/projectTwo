package tasks;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;


class InMemoryTaskManagerTest {
    private final InMemoryTaskManager manager = new InMemoryTaskManager();
    private final InMemoryHistoryManager historyManager = new InMemoryHistoryManager();

    @Test
    void createTask() {
        Task task = new Task("One", "Nothing", "RUN!",
                LocalDateTime.of(2023, 9, 10, 12, 15), 10, 2);
        manager.createTask(task);
        final int id = InMemoryTaskManager.id;

        final Task savedTask = InMemoryTaskManager.getDataTasks().get(id - 1);

        assertNotNull(savedTask, "Задача не найдена!");
        assertEquals(task, savedTask, "Задачи не совпадают!");

    }


}