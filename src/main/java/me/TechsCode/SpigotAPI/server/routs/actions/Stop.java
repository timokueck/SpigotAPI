package me.TechsCode.SpigotAPI.server.routs.actions;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import me.TechsCode.SpigotAPI.server.HttpRouter;
import me.TechsCode.SpigotAPI.server.Logger;
import me.TechsCode.SpigotAPI.server.SpigotAPIServer;
import me.TechsCode.SpigotAPI.server.browsers.VirtualBrowser;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

public class Stop implements HttpHandler {
    public void handle(HttpExchange t) throws IOException {
        Map<String, String> params = HttpRouter.getParamMap(t.getRequestURI().getQuery());
        JSONObject obj = new JSONObject();
        String response;
        int responseCode;

        boolean stopAPI = false;

        if (params.get("token") != null) {
            String token = params.get("token");
            if (HttpRouter.isTokenValid(token)) {
                Logger.send("Stopping SpigotAPI Server...", true);
                stopAPI = true;
                VirtualBrowser.quit();
                SpigotAPIServer.KillProcess("chrome.exe");

                obj.put("status", "success");
                obj.put("msg", "Stopping API");
                response = obj.toString();
                responseCode = 200;
            } else {
                obj.put("status", "error");
                obj.put("msg", "Invalid token");
                response = obj.toString();
                responseCode = 401;
            }
        } else {
            obj.put("status", "error");
            obj.put("msg", "Missing token");
            response = obj.toString();
            responseCode = 401;
        }

        t.sendResponseHeaders(responseCode, response.length());
        OutputStream os = t.getResponseBody();
        os.write(response.getBytes());
        os.close();

        if (stopAPI) {
            System.exit(0);
        }
    }
}
