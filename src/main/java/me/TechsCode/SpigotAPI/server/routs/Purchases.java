package me.TechsCode.SpigotAPI.server.routs;

import com.google.gson.JsonArray;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import me.TechsCode.SpigotAPI.data.Purchase;
import me.TechsCode.SpigotAPI.data.lists.PurchasesList;
import me.TechsCode.SpigotAPI.server.HttpRouter;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

public class Purchases implements HttpHandler {
    public void handle(HttpExchange t) throws IOException {
        Map<String, String> params = HttpRouter.getParamMap(t.getRequestURI().getQuery());
        JSONObject obj = new JSONObject();
        String response;
        int responseCode ;

        if(params.get("token") !=null){
            String token = params.get("token");
            if(HttpRouter.isTokenValid(token)){

                JsonArray purchases = new JsonArray();
                PurchasesList purchasesList = HttpRouter.getDataManager().getDataset().getPurchases();

                if(params.get("userId") !=null) {
                    String userId = params.get("userId");
                    purchasesList = purchasesList.userId(userId);
                }
                if(params.get("username") !=null) {
                    String username = params.get("username");
                    purchasesList = purchasesList.username(username);
                }
                if(params.get("resourceId") !=null) {
                    String resourceId = params.get("resourceId");
                    purchasesList = purchasesList.resourceId(resourceId);
                }
                if(params.get("resourceName") !=null) {
                    String resourceName = params.get("resourceName");
                    purchasesList = purchasesList.resourceName(resourceName);
                }

                purchasesList.stream().map(Purchase::getState).forEach(purchases::add);

                obj.put("data", purchases);
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
