import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;


public class HistoryManagerTest {
    private final Task task = new Task();
    private final InMemoryHistoryManager historyManager = new InMemoryHistoryManager();

    @Test
    void add() {
        historyManager.addTask(task);
        final List<Task> list = historyManager.getHistory();

        assertNotNull(list, "Задачи не добавляются!");
        assertEquals(task, list.get(0), "Задачи не совпадают!");
    }



}
