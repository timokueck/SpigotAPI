package me.TechsCode.SpigotAPI.server.routs;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;

import org.json.simple.JSONObject;

public class Docs implements HttpHandler {
    public void handle(HttpExchange t) throws IOException {
        JSONObject spigot = new JSONObject();
        spigot.put("Spigot | Verify", "/spigot/verifyUser?token=&user=&showAll=false");
        spigot.put("Spigot | All", "/data/spigot/all?token=");
        spigot.put("Spigot | Resources", "/data/spigot/resources?token=");
        spigot.put("Spigot | Reviews", "/data/spigot/reviews?token=");
        spigot.put("Spigot | Purchases", "/data/spigot/purchases?token=");
        spigot.put("Spigot | Updates", "/data/spigot/updates?token=");

        JSONObject market = new JSONObject();
        market.put("Market | Verify", "/market/verifyUser?token=&user=&showAll=false");
        market.put("Market | All", "/data/market/all?token=");
        market.put("Market | Resources", "/data/market/resources?token=");
        market.put("Market | Reviews", "/data/market/reviews?token=");
        market.put("Market | Purchases", "/data/market/purchases?token=");
        market.put("Market | Updates", "/data/market/updates?token=");

        JSONObject main = new JSONObject();
        main.put("Status", "/status?token=");
        main.put("Actions", "/actions?token=");
        main.put("spigot", spigot);
        main.put("market", market);
        String response = main.toString();
        int responseCode = 200;

        t.sendResponseHeaders(responseCode, response.length());
        OutputStream os = t.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}
