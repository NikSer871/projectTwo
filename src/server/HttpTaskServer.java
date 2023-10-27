package server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import tasks.FileBackedTasksManager;
import tasks.Managers;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.sql.SQLOutput;
import java.util.logging.Handler;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private static final FileBackedTasksManager MANAGER = Managers.getDefault();

    public static HttpServer createServer() throws IOException {

        HttpServer httpServer = HttpServer.create();
        httpServer.bind(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks", new TaskHandler());
        httpServer.start();
        return httpServer;
    }

    public static void main(String[] args) {


    }

    static class TaskHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            System.out.println("Началась обработка!!!!"); // --------
            String method = exchange.getRequestMethod();

            InputStream inputStream = exchange.getRequestBody();
            String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);

            System.out.println(body); //----

            URI uri = exchange.getRequestURI();
            String path = uri.getPath();
            String[] splitStrings = path.split("/");

            if (splitStrings.length == 2 && method.equals("GET") && splitStrings[1].equals("tasks")) {
                getTasks(exchange);
            }



        }

        private void getTasks(HttpExchange exchange) {
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(MANAGER.giveListOfTasks(1).getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }





    }
}
