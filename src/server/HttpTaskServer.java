package server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import tasks.FileBackedTasksManager;
import tasks.Managers;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class HttpTaskServer implements HttpHandler {
    private static final int PORT = 8080;
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private final FileBackedTasksManager manager = Managers.getDefault();

    @Override

    public void handle(HttpExchange exchange) throws IOException {

    }
}
