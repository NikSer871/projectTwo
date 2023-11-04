package server;


import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import tasks.*;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private static final FileBackedTasksManager manager = Managers.getDefault();

    public static HttpServer createServer() throws IOException {

        HttpServer httpServer = HttpServer.create();
        httpServer.bind(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks", new TaskHandler());
        return httpServer;
    }

    public static void main(String[] args) {


    }

    public static FileBackedTasksManager getManager() {
        return manager;
    }

    static class TaskHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            System.out.println("Началась обработка!!!!"); // --------
            String method = exchange.getRequestMethod();
            InputStream inputStream = exchange.getRequestBody();
            //String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
            URI uri = exchange.getRequestURI();
            String path = uri.getPath();
            String[] splitStrings = path.substring(1).split("/");
            System.out.println(Arrays.toString(splitStrings));
            responseOnRequest(splitStrings, method, exchange);

        }

        private void responseOnRequest(String[] splitStrings, String method, HttpExchange exchange) throws IOException {
            if (isGetTasks(splitStrings, method)) {
                exchange.sendResponseHeaders(200, 0);
                getTasks(exchange);
                getEpics(exchange);
                getSubtasks(exchange);
                return;
            } else if (isGetHistory(splitStrings, method)) {
                exchange.sendResponseHeaders(200, 0);
                getHistory(exchange);
                return;
            }
            if (isGetTaskById(splitStrings, method) && isIdRequest(exchange)) {
                exchange.sendResponseHeaders(200, 0);
                getAnyTaskById(exchange, getId(exchange), 1);
                return;
            } else if (isGetEpicById(splitStrings, method) && isIdRequest(exchange)) {
                getAnyTaskById(exchange, getId(exchange), 2);
                exchange.sendResponseHeaders(200, 0);
                return;
            } else if (isGetSubtaskById(splitStrings, method) && isIdRequest(exchange)) {
                exchange.sendResponseHeaders(200, 0);
                getAnyTaskById(exchange, getId(exchange), 3);
                return;
            }

            if (isCreateTask(splitStrings, method)) {
                exchange.sendResponseHeaders(200, 0);
                createTask(exchange);
                return;
            } else if (isCreateEpic(splitStrings, method)) {
                exchange.sendResponseHeaders(200, 0);
                createEpic(exchange);
                return;
            } else if (isCreateSubtask(splitStrings, method)) {
                System.out.println("skss");
                exchange.sendResponseHeaders(200, 0);
                createSubtask(exchange);
                return;
            }

            if (isDeleteTaskById(splitStrings, method) && isIdRequest(exchange)) {
                exchange.sendResponseHeaders(200, 0);
                deleteAnyTaskByID(exchange, getId(exchange), 1);
                return;
            } else if (isDeleteEpicById(splitStrings, method) && isIdRequest(exchange)) {
                exchange.sendResponseHeaders(200, 0);
                deleteAnyTaskByID(exchange, getId(exchange), 2);
                return;
            } else if (isDeleteSubtaskById(splitStrings, method) && isIdRequest(exchange)) {
                exchange.sendResponseHeaders(200, 0);
                deleteAnyTaskByID(exchange, getId(exchange), 3);
                return;
            }



        }

        boolean isNotExistenceOfTaskInHashMap(int id, int condition) {
            switch (condition) {
                case 1 -> {
                    if (InMemoryTaskManager.getDataTasks().containsKey(id)) {
                        return false;
                    }
                    return true;
                }
                case 2 -> {
                    if (InMemoryTaskManager.getDataEpics().containsKey(id)) {
                        return false;
                    }
                    return true;
                }
                case 3 -> {
                    if (InMemoryTaskManager.getDataSubTasks().containsKey(id)) {
                        return false;
                    }
                    return true;
                }
            }
            return true;
        }

        private void deleteAnyTaskByID(HttpExchange exchange, int id, int condition) {
            String line;
            if (isNotExistenceOfTaskInHashMap(id, condition)) {
                line = String.format("Such task with id = %d doesn't exist", id);
            } else {
                System.out.println("sdsdsd");
                manager.deleteTask(condition, id);
                System.out.println("sdsdsd");
                if (isNotExistenceOfTaskInHashMap(id, condition)) {
                    line = "Task deleted!";
                } else {
                    line = "ERROR!!!";
                }
            }

            try (OutputStream os = exchange.getResponseBody()) {
                os.write(line.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private String getJSONString(HttpExchange exchange) {
            String line;
            StringBuilder builder = new StringBuilder();
            try (InputStream stream = exchange.getRequestBody();InputStreamReader reader = new InputStreamReader(stream)
                 ;BufferedReader bufferedReader = new BufferedReader(reader)) {
                while ((line = bufferedReader.readLine()) != null) {
                    builder.append(line);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return builder.toString();
        }

        private void createTask(HttpExchange exchange) {
            String line;
            JsonElement element = JsonParser.parseString(getJSONString(exchange));
            JsonObject jsonObject = element.getAsJsonObject();
            int checkpoint = createStandardTask(jsonObject, "TASK");
            if (checkpoint == -1) {
                line = "This intersection isn't empty!!!";
            } else {
                line = "Task created!";
            }

            try (OutputStream os = exchange.getResponseBody()) {
                os.write(line.getBytes());
            } catch (
                    IOException e) {
                e.printStackTrace();
            }
        }


        private void createSubtask(HttpExchange exchange) {
            String line;
            JsonElement element = JsonParser.parseString(getJSONString(exchange));
            JsonObject jsonObject = element.getAsJsonObject();
            int checkpoint = createStandardTask(jsonObject, "SUBTASK");
            if (checkpoint == -1) {
                line = "This intersection isn't empty!!!";
            } else {
                line = "Subtask created!";
            }

            try (OutputStream os = exchange.getResponseBody()) {
                os.write(line.getBytes());
            } catch (
                    IOException e) {
                e.printStackTrace();
            }

        }

        private void createEpic(HttpExchange exchange) {
            System.out.println("EPIC!!!!");
            String line = "Epic created!!!";
            JsonElement element = JsonParser.parseString(getJSONString(exchange));
            JsonObject jsonObject = element.getAsJsonObject();
            createStandardEpic(jsonObject);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(line.getBytes());
            } catch (
                    IOException e) {
                e.printStackTrace();
            }
        }


        private void createStandardEpic(JsonObject object) {
            String name, description, action;
            name = object.get("name").getAsString();
            description = object.get("description").getAsString();
            action = object.get("action").getAsString();
            Epic epic = new Epic(name, description, action);
            manager.createEpic(epic);
        }

        private int createStandardTask(JsonObject object, String a) {
            int hours, minutes, year, month, day, durHours, durMinutes;
            String name, description, action;
            LocalDateTime startTime;
            name = object.get("name").getAsString();
            description = object.get("description").getAsString();
            action = object.get("action").getAsString();
            hours = object.get("hours").getAsInt();
            minutes = object.get("minutes").getAsInt();
            year = object.get("years").getAsInt();
            month = object.get("month").getAsInt();
            day = object.get("day").getAsInt();
            durHours = object.get("durHours").getAsInt();
            durMinutes = object.get("durMinutes").getAsInt();
            System.out.println(durHours);
            startTime = LocalDateTime.of(year, month, day, hours, minutes);
            if (a.equals("TASK")) {
                Task task = new Task(name, description, action, startTime, durHours, durMinutes);
                System.out.println(task.getStartTime());
                manager.createTask(task);

                if (InMemoryTaskManager.getDataTasks().containsValue(task)) {
                    return 1;
                } else {
                    return -1;
                }
            } else {
                Subtask task = new Subtask(name, description, action, startTime, durHours, durMinutes);
                manager.createSubTask(task, object.get("epicId").getAsInt());
                if (InMemoryTaskManager.getDataSubTasks().containsValue(task)) {
                    return 1;
                } else {
                    return -1;
                }
            }

        }


        private void getTasks(HttpExchange exchange) {
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(manager.giveListOfTasks(1).getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void getEpics(HttpExchange exchange) {
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(manager.giveListOfTasks(2).getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void getSubtasks(HttpExchange exchange) {
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(manager.giveListOfTasks(3).getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void getHistory(HttpExchange exchange) {
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(FileBackedTasksManager.toString(manager.getInMemoryHistoryManager()).getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void getAnyTaskById(HttpExchange exchange, int id, int condition) {
            Task task = manager.getTask(condition, id);
            String line;
            if (task == null) {
                line = "Неправильно введён ID!!!";
            } else {
                /*Gson gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new JsonDeserializer<LocalDateTime>() {
                    @Override
                    public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

                        return LocalDateTime.parse(json.getAsString(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")); }

                }).create();*/
                line = task.toString();
            }
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(line.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

       /* private void getEpicById(HttpExchange exchange, int id) {
            Task epic = manager.getTask(2, id);
            String line;
            if (epic == null) {
                line = "Неправильно введён ID!!!";
            } else {
                line = epic.toString();
            }

            try (OutputStream os = exchange.getResponseBody()) {
                os.write(line.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void getSubtaskById(HttpExchange exchange, int id) {
            Task subtask = manager.getTask(3, id);
            String line;
            if (subtask == null) {
                line = "Неправильно введён ID!!!";
            } else {
                line = subtask.toString();
            }

            try (OutputStream os = exchange.getResponseBody()) {
                os.write(line.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/

        private boolean isCreateTask(String[] splitStrings, String method) {
            return splitStrings.length == 2 && method.equals("POST") &&
                    (splitStrings[1].equals("task"));
        }

        private boolean isCreateSubtask(String[] splitStrings, String method) {
            return splitStrings.length == 2 && method.equals("POST") &&
                    (splitStrings[1].equals("subtask"));
        }

        private boolean isCreateEpic(String[] splitStrings, String method) {
            return splitStrings.length == 2 && method.equals("POST") &&
                    (splitStrings[1].equals("epic"));
        }

        private boolean isGetTaskById(String[] splitStrings, String method) {
            return splitStrings.length == 2 && method.equals("GET")
                    && (splitStrings[1].equals("task")); // check!!!
        }

        private boolean isGetEpicById(String[] splitStrings, String method) {
            return splitStrings.length == 2 && method.equals("GET")
                    && (splitStrings[1].equals("epic"));
        }


        private boolean isDeleteSubtaskById(String[] splitStrings, String method) {
            return splitStrings.length == 2 && method.equals("DELETE")
                    && (splitStrings[1].equals("subtask"));
        }
        private boolean isDeleteTaskById(String[] splitStrings, String method) {
            return splitStrings.length == 2 && method.equals("DELETE")
                    && (splitStrings[1].equals("task")); // check!!!
        }

        private boolean isDeleteEpicById(String[] splitStrings, String method) {
            return splitStrings.length == 2 && method.equals("DELETE")
                    && (splitStrings[1].equals("epic"));
        }


        private boolean isGetSubtaskById(String[] splitStrings, String method) {
            return splitStrings.length == 2 && method.equals("GET")
                    && (splitStrings[1].equals("subtask"));
        }

        private boolean isIdRequest(HttpExchange exchange) {
            return exchange.getRequestURI().getQuery().startsWith("id=");
        }


        private boolean isGetTasks(String[] splitStrings, String method) {
            return splitStrings.length == 1 && method.equals("GET") && splitStrings[0].equals("tasks");
        }

        private boolean isGetHistory(String[] splitStrings, String method) {
            return splitStrings.length == 2 && method.equals("GET") && splitStrings[1].equals("history");
        }

        private int getId(HttpExchange exchange) {
            return Integer.parseInt(exchange.getRequestURI().getQuery().substring(3));
        }


    }
}
