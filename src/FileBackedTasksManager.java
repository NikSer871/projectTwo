import java.io.*;
import java.time.LocalDateTime;
import java.util.*;

public class FileBackedTasksManager extends InMemoryTaskManager {

    private static final String PATH_OF_FILE = "C:\\Users\\nikul\\IdeaProjects\\project_two\\src\\manager.txt";

    private static final String BEGINNING_OF_FILE = "id,type,name,status,description,startTime,duration, epic,";

    @Override
    public void deleteTasks(int a) {
        super.deleteTasks(a);
        save();
    }

    @Override
    public void createTask(Task a) {
        super.createTask(a);
        save();
    }

    @Override
    public void createEpic(Epic a) {
        super.createEpic(a);
        save();
    }

    @Override
    public void createSubTask(Subtask a, int i) {
        super.createSubTask(a, i);
        save();
    }

    @Override
    public void updateTask(Task a) {
        super.updateTask(a);
        save();
    }

    @Override
    public void updateEpic(Epic a) {
        super.updateEpic(a);
        save();
    }

    @Override
    public void updateSubTask(Subtask a) {
        super.updateSubTask(a);
        save();
    }

    @Override
    public void deleteTask(int a, int id) {
        super.deleteTask(a, id);
        save();
    }

    @Override
    public void getTask(int a, int id) {
        super.getTask(a, id);
        save();
    }

    String getStrOfTasks() {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<Integer, Task> task : dataTasks.entrySet()
        ) {
            builder.append(toString(task.getValue())).append("\n");
        }
        for (Map.Entry<Integer, Epic> task : dataEpics.entrySet()
        ) {
            builder.append(toString(task.getValue())).append("\n");
        }
        for (Map.Entry<Integer, Subtask> task : dataSubTasks.entrySet()
        ) {
            builder.append(toString(task.getValue())).append("\n");
        }
        builder.append("\n");
        return builder.toString();
    }

    void save() {
        String history = FileBackedTasksManager.toString(inMemoryHistoryManager);
        StringBuilder builder = new StringBuilder();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(PATH_OF_FILE))) {
            builder.append(BEGINNING_OF_FILE).append("\n");
            builder.append(getStrOfTasks());
            if (history.length() != 0) {
                builder.append(history).append("\n");
            }
            writer.write(builder.toString());
        } catch (IOException e) {
            throw new MyException();
        }


    }

    String toString(Task task) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(task.id).append(",").append(task.type).append(",").append(task.name).append(",")
                .append(task.status).append(",").append(task.action).append(",").append(task.description).append(",")
                .append(task.getStartTime()).append(",").append(task.getDuration()).append(",");

        if (task.type.equals(NameOfTasks.SUBTASK.toString())) {
            stringBuilder.append(task.epic.id);
        } else {
            stringBuilder.append("null");
        }
        return stringBuilder.toString();
    }

    FileBackedTasksManager loadFromFile(File file) {
        String line;
        HashMap<Integer, Task> tasks = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            reader.readLine();
            line = reader.readLine();
            while ((line != null && line.length() > 3)) {
                String[] str = line.split(",");
                String[] duration = str[7].split(":");
                StringTokenizer tokenizer = new StringTokenizer(str[6], "-:T");
                switch (str[1]) {
                    case "TASK" -> {
                        id = Integer.parseInt(str[0]);
                        super.createTask(new Task(str[2], str[5], str[4], Integer.parseInt(duration[0]),
                                Integer.parseInt(duration[1])));
                        dataTasks.get(id - 1).status = str[3];
                        dataTasks.get(id - 1).setStartTime(LocalDateTime.of(
                                Integer.parseInt(tokenizer.nextToken()), Integer.parseInt(tokenizer.nextToken()),
                                Integer.parseInt(tokenizer.nextToken()), Integer.parseInt(tokenizer.nextToken()),
                                Integer.parseInt(tokenizer.nextToken())));
                        tasks.put(id - 1, dataTasks.get(id - 1));
                    }
                    case "EPIC" -> {
                        id = Integer.parseInt(str[0]);
                        super.createEpic(new Epic(str[2], str[5], str[4]));
                        dataEpics.get(id - 1).status = str[3];
                        dataTasks.get(id - 1).setStartTime(LocalDateTime.of(
                                Integer.parseInt(tokenizer.nextToken()), Integer.parseInt(tokenizer.nextToken()),
                                Integer.parseInt(tokenizer.nextToken()), Integer.parseInt(tokenizer.nextToken()),
                                Integer.parseInt(tokenizer.nextToken())));
                        tasks.put(id - 1, dataEpics.get(id - 1));
                    }
                    case "SUBTASK" -> {
                        id = Integer.parseInt(str[0]);
                        super.createSubTask(new Subtask(str[2], str[5], str[4], Integer.parseInt(duration[0]),
                                Integer.parseInt(duration[1])), Integer.parseInt(str[8]));
                        dataSubTasks.get(id - 1).status = str[3];
                        dataTasks.get(id - 1).setStartTime(LocalDateTime.of(
                                Integer.parseInt(tokenizer.nextToken()), Integer.parseInt(tokenizer.nextToken()),
                                Integer.parseInt(tokenizer.nextToken()), Integer.parseInt(tokenizer.nextToken()),
                                Integer.parseInt(tokenizer.nextToken())));
                        tasks.put(id - 1, dataSubTasks.get(id - 1));
                    }
                }

                line = reader.readLine();
            }
            id++;
            if ((line = reader.readLine()) != null) {
                List<Integer> listOfHistory = fromStringHistory(line);
                for (int i = listOfHistory.size() - 1; i >= 0; i--) {
                    inMemoryHistoryManager.addTask(tasks.get(listOfHistory.get(i)));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new MyException();
        }
        return new FileBackedTasksManager();
    }

/*    Task fromString(String data) {
        String[] taskData = data.split(",");

        Task task = new Task(taskData[2], taskData[5], taskData[4]);
        task.id = Integer.parseInt(taskData[0]);
        task.type = taskData[1];
        task.status = taskData[3];
        if (!taskData[taskData.length - 1].equals("null")) {
            task.epic = dataEpics.get(Integer.parseInt(taskData[8]));
        }
        return task;
    }*/

    public static String toString(HistoryManager manager) {
        List<Task> tasks = manager.getHistory();
        StringBuilder stringBuilder = new StringBuilder();
        for (Task task :
                tasks
        ) {
            stringBuilder.append(task.id).append(",");
        }
        if (stringBuilder.length() != 0) {
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        }

        return stringBuilder.toString();
    }

    public static List<Integer> fromStringHistory(String value) {
        List<Integer> listOfTasks = new ArrayList<>(); //
        String[] mas = value.split(",");
        for (String s : mas
        ) {
            listOfTasks.add(Integer.parseInt(s));
        }
        return listOfTasks;
    }

    public static void main(String[] args) {
        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager();
        fileBackedTasksManager.loadFromFile(new File(PATH_OF_FILE)).menu();

    }


}
