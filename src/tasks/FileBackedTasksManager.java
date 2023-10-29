package tasks;

import com.sun.net.httpserver.HttpServer;
import server.HttpTaskServer;

import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
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
    public Task getTask(int a, int id) {
        Task task = super.getTask(a, id);
        save();
        return task;
    }



    String getStrOfTasks() {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<Integer, Task> task : getDataTasks().entrySet()
        ) {
            builder.append(toString(task.getValue())).append("\n");
        }
        for (Map.Entry<Integer, Epic> task : getDataEpics().entrySet()
        ) {
            builder.append(toString(task.getValue())).append("\n");
        }
        for (Map.Entry<Integer, Subtask> task : getDataSubTasks().entrySet()
        ) {
            builder.append(toString(task.getValue())).append("\n");
        }
        builder.append("\n");
        return builder.toString();
    }

    void save() {
        String history = FileBackedTasksManager.toString(getInMemoryHistoryManager());
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
        stringBuilder.append(task.getId()).append(",").append(task.getType()).append(",").append(task.getName()).append(",")
                .append(task.getStatus()).append(",").append(task.getAction()).append(",").append(task.getDescription()).append(",")
                .append(task.getStartTime()).append(",").append(task.getDuration()).append(",");

        if (task.getType().equals(NameOfTasks.SUBTASK.toString())) {
            stringBuilder.append(task.getEpic().getId());
        } else {
            stringBuilder.append("null");
        }
        return stringBuilder.toString();
    }

    public FileBackedTasksManager loadFromFile(File file) {
        String line;
        HashMap<Integer, Task> tasks = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            reader.readLine();
            line = reader.readLine();
            while ((line != null && line.length() > 3)) {
                String[] str = line.split(",");
                String[] duration = str[7].split(":");
                StringTokenizer startTime = new StringTokenizer(str[6], "-:T");
                switch (str[1]) {
                    case "TASK" -> {
                        id = Integer.parseInt(str[0]);
                        LocalDateTime startT = LocalDateTime.of(
                                Integer.parseInt(startTime.nextToken()), Integer.parseInt(startTime.nextToken()),
                                Integer.parseInt(startTime.nextToken()), Integer.parseInt(startTime.nextToken()),
                                Integer.parseInt(startTime.nextToken()));
                        super.createTask(new Task(str[2], str[5], str[4], startT, Integer.parseInt(duration[0]),
                                Integer.parseInt(duration[1])));
                        getDataTasks().get(id - 1).setStatus(str[3]);
                        tasks.put(id - 1, getDataTasks().get(id - 1));
                    }
                    case "EPIC" -> {
                        id = Integer.parseInt(str[0]);
                        super.createEpic(new Epic(str[2], str[5], str[4]));
                        getDataEpics().get(id - 1).setStatus(str[3]);
                        getDataTasks().get(id - 1).setStartTime(LocalDateTime.of(
                                Integer.parseInt(startTime.nextToken()), Integer.parseInt(startTime.nextToken()),
                                Integer.parseInt(startTime.nextToken()), Integer.parseInt(startTime.nextToken()),
                                Integer.parseInt(startTime.nextToken())));
                        tasks.put(id - 1, getDataEpics().get(id - 1));
                    }
                    case "SUBTASK" -> {
                        id = Integer.parseInt(str[0]);
                        LocalDateTime startT = LocalDateTime.of(
                                Integer.parseInt(startTime.nextToken()), Integer.parseInt(startTime.nextToken()),
                                Integer.parseInt(startTime.nextToken()), Integer.parseInt(startTime.nextToken()),
                                Integer.parseInt(startTime.nextToken()));
                        super.createSubTask(new Subtask(str[2], str[5], str[4], startT, Integer.parseInt(duration[0]),
                                Integer.parseInt(duration[1])), Integer.parseInt(str[8]));
                        getDataSubTasks().get(id - 1).setStatus(str[3]);
                        tasks.put(id - 1, getDataSubTasks().get(id - 1));
                    }
                }

                line = reader.readLine();
            }
            if ((line = reader.readLine()) != null) {
                List<Integer> listOfHistory = fromStringHistory(line);
                for (int i = listOfHistory.size() - 1; i >= 0; i--) {
                    getInMemoryHistoryManager().addTask(tasks.get(listOfHistory.get(i)));
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
            stringBuilder.append(task.getId()).append(",");
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
        HttpTaskServer.getManager().loadFromFile(new File(PATH_OF_FILE));
        try {
            HttpServer server = HttpTaskServer.createServer();
            server.start();

            HttpClient client = HttpClient.newHttpClient();
            URI url = URI.create("http://localhost:8080/tasks/task/");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(url)
                    .GET()
                    .build();


            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println(response.body()); // ¬ã¬Õ¬Ö¬Ý¬Ñ¬ä¬î JSON ¬à¬ä¬Ó¬Ö¬ä


        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

    }


}
