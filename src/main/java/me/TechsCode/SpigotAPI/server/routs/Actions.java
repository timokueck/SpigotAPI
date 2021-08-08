package me.TechsCode.SpigotAPI.server.routs;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import me.TechsCode.SpigotAPI.server.HttpRouter;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

public class Actions implements HttpHandler {
    public void handle(HttpExchange t) throws IOException {
        Map<String, String> params = HttpRouter.getParamMap(t.getRequestURI().getQuery());
        JSONObject obj = new JSONObject();
        String response;
        int responseCode ;

        if(params.get("token") !=null){
            String token = params.get("token");
            if(HttpRouter.isTokenValid(token)){
                obj.put("Restart", "/actions/restart?token=");
                obj.put("Stop", "/actions/stop?token=");
                response = obj.toString();
                responseCode = 200;
            }else{
                obj.put("Status", "Error");
                obj.put("Msg", "Invalid token");
                response = obj.toString();
                responseCode = 403;
            }
        }else{
            obj.put("Status", "Error");
            obj.put("Msg", "Missing token");
            response = obj.toString();
            responseCode = 400;
        }

        t.sendResponseHeaders(responseCode, response.length());
        OutputStream os = t.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}