package me.TechsCode.SpigotAPI.server.routs;

import com.google.gson.JsonArray;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import me.TechsCode.SpigotAPI.data.UserVerification;
import me.TechsCode.SpigotAPI.server.*;
import me.TechsCode.SpigotAPI.server.browsers.SpigotVerifyBrowser;
import me.TechsCode.SpigotAPI.server.browsers.VirtualVerifyBrowser;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class VerifyUser implements HttpHandler {
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

                    int codeLength = 9;
                    if(params.containsKey("codeLength")){
                        codeLength = Integer.parseInt(params.get("codeLength"));
                    }
                    String code = "TechsVerification." + RandomGenerator.string(codeLength) + "";
                    UserVerification verification = new UserVerification(userId, code);

                    if (!DataManager.getVerificationQueue().userId(userId).isPresent()){
                        DataManager.addVerification(verification);
                        obj.put("status", "success");
                        obj.put("msg", "Verification added to queue");
                        obj.put("userId", userId);
                        obj.put("code", code);
                        response = obj.toString();
                        responseCode = 200;
                    }else{
                        UserVerification oldVerification = DataManager.getVerificationQueue().userId(userId).get();
                        DataManager.getVerificationQueue().remove(oldVerification);

                        DataManager.addVerification(verification);
                        obj.put("status", "success");
                        obj.put("msg", "Old verification deleted and new verification added to queue");
                        obj.put("userId", userId);
                        obj.put("code", code);
                        response = obj.toString();
                        responseCode = 200;
                    }
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
