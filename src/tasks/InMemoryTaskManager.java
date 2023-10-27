package tasks;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    static HashMap<Integer, Task> dataTasks;
    static HashMap<Integer, Epic> dataEpics;
    static HashMap<Integer, Subtask> dataSubTasks;
    static HashMap<LocalDateTime, Boolean> intersections;
    static TreeSet<Task> sortedListOfTasks;

    static LocalDateTime dateTime;


    InMemoryHistoryManager inMemoryHistoryManager = new InMemoryHistoryManager();

    static int id;

    static {
        dataTasks = new HashMap<>();
        dataEpics = new HashMap<>();
        dataSubTasks = new HashMap<>();
        intersections = new HashMap<>();
        sortedListOfTasks = new TreeSet<>();
        dateTime = LocalDateTime.of(2023, Month.SEPTEMBER, 1, 0, 0);
        id = 0;
    }
    static {
        while (dateTime.isBefore(LocalDateTime.of(2024, Month.JANUARY, 1, 0, 0))) {
            intersections.put(dateTime, false);
            dateTime = dateTime.plusMinutes(15);
        }
    }


    @Override
    public String getPrioritizedTasks() {
        return String.valueOf(sortedListOfTasks);
    }

    public int checkIntersections(Task task, int condition) {
        int minutes = task.getDuration().getMinute() + task.getDuration().getHour() * 60;
        int partOfFifteen = (minutes / 15) + 1;
        LocalDateTime checkTime = LocalDateTime.of(task.getStartTime().getYear(),
                task.getStartTime().getMonth(), task.getStartTime().getDayOfMonth(), task.getStartTime().getHour(),
                task.getStartTime().getMinute() - task.getStartTime().getMinute() % 15);
        if (condition == 1) {
            for (int i = 0; i < partOfFifteen; i++) {
                if (intersections.get(checkTime)) {
                    System.out.println("Ваш временной интервал задел чужие задачи((((((((((((((((");
                    System.out.println("Создание задачи отклонено!!!!!!!!!");
                    return -1;
                } else {
                    intersections.put(checkTime, true);
                }
                checkTime = checkTime.plusMinutes(15);
            }
        } else {
            for (int i = 0; i < partOfFifteen; i++) {
                if (intersections.get(checkTime)) {
                    intersections.put(checkTime, false);
                }
                checkTime = checkTime.plusMinutes(15);
            }
        }
        return 0;

    }

    @Override
    public String giveListOfTasks(int a) {
        StringBuilder builder = new StringBuilder();
        builder.append("|  id          name    " + "\n");
        switch (a) {
            case 1:
                for (Map.Entry<Integer, Task> str : dataTasks.entrySet()) {
                    builder.append("| ");
                    builder.append(str.getKey()).append(" ").append(str.getValue().name);
                    builder.append(" |").append("\n");
                }
                break;
            case 2:
                for (Map.Entry<Integer, Epic> str : dataEpics.entrySet()) {
                    builder.append("| ");
                    builder.append(str.getKey()).append(" ").append(str.getValue().name);
                    builder.append(" |").append("\n");
                }
                break;
            case 3:
                for (Map.Entry<Integer, Subtask> str : dataSubTasks.entrySet()) {
                    builder.append("| ");
                    builder.append(str.getKey()).append(" ").append(str.getValue().name);
                    builder.append(" |").append("\n");
                }
                break;
        }
        return builder.toString();
    }

    @Override
    public void deleteTasks(int a) {
        switch (a) {
            case 1:
                for (Map.Entry<Integer, Task> task : dataTasks.entrySet()
                ) {
                    inMemoryHistoryManager.removeTask(task.getValue().id);
                }
                dataTasks.clear();
            case 2:
                for (Map.Entry<Integer, Epic> task : dataEpics.entrySet()
                ) {
                    inMemoryHistoryManager.removeTask(task.getValue().id);
                }
                dataSubTasks.clear();
                dataEpics.clear();
            case 3:
                for (Map.Entry<Integer, Subtask> task : dataSubTasks.entrySet()
                ) {
                    inMemoryHistoryManager.removeTask(task.getValue().id);
                }
                dataSubTasks.clear();
                dataEpics.clear();

        }
    }

    @Override
    public Task getTask(int a, int id) {
        switch (a) {
            case 1 -> {
                if (!dataTasks.containsKey(id)) {
                    System.out.println("Нет задачи с таким идентивикатором!!!");
                    break;
                }
                inMemoryHistoryManager.addTask(dataTasks.get(id));
                return dataTasks.get(id);

            }
            case 2 -> {
                if (!dataEpics.containsKey(id)) {
                    System.out.println("Нет задачи с таким идентивикатором!!!");
                    break;
                }
                inMemoryHistoryManager.addTask(dataEpics.get(id));
                return dataEpics.get(id);
            }
            case 3 -> {
                if (!dataSubTasks.containsKey(id)) {
                    System.out.println("Нет задачи с таким идентивикатором!!!");
                    break;
                }
                inMemoryHistoryManager.addTask(dataSubTasks.get(id));
                return dataSubTasks.get(id);
            }
        }
        return null;
    }


    @Override
    public void createTask(Task a) {
        if (checkIntersections(a, 1) == -1) {
            return;
        }
        a.status = Conditions.NEW.toString();
        a.id = id;
        a.type = NameOfTasks.TASK.toString();
        dataTasks.put(id++, a);
        sortedListOfTasks.add(a);
    }

    @Override
    public void createEpic(Epic a) {
        a.status = Conditions.NEW.toString();
        a.id = id;
        a.type = NameOfTasks.EPIC.toString();
        dataEpics.put(id++, a);
    }

    @Override
    public void createSubTask(Subtask a, int i) {
        if (checkIntersections(a, 1) == -1) {
            return;
        }
        a.status = Conditions.NEW.toString();
        a.id = id;
        a.type = NameOfTasks.SUBTASK.toString();
        a.epic = dataEpics.get(i);
        dataSubTasks.put(id++, a);
        if (a.epic.subtasks.size() == 0) {
            a.epic.setStartTime(a.getStartTime());
            a.epic.setDuration(a.getDuration());
        }
        if (a.epic.getStartTime().isAfter(a.getStartTime())) {
            a.epic.setStartTime(a.getStartTime());
        }
        dataEpics.get(i).subtasks.add(a);
        a.epic.setDuration(a.epic.getDuration().plusHours(a.getDuration().getHour()).
                plusMinutes(a.getDuration().getMinute()));
        a.epic.setEndFullTime(a.getEndTime());
        sortedListOfTasks.add(a);

    }

    @Override
    public void updateTask(Task a) {
        checkIntersections(a, 0);
        a.action = Conditions.DONE.toString();
        dataTasks.put(a.id, a);
    }

    @Override
    public void updateEpic(Epic a) {
        if (a.subtasks.size() == 0) {
            a.status = Conditions.DONE.toString();
            dataEpics.put(a.id, a);
            System.out.println(dataEpics.get(a.id).status);
        } else {
            a.status = Conditions.IN_PROGRESS.toString();
            dataEpics.put(a.id, a);
        }

    }

    @Override
    public void updateSubTask(Subtask a) {
        checkIntersections(a, 0);
        a.action = Conditions.DONE.toString();
        dataSubTasks.put(a.id, a);
        a.epic.subtasks.remove(a);
        if (a.epic.subtasks.size() == 0) {
            a.epic.status = Conditions.DONE.toString();
        } else {
            a.epic.status = Conditions.IN_PROGRESS.toString();
        }
    }

    @Override
    public void deleteTask(int a, int id) {
        switch (a) {
            case 1 -> {
                if (!dataTasks.containsKey(id)) {
                    System.out.println("Нет задачи с таким идентивикатором!!!");
                    break;
                }
                inMemoryHistoryManager.removeTask(id);
                dataTasks.remove(id);
            }
            case 2 -> {
                if (!dataEpics.containsKey(id)) {
                    System.out.println("Нет задачи с таким идентивикатором!!!");
                    break;
                }
                for (Subtask s : dataEpics.get(id).subtasks
                ) {
                    inMemoryHistoryManager.removeTask(s.id);
                    dataSubTasks.remove(s.id);
                }
                inMemoryHistoryManager.removeTask(id);
                dataEpics.get(id).subtasks.clear();
                dataEpics.remove(id);
            }
            case 3 -> {
                if (!dataSubTasks.containsKey(id)) {
                    System.out.println("Нет задачи с таким идентивикатором!!!");
                    break;
                }
                inMemoryHistoryManager.removeTask(id);
                dataSubTasks.get(id).epic.subtasks.remove(dataSubTasks.get(id));
                dataSubTasks.remove(id);
            }
        }
    }

    public void text() {
        System.out.println("Please, pick one of the options");
        System.out.println("1 - GIVE A SORTED LIST WITH ALL OF THE TASKS AND SUBTASKS");
        System.out.println("2 - DELETE ALL TYPE OF TASKS");
        System.out.println("3 - GIVE TASK WITH USING ID");
        System.out.println("4 - CREATION ONE TYPE OF TASK");
        System.out.println("5 - UPDATE");
        System.out.println("6 - DELETE TASK USING ID");
        System.out.println("7 - GIVE A LIST ALL OF THE SUBTASK CERTAIN EPIC");
        System.out.println("8 - GIVE A HISTORY");
        System.out.println("9 - DELETE TASK FROM HISTORY");
    }

    @Override
    public String giveListSubTasks(Epic epic) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < epic.subtasks.size(); i++) {
            builder.append(epic.subtasks.get(i).name).append("\n");
            //System.out.println("+++++++++++++++++++");
        }
        return builder.toString();
    }


    /*public void menu() {
        String text;
        int a;
        int id;
        Scanner v = new Scanner(System.in);
        System.out.println("HELLO!!!");
        System.out.println("If you want to stay in menu, write \"y\" ");
        text = v.nextLine();
        while (text.equals("y")) {
            System.out.println("IF YOU WANT TO LEAVE WRITE 0");
            System.out.println("---------------------------------------");
            System.out.println("---------------------------------------");
            System.out.println("---------------------------------------");
            if (v.nextLine().equals("0")) {
                System.exit(0);
            }
            text();
            switch (v.nextLine()) {
                case "1" -> {
                    getPrioritizedTasks();
                }
                case "2" -> {
                    System.out.println("Pick 1 - TASKS OR 2 - EPiCS OR 3 - SUBTASKS");
                    a = v.nextInt();
                    v.nextLine();
                    System.out.println();
                    deleteTasks(a);
                }
                case "3" -> {
                    System.out.println("Pick 1 - TASKS OR 2 - EPiCS OR 3 - SUBTASKS");
                    a = v.nextInt();
                    v.nextLine();
                    giveListOfTasks(a);
                    System.out.println("PICK ID");
                    id = v.nextInt();
                    v.nextLine();
                    getTask(a, id);
                }
                case "4" -> {
                    int hours = 0;
                    int minutes = 0;
                    int year = 0;
                    int month = 0;
                    int day = 0;
                    int durHours = 0;
                    int durMinutes = 0;
                    LocalDateTime startTime = null;
                    String name;
                    String description;
                    String action;
                    System.out.println("Pick 1 - TASKS OR 2 - EPiCS OR 3 - SUBTASKS");
                    a = v.nextInt();
                    v.nextLine();
                    System.out.println("name :");
                    name = v.nextLine();
                    System.out.println("description: ");
                    description = v.nextLine();
                    System.out.println("action: ");
                    action = v.nextLine();
                    if (a != 2) {
                        System.out.println("Give me information about task's startTime (years, month, day, hours, minutes), except Epics"); // WARMING!!! FOR EPIC
                        System.out.println("--------------------Year--------------------");
                        year = v.nextInt();
                        v.nextLine();
                        System.out.println("--------------------Month--------------------");
                        month = v.nextInt();
                        v.nextLine();
                        System.out.println("--------------------Day--------------------");
                        day = v.nextInt();
                        v.nextLine();
                        System.out.println("--------------------Hours--------------------");
                        hours = v.nextInt();
                        v.nextLine();
                        System.out.println("--------------------Minutes--------------------");
                        minutes = v.nextInt();
                        startTime = LocalDateTime.of(year, month, day, hours, minutes);
                        System.out.println("Give me information about task's startTime (years, month, day, hours, minutes)");
                        System.out.println("--------------------Hours--------------------");
                        durHours = v.nextInt();
                        v.nextLine();
                        System.out.println("--------------------Minutes--------------------");
                        durMinutes = v.nextInt();
                    }

                    switch (a) {
                        case 1 -> createTask(new Task(name, description, action, startTime, durHours, durMinutes));
                        case 2 -> createEpic(new Epic(name, description, action));
                        case 3 -> {
                            System.out.println("PICK EPIC");
                            giveListOfTasks(2);
                            id = v.nextInt();
                            v.nextLine();
                            createSubTask(new Subtask(name, description, action, startTime, durHours, durMinutes), id);
                        }
                    }
                }
                case "5" -> {
                    System.out.println("Pick 1 - TASKS OR 2 - EPiCS OR 3 - SUBTASKS");
                    a = v.nextInt();
                    v.nextLine();
                    giveListOfTasks(a);
                    System.out.println("PICK ID");
                    id = v.nextInt();
                    v.nextLine();
                    switch (a) {
                        case 1 -> updateTask(dataTasks.get(id));
                        case 2 -> updateEpic(dataEpics.get(id));
                        case 3 -> {
                            updateSubTask(dataSubTasks.get(id));
                            updateEpic(dataSubTasks.get(id).epic);
                        }
                    }
                }
                case "6" -> {
                    System.out.println("Pick 1 - TASKS OR 2 - EPiCS OR 3 - SUBTASKS");
                    a = v.nextInt();
                    v.nextLine();
                    giveListOfTasks(a);
                    System.out.println("PICK ID");
                    id = v.nextInt();
                    v.nextLine();
                    deleteTask(a, id);
                }
                case "7" -> {
                    System.out.println("PICK EPIC");
                    giveListOfTasks(2);
                    id = v.nextInt();
                    v.nextLine();
                    giveListSubTasks(dataEpics.get(id));
                }
                case "8" -> {
                    printHistory(InMemoryHistoryManager.history.getTasks());
                }
                case "9" -> {
                    printHistory(inMemoryHistoryManager.getHistory());
                    System.out.println("PICK ID FOR REMOVING CERTAIN TASK FROM HISTORY");
                    id = v.nextInt();
                    inMemoryHistoryManager.removeTask(id);
                    printHistory(InMemoryHistoryManager.history.getTasks());
                }
            }

        }
        System.exit(0);

    }*/

    public static void printHistory(List<Task> tasks) {
        for (Task task : tasks
        ) {
            System.out.println("Id == " + task.id + " name " + task.name);
        }
    }


}
