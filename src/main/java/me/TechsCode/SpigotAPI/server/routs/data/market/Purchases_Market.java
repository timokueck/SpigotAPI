package me.TechsCode.SpigotAPI.server.routs.data.market;

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

public class Purchases_Market implements HttpHandler {
    public void handle(HttpExchange t) throws IOException {
        Map<String, String> params = HttpRouter.getParamMap(t.getRequestURI().getQuery());
        JSONObject obj = new JSONObject();
        String response;
        int responseCode;

        if(params.get("token") !=null){
            String token = params.get("token");
            if(HttpRouter.isTokenValid(token)){
                JsonArray purchases = new JsonArray();
                PurchasesList purchasesList = HttpRouter.getDataManager().getDataset_market().getPurchases();
                purchasesList.stream().map(Purchase::getState).forEach(purchases::add);

                obj.put("data", purchases);
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
