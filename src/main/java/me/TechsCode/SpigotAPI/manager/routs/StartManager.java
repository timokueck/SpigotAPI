package me.TechsCode.SpigotAPI.manager.routs;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import me.TechsCode.SpigotAPI.manager.HttpRouterManager;
import me.TechsCode.SpigotAPI.server.HttpRouter;
import me.TechsCode.SpigotAPI.server.Logger;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Map;

public class StartManager implements HttpHandler {
    public void handle(HttpExchange t) throws IOException {
        Map<String, String> params = HttpRouter.getParamMap(t.getRequestURI().getQuery());
        JSONObject obj = new JSONObject();
        String response;
        int responseCode;

        if(params.get("token") !=null){
            String token = params.get("token");
            if(HttpRouterManager.isTokenValid(token)){
                Logger.send("Starting SpigotAPI Server...", true);

                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    Logger.send(e.getMessage(), true);
                    Logger.send(Arrays.toString(e.getStackTrace()), true);
                }

                try {
                    String currentPath = new java.io.File(".").getCanonicalPath();
                    Runtime.getRuntime().exec("cmd.exe /c start "+currentPath+"\\startup\\SpigotAPI.exe");
                    Thread.sleep(1000L);
                } catch (IOException|InterruptedException e) {
                    Logger.send(e.getMessage(), true);
                    Logger.send(Arrays.toString(e.getStackTrace()), true);
                }

                obj.put("Status", "Success");
                obj.put("Msg", "Starting API");
                response = obj.toString();
                responseCode = 200;
            }else{
                obj.put("Status", "Error");
                obj.put("Msg", "Invalid token");
                response = obj.toString();
                responseCode = 401;
            }
        }else{
            obj.put("Status", "Error");
            obj.put("Msg", "Missing token");
            response = obj.toString();
            responseCode = 401;
        }

        t.sendResponseHeaders(responseCode, response.length());
        OutputStream os = t.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}
