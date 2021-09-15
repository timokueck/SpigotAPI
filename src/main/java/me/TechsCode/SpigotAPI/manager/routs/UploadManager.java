package me.TechsCode.SpigotAPI.manager.routs;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import me.TechsCode.SpigotAPI.manager.HttpRouterManager;
import me.TechsCode.SpigotAPI.server.HttpRouter;
import me.TechsCode.SpigotAPI.server.Logger;
import me.TechsCode.SpigotAPI.server.SpigotAPIServer;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

public class UploadManager implements HttpHandler {
    public void handle(HttpExchange t) throws IOException {
        Map<String, String> params = HttpRouter.getParamMap(t.getRequestURI().getQuery());
        JSONObject obj = new JSONObject();
        String response;
        int responseCode;

        if(params.get("token") !=null){
            String token = params.get("token");
            if(HttpRouterManager.isTokenValid(token)){

                response = "<html><body><form action=\"./uploadFile\" method=\"post\" enctype=\"multipart/form-data\"><p><strong>Jar File:</strong><br/><input type=\"file\" name=\"myFile\" accept=\".jar\"></p><input type=\"submit\" value = \"Submit\"></form></body></html>";
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
