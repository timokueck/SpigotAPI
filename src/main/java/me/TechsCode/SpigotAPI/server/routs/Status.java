package me.TechsCode.SpigotAPI.server.routs;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import me.TechsCode.SpigotAPI.server.Config;
import me.TechsCode.SpigotAPI.server.HttpRouter;
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
                long spigotTimeCreated = HttpRouter.getDataManager().getDataset_spigot().getTimeCreated();
                if(spigotTimeCreated + TimeUnit.MINUTES.toMillis(Config.getInstance().getSpigotRefreshDelay() + 10) < System.currentTimeMillis()){
                    obj.put("spigotFetching", false);
                }else{
                    obj.put("spigotFetching", true);
                }

                long marketTimeCreated = HttpRouter.getDataManager().getDataset_market().getTimeCreated();
                if(marketTimeCreated + TimeUnit.MINUTES.toMillis(Config.getInstance().getMarketRefreshDelay() + 10) < System.currentTimeMillis()){
                    obj.put("marketFetching", false);
                }else{
                    obj.put("marketFetching", true);
                }

                long lastSpigotFetch = HttpRouter.getDataManager().getDataset_spigot().getTimeCreated();
                Date createdSpigot = new Date(lastSpigotFetch);
                obj.put("lastSpigotFetch", lastSpigotFetch);
                obj.put("lastSpigotFetchDate", createdSpigot.toString());

                long lastMarketFetch = HttpRouter.getDataManager().getDataset_market().getTimeCreated();
                Date createdMarket = new Date(lastMarketFetch);
                obj.put("lastMarketFetch", lastMarketFetch);
                obj.put("lastMarketFetchDate", createdMarket.toString());

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

    public static String getStatus(String url) throws IOException {
        String result = "";
        int code = 200;
        try {
            URL siteURL = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) siteURL.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(3000);
            connection.connect();

            code = connection.getResponseCode();
            result = String.valueOf(code);
        } catch (Exception e) {
            result = e.getMessage();
        }
        return result;
    }
}
