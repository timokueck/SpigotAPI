package me.TechsCode.SpigotAPI.server.routs;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;

import org.json.simple.JSONObject;

public class Home implements HttpHandler {
    public void handle(HttpExchange t) throws IOException {
        JSONObject obj = new JSONObject();
        obj.put("Status", "/status?token=");
        obj.put("Resources", "/resources?token=");
        obj.put("Reviews", "/reviews?token=");
        obj.put("Purchases", "/purchases?token=");
        obj.put("Updates", "/updates?token=");
        obj.put("Actions", "/actions?token=");
        obj.put("Verify", "/verifyUser?token=&user=&showAll=false");
        String response = obj.toString();
        int responseCode = 200;

        t.sendResponseHeaders(responseCode, response.length());
        OutputStream os = t.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}
