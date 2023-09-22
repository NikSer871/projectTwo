import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    static HashMap<Integer, Task> dataTasks = new HashMap<>();
    static HashMap<Integer, Epic> dataEpics = new HashMap<>();
    static HashMap<Integer, Subtask> dataSubTasks = new HashMap<>();


    InMemoryHistoryManager inMemoryHistoryManager = new InMemoryHistoryManager();
    static int idTasks = 0;
    static int idEpics = 0;
    static int idSubTasks = 0;

    static int id = 0;

    @Override
    public void giveListOfTasks(int a) {
        System.out.println("|  id          name    ");
        switch (a) {
            case 1:
                for (Map.Entry<Integer, Task> str : dataTasks.entrySet()) {
                    System.out.print("| ");
                    System.out.print(str.getKey() + " " + str.getValue().name);
                    System.out.println(" |");
                }
                break;
            case 2:
                for (Map.Entry<Integer, Epic> str : dataEpics.entrySet()) {
                    System.out.print("| ");
                    System.out.print(str.getKey() + " " + str.getValue().name);
                    System.out.println(" |");
                }
                break;
            case 3:
                for (Map.Entry<Integer, Subtask> str : dataSubTasks.entrySet()) {
                    System.out.print("| ");
                    System.out.print(str.getKey() + " " + str.getValue().name);
                    System.out.println(" |");
                }
                break;
        }
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
    public void getTask(int a, int id) {
        switch (a) {
            case 1 -> {
                if (!dataTasks.containsKey(id)) {
                    System.out.println("Нет задачи с таким идентивикатором!!!");
                    break;
                }
                System.out.println(dataTasks.get(id).name);
                inMemoryHistoryManager.addTask(dataTasks.get(id));
            }
            case 2 -> {
                if (!dataEpics.containsKey(id)) {
                    System.out.println("Нет задачи с таким идентивикатором!!!");
                    break;
                }
                System.out.println(dataEpics.get(id).name);
                inMemoryHistoryManager.addTask(dataEpics.get(id));
            }
            case 3 -> {
                if (!dataSubTasks.containsKey(id)) {
                    System.out.println("Нет задачи с таким идентивикатором!!!");
                    break;
                }
                System.out.println(dataSubTasks.get(id).name);
                inMemoryHistoryManager.addTask(dataSubTasks.get(id));
            }
        }
    }


    @Override
    public void createTask(Task a) {
        a.status = Conditions.NEW.toString();
        a.id = id;
        a.type = NameOfTasks.TASK.toString();
        dataTasks.put(id++, a);
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
        a.status = Conditions.NEW.toString();
        a.id = id;
        a.type = NameOfTasks.SUBTASK.toString();
        dataEpics.get(i).subtasks.add(a);
        a.epic = dataEpics.get(i);
        dataSubTasks.put(id++, a);
        if (a.epic.subtasks.size() == 0) {
            a.epic.setStartTime(a.getStartTime());
            a.epic.setDuration(a.getDuration());
        }
        if (a.epic.getStartTime().isAfter(a.getStartTime())) {
            a.epic.setStartTime(a.getStartTime());
        }
        a.epic.setDuration(a.epic.getDuration().plusHours(a.getDuration().getHour()).
                plusMinutes(a.getDuration().getMinute()));
        a.epic.setEndFullTime(a.getEndTime());
        System.out.println(a.epic.subtasks.size());

    }

    @Override
    public void updateTask(Task a) {
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
        System.out.println("1 - GIVE A LIST WITH ALL OF THE TASKS");
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
    public void giveListSubTasks(Epic epic) {
        for (int i = 0; i < epic.subtasks.size(); i++) {
            System.out.println(epic.subtasks.get(i).name);
            System.out.println("+++++++++++++++++++");
        }
    }


    public void menu() {
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
                    System.out.println("Pick 1 - TASKS OR 2 - EPiCS OR 3 - SUBTASKS");
                    a = v.nextInt();
                    v.nextLine();
                    giveListOfTasks(a);
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
                    int hours;
                    int minutes;
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
                    System.out.println("Give me information about task's duration (hours, minutes"); // WARMING!!! FOR EPIC
                    System.out.println("--------------------Hours--------------------");
                    hours = v.nextInt();
                    v.nextLine();
                    System.out.println("--------------------Minutes--------------------");
                    minutes = v.nextInt();
                    switch (a) {
                        case 1 -> createTask(new Task(name, description, action, hours, minutes));
                        case 2 -> createEpic(new Epic(name, description, action));
                        case 3 -> {
                            System.out.println("PICK EPIC");
                            giveListOfTasks(2);
                            id = v.nextInt();
                            v.nextLine();
                            createSubTask(new Subtask(name, description, action, hours, minutes), id);
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

    }

    public static void printHistory(List<Task> tasks) {
        for (Task task : tasks
        ) {
            System.out.println("Id == " + task.id + " name " + task.name);
        }
    }


}
