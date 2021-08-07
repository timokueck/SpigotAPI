package me.TechsCode.SpigotAPI.server.routs;

import com.google.gson.JsonArray;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import me.TechsCode.SpigotAPI.data.ProfileComment;
import me.TechsCode.SpigotAPI.server.Config;
import me.TechsCode.SpigotAPI.server.HttpRouter;
import me.TechsCode.SpigotAPI.server.spigot.SpigotBrowser;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;

public class Status implements HttpHandler {
    public void handle(HttpExchange t) throws IOException {
        Map<String, String> params = HttpRouter.getParamMap(t.getRequestURI().getQuery());
        JSONObject obj = new JSONObject();
        String response;
        int responseCode ;

        if(params.get("token") !=null){
            String token = params.get("token");
            if(HttpRouter.isTokenValid(token)){
                String spigotStatus = getStatus("https://static.spigotmc.org/img/spigot.png");
                if(spigotStatus.equals("200")){
                    obj.put("spigotStatus", "online");
                }else{
                    obj.put("spigotStatus", "offline");
                    obj.put("statusCode", spigotStatus);
                }

                long lastFetch = HttpRouter.getDataManager().getDataset_market().getTimeCreated();
                Date created = new Date(lastFetch);
                obj.put("lastFetch", lastFetch);
                obj.put("lastFetchDate", created.toString());
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
