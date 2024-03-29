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
                obj.put("Kill Chrome", "/actions/killchrome?token=");
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
