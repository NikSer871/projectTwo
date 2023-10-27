package tasks;

public interface TaskManager {

    String getPrioritizedTasks();

    String giveListOfTasks(int a);

    void deleteTasks(int a);

    Task getTask(int a, int id);

    void createTask(Task a);

    void createEpic(Epic a);

    void createSubTask(Subtask a, int id);

    void updateTask(Task a);

    void updateEpic(Epic a);

    void updateSubTask(Subtask a);

    void deleteTask(int a, int id);

    String giveListSubTasks(Epic epic);

}
