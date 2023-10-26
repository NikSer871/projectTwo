package tasks;

import java.util.List;

public class Managers {
    public static FileBackedTasksManager getDefault() {
        return new FileBackedTasksManager(){

        };
    }

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
