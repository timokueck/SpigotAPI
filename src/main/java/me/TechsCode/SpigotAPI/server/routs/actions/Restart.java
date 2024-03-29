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
import java.util.Arrays;
import java.util.Map;

public class Restart implements HttpHandler {
    public void handle(HttpExchange t) throws IOException {
        Map<String, String> params = HttpRouter.getParamMap(t.getRequestURI().getQuery());
        JSONObject obj = new JSONObject();
        String response;
        int responseCode;

        boolean restartAPI = false;

        if(params.get("token") !=null){
            String token = params.get("token");
            if(HttpRouter.isTokenValid(token)){
                Logger.send("Restarting SpigotAPI Server...", true);
                restartAPI = true;
                VirtualBrowser.quit();
                SpigotAPIServer.KillProcess("chrome.exe");

                obj.put("status", "success");
                obj.put("msg", "Restarting API");
                response = obj.toString();
                responseCode = 200;
            }else{
                obj.put("status", "error");
                obj.put("msg", "Invalid token");
                response = obj.toString();
                responseCode = 401;
            }
        }else{
            obj.put("status", "error");
            obj.put("msg", "Missing token");
            response = obj.toString();
            responseCode = 401;
        }

        t.sendResponseHeaders(responseCode, response.length());
        OutputStream os = t.getResponseBody();
        os.write(response.getBytes());
        os.close();

        if (restartAPI){
            try {
                String currentPath = new java.io.File(".").getCanonicalPath();
                Runtime.getRuntime().exec("cmd.exe /c start "+currentPath+"\\startup\\SpigotAPI.exe");
                Thread.sleep(1000L);
            } catch (IOException|InterruptedException e) {
                Logger.send(e.getMessage(), true);
                Logger.send(Arrays.toString(e.getStackTrace()), true);
            }
            System.exit(0);
        }
    }
}
