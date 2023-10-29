package server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import tasks.FileBackedTasksManager;
import tasks.Managers;
import tasks.Task;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.sql.SQLOutput;
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
            exchange.sendResponseHeaders(200, 0);
            String method = exchange.getRequestMethod();
            InputStream inputStream = exchange.getRequestBody();
            String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);

            System.out.println(body);

            URI uri = exchange.getRequestURI();
            String path = uri.getPath();
            String[] splitStrings = path.substring(1).split("/");
            System.out.println(Arrays.toString(splitStrings));

            responseOnRequest(splitStrings, method, exchange);

            System.out.println("Hello!!!");
        }

        private void responseOnRequest(String[] splitStrings, String method, HttpExchange exchange) {
            if (isGetTasks(splitStrings, method)) {
                getTasks(exchange);
                System.out.println("sdsdsd");
                return;
            } else if (isGetHistory(splitStrings, method)) {
                getHistory(exchange);
                return;
            }

            if (isGetTaskById(splitStrings, method)) {
                getTaskById(exchange, getId(splitStrings[2]));
                return;
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
            String line = "Id: = " + task.getId() + " " + "name: " + task.getName();
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(line.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private boolean isGetTaskById(String[] splitStrings, String method) {
            return splitStrings.length == 3 && method.equals("GET") && (splitStrings[2].contains("?id=")); // check!!!
        }

        private boolean isGetTasks(String[] splitStrings, String method) {
            return splitStrings.length == 2 && method.equals("GET") && splitStrings[1].equals("task");
        }

        private boolean isGetHistory(String[] splitStrings, String method) {
            return splitStrings.length == 2 && method.equals("GET") && splitStrings[1].equals("history");
        }

        private int getId(String str) {
            return Integer.parseInt(str.substring(str.indexOf("=") + 1));
        }



    }
}
