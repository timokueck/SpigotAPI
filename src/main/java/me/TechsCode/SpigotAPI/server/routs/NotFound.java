package me.TechsCode.SpigotAPI.server.routs;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.io.OutputStream;

public class NotFound implements HttpHandler {
    public void handle(HttpExchange t) throws IOException {
        JSONObject main = new JSONObject();
        main.put("Status", "error");
        main.put("Msg", "Page not found");
        String response = main.toString();
        int responseCode = 200;

        t.sendResponseHeaders(responseCode, response.length());
        OutputStream os = t.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}
