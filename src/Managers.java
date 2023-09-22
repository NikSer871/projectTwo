import java.util.List;

public class Managers {
    /*public TaskManager getDefault() {
        return new TaskManager();
    }*/

    static InMemoryHistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager() {
            
            @Override
            public void addTask(Task task) {

            }

            @Override
            public List<Task> getHistory() {
                return null;
            }
        };
    }
}
