package me.TechsCode.SpigotAPI.server.routs;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;

import org.json.simple.JSONObject;

public class Docs implements HttpHandler {
    public void handle(HttpExchange t) throws IOException {
        JSONObject main = new JSONObject();
        main.put("Status", "/status?token=");
        main.put("Actions", "/actions?token=");
        main.put("Verify User", "/request_verify_user?userId=&token=");
        main.put("Check User Verification", "/verify_user?userId=&token=");
        main.put("Purchases", "/purchases?token=");
        main.put("Purchase options", "&userId=, &username=, &resourceId=, &resourceName=");
        String response = main.toString();
        int responseCode = 200;

        t.sendResponseHeaders(responseCode, response.length());
        OutputStream os = t.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}
