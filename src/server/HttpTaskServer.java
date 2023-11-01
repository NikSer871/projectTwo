package server;


import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import tasks.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.sql.SQLOutput;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.logging.Handler;

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
            System.out.println(Arrays.toString(splitStrings));
            if (isGetTaskById(splitStrings, method) && isIdRequest(exchange)) {
                exchange.sendResponseHeaders(200, 0);
                getTaskById(exchange, getId(exchange) );
                return;
            } else if (isGetEpicById(splitStrings, method) && isIdRequest(exchange)) {
                exchange.sendResponseHeaders(200, 0);
                getEpicById(exchange, getId(exchange));
                return;
            } else if (isGetSubtaskById(splitStrings, method) && isIdRequest(exchange)) {
                exchange.sendResponseHeaders(200, 0);
                getSubtaskById(exchange, getId(exchange));
                return;
            }

            if (isCreateTask(splitStrings, method)) {
                createTask(exchange);
                return;
            } else if (isCreateEpic(splitStrings, method)) {
                createEpic(exchange);
                return;
            } else if (isCreateSubtask(splitStrings, method)) {
                createSubtask(exchange);
                return;
            }


        }

        private void createTask(HttpExchange exchange) {
            String line;
            JsonElement element = JsonParser.parseString(exchange.getRequestBody().toString());
            JsonObject jsonObject = element.getAsJsonObject();
            int checkpoint = createStandardTask(jsonObject, "TASK");
            if (checkpoint == -1) {
                line = "Данный промежуток времени занят!!!";
            } else {
                line = "Задача создана!";
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
            JsonElement element = JsonParser.parseString(exchange.getRequestBody().toString());
            JsonObject jsonObject = element.getAsJsonObject();
            int checkpoint = createStandardTask(jsonObject, "SUBTASK");
            if (checkpoint == -1) {
                line = "Данный промежуток времени занят!!!";
            } else {
                line = "Задача создана!";
            }

            try (OutputStream os = exchange.getResponseBody()) {
                os.write(line.getBytes());
            } catch (
                    IOException e) {
                e.printStackTrace();
            }

        }

        private void createEpic(HttpExchange exchange) {
            String line = "Epic создан!!!";
            JsonElement element = JsonParser.parseString(exchange.getRequestBody().toString());
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
            year = object.get("year").getAsInt();
            month = object.get("month").getAsInt();
            day = object.get("day").getAsInt();
            durHours = object.get("durHours").getAsInt();
            durMinutes = object.get("durMinutes").getAsInt();
            startTime = LocalDateTime.of(year, month, day, hours, minutes);
            if (a.equals("TASK")) {
                Task task = new Task(name, description, action, startTime, durHours, durMinutes);
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

        private void getTaskById(HttpExchange exchange, int id) {
            Task task = manager.getTask(1, id);
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

        private void getEpicById(HttpExchange exchange, int id) {
            Task task = manager.getTask(2, id);
            String line;
            if (task == null) {
                line = "Неправильно введён ID!!!";
            } else {
                Gson gson = new Gson();
                line = gson.toJson(task);
            }

            try (OutputStream os = exchange.getResponseBody()) {
                os.write(line.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void getSubtaskById(HttpExchange exchange, int id) {
            Task task = manager.getTask(3, id);
            String line;
            if (task == null) {
                line = "Неправильно введён ID!!!";
            } else {
                Gson gson = new Gson();
                line = gson.toJson(task);
            }

            try (OutputStream os = exchange.getResponseBody()) {
                os.write(line.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private boolean isCreateTask(String[] splitStrings, String method) {
            return splitStrings.length == 2 && method.equals("POST") &&
                    (splitStrings[1].equals("task"));
        }

        private boolean isCreateSubtask(String[] splitStrings, String method) {
            return splitStrings.length == 2 && method.equals("POST") &&
                    (splitStrings[1].equals("epic"));
        }

        private boolean isCreateEpic(String[] splitStrings, String method) {
            return splitStrings.length == 2 && method.equals("POST") &&
                    (splitStrings[1].equals("subtask"));
        }

        private boolean isGetTaskById(String[] splitStrings, String method) {
            return splitStrings.length == 2 && method.equals("GET")
                    && (splitStrings[1].equals("task")); // check!!!
        }

        private boolean isGetEpicById(String[] splitStrings, String method) {
            return splitStrings.length == 2 && method.equals("GET")
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
