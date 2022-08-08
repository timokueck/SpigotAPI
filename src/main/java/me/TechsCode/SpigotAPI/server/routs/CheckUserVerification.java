package me.TechsCode.SpigotAPI.server.routs;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import me.TechsCode.SpigotAPI.data.UserVerification;
import me.TechsCode.SpigotAPI.server.DataManager;
import me.TechsCode.SpigotAPI.server.HttpRouter;
import org.json.JSONArray;
import org.json.simple.JSONObject;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.Optional;

public class CheckUserVerification implements HttpHandler {
    public void handle(HttpExchange t) throws IOException {
        Map<String, String> params = HttpRouter.getParamMap(t.getRequestURI().getQuery());
        JSONObject obj = new JSONObject();
        String response;
        int responseCode;

        if(params.get("token") !=null){
            String token = params.get("token");
            if(HttpRouter.isTokenValid(token)){

                if(params.get("userId") != null){
                    String userId = params.get("userId");
                    Optional<UserVerification> userVerification = DataManager.getVerificationQueue().userId(userId);
                    if(userVerification.isPresent()){
                        response = userVerification.get().toJsonObject().toString();
                        responseCode = 200;
                    }else{
                        obj.put("status", "error");
                        obj.put("msg", "Verification not found");
                        response = obj.toString();
                        responseCode = 404;
                    }
                }else if(params.get("code") != null){
                    String code = params.get("code");
                    Optional<UserVerification> userVerification = DataManager.getVerificationQueue().code(code);
                    if(userVerification.isPresent()){
                        response = userVerification.get().toJsonObject().toString();
                        responseCode = 200;
                    }else{
                        obj.put("status", "error");
                        obj.put("msg", "Verification not found");
                        response = obj.toString();
                        responseCode = 404;
                    }
                }else{
                    obj.put("error", "Missing userId or code");
                    response = obj.toString();
                    responseCode = 401;
                }
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
