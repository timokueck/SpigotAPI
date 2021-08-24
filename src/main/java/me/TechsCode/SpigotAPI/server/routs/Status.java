package me.TechsCode.SpigotAPI.server.routs;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import me.TechsCode.SpigotAPI.server.Config;
import me.TechsCode.SpigotAPI.server.HttpRouter;
import me.TechsCode.SpigotAPI.server.Logger;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
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
                if(HttpRouter.getDataManager().getDataset_spigot() != null){
                    long spigotTimeCreated = HttpRouter.getDataManager().getDataset_spigot().getTimeCreated();
                    if(spigotTimeCreated + TimeUnit.MINUTES.toMillis(Config.getInstance().getSpigotRefreshDelay() * 2L) < System.currentTimeMillis()){
                        obj.put("spigotFetching", false);
                    }else{
                        obj.put("spigotFetching", true);
                    }
                    long lastSpigotFetch = HttpRouter.getDataManager().getDataset_spigot().getTimeCreated();
                    Date createdSpigot = new Date(lastSpigotFetch);
                    obj.put("lastSpigotFetch", lastSpigotFetch);
                    obj.put("lastSpigotFetchDate", createdSpigot.toString());
                }else{
                    obj.put("spigotFetching", false);
                    obj.put("lastSpigotFetch", 0);
                    obj.put("lastSpigotFetchDate", "Unknown");
                }

                if(HttpRouter.getDataManager().getDataset_market() != null) {
                    long marketTimeCreated = HttpRouter.getDataManager().getDataset_market().getTimeCreated();
                    if (marketTimeCreated + TimeUnit.MINUTES.toMillis(Config.getInstance().getMarketRefreshDelay() * 2L) < System.currentTimeMillis()) {
                        obj.put("marketFetching", false);
                    } else {
                        obj.put("marketFetching", true);
                    }
                    long lastMarketFetch = HttpRouter.getDataManager().getDataset_market().getTimeCreated();
                    Date createdMarket = new Date(lastMarketFetch);
                    obj.put("lastMarketFetch", lastMarketFetch);
                    obj.put("lastMarketFetchDate", createdMarket.toString());
                }else{
                    obj.put("marketFetching", false);
                    obj.put("lastMarketFetch", 0);
                    obj.put("lastMarketFetchDate", "Unknown");
                }

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
