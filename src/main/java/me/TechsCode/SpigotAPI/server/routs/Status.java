package me.TechsCode.SpigotAPI.server.routs;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import me.TechsCode.SpigotAPI.server.Config;
import me.TechsCode.SpigotAPI.server.HttpRouter;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Status implements HttpHandler {
    public void handle(HttpExchange t) throws IOException {
        Map<String, String> params = HttpRouter.getParamMap(t.getRequestURI().getQuery());
        JSONObject obj = new JSONObject();
        String response;
        int responseCode ;

        if(params.get("token") !=null){
            String token = params.get("token");
            if(HttpRouter.isTokenValid(token)){
                if(HttpRouter.getDataManager().getDataset() != null){
                    obj.put("spigotFetching", HttpRouter.getDataManager().isFetching());
                    long lastSpigotFetch = HttpRouter.getDataManager().getDataset().getTimeCreated();
                    Date createdSpigot = new Date(lastSpigotFetch);
                    obj.put("lastSpigotFetch", lastSpigotFetch);
                    obj.put("lastSpigotFetchDate", createdSpigot.toString());
                }else{
                    obj.put("spigotFetching", false);
                    obj.put("lastSpigotFetch", 0);
                    obj.put("lastSpigotFetchDate", "Unknown");
                }

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
    }
}
