package me.TechsCode.SpigotAPI.manager.routs;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.io.OutputStream;

public class DocsManager implements HttpHandler {
    public void handle(HttpExchange t) throws IOException {
        JSONObject main = new JSONObject();
        main.put("API | Start", "/start?token=");
        main.put("API | Stop", "/stop?token=");
        main.put("API | Restart", "/restart?token=");
        main.put("API | Kill Chrome", "/killchrome?token=");
        String response = main.toString();
        int responseCode = 200;

        t.sendResponseHeaders(responseCode, response.length());
        OutputStream os = t.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}
