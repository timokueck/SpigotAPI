package me.TechsCode.SpigotAPI.server.routs;

import com.google.gson.JsonArray;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import me.TechsCode.SpigotAPI.server.Config;
import me.TechsCode.SpigotAPI.server.HttpRouter;
import me.TechsCode.SpigotAPI.server.Logger;
import me.TechsCode.SpigotAPI.server.browsers.SpigotVerifyBrowser;
import me.TechsCode.SpigotAPI.server.browsers.VirtualVerifyBrowser;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class CheckUserVerification implements HttpHandler {
    public void handle(HttpExchange t) throws IOException {
        Map<String, String> params = HttpRouter.getParamMap(t.getRequestURI().getQuery());
        JSONObject obj = new JSONObject();
        String response;
        int responseCode;

        if(params.get("token") !=null){
            String token = params.get("token");
            if(HttpRouter.isTokenValid(token)){

                if(params.get("userId") == null) {
                    obj.put("error", "Missing userId");
                    response = obj.toString();
                    responseCode = 401;
                }else{
                    String userId = params.get("userId");

                    JSONObject userVerification = VirtualVerifyBrowser.getVerifiedUser(userId);
                    if(userVerification == null){
                        obj.put("status", "Error");
                        obj.put("msg", "Verification not found");
                        response = obj.toString();
                        responseCode = 404;
                    }else{
                        response = userVerification.toString();
                        responseCode = 200;
                    }
                }
            }else{
                obj.put("status", "Error");
                obj.put("msg", "Invalid token");
                response = obj.toString();
                responseCode = 401;
            }
        }else{
            obj.put("status", "Error");
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
