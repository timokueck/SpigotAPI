package me.TechsCode.SpigotAPI.server;

import com.sun.net.httpserver.HttpServer;
import me.TechsCode.SpigotAPI.server.routs.Actions;
import me.TechsCode.SpigotAPI.server.routs.Home;
import me.TechsCode.SpigotAPI.server.routs.Status;
import me.TechsCode.SpigotAPI.server.routs.actions.Restart;
import me.TechsCode.SpigotAPI.server.routs.actions.Stop;
import me.TechsCode.SpigotAPI.server.routs.data.market.*;
import me.TechsCode.SpigotAPI.server.routs.data.spigot.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HttpRouter {
    private static DataManager dataManager;
    private static String apiToken;

    public HttpRouter(DataManager dataManager, String apiToken) {
        HttpRouter.dataManager = dataManager;
        HttpRouter.apiToken = apiToken;

        try {
            initServer();
            startServer();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Runtime.getRuntime().addShutdownHook(new Thread(HttpRouter::stopServer));
    }

    private void initServer() throws IOException {
        InetSocketAddress sockAddress = new InetSocketAddress("0.0.0.0", Config.getInstance().getPort());
        SpigotAPIServer.setServer(HttpServer.create(sockAddress, 0));

        SpigotAPIServer.getServer().createContext("/", new Home());
        SpigotAPIServer.getServer().createContext("/status", new Status());
        SpigotAPIServer.getServer().createContext("/actions", new Actions());
        SpigotAPIServer.getServer().createContext("/actions/restart", new Restart());
        SpigotAPIServer.getServer().createContext("/actions/stop", new Stop());

        //Data spigot
        SpigotAPIServer.getServer().createContext("/data/spigot/all", new All_Spigot());
        SpigotAPIServer.getServer().createContext("/data/spigot/resources", new Resources_Spigot());
        SpigotAPIServer.getServer().createContext("/data/spigot/purchases", new Purchases_Spigot());
        SpigotAPIServer.getServer().createContext("/data/spigot/updates", new Updates_Spigot());
        SpigotAPIServer.getServer().createContext("/data/spigot/reviews", new Reviews_Spigot());

        //Data market
        SpigotAPIServer.getServer().createContext("/data/market/all", new All_Market());
        SpigotAPIServer.getServer().createContext("/data/market/resources", new Resources_Market());
        SpigotAPIServer.getServer().createContext("/data/market/purchases", new Purchases_Market());
        SpigotAPIServer.getServer().createContext("/data/spigot/updates", new Updates_Market());
        SpigotAPIServer.getServer().createContext("/data/market/reviews", new Reviews_Market());

        //Verify User
        SpigotAPIServer.getServer().createContext("/spigot/verifyUser", new VerifyUser_Spigot());
        SpigotAPIServer.getServer().createContext("/market/verifyUser", new VerifyUser_Market());

        SpigotAPIServer.getServer().setExecutor(null);
    }

    private void startServer() {
        try {
            if(SpigotAPIServer.getServer() != null) {
                SpigotAPIServer.getServer().start();
                System.out.println("API token: "+Config.getInstance().getToken());
                Logger.send("Listening on port "+Config.getInstance().getPort(), true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void stopServer() {
        try {
            if(SpigotAPIServer.getServer() != null){
                SpigotAPIServer.getServer().stop(0);
                System.out.print("API stopped");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isTokenValid(String token){
        return apiToken.equals(token);
    }

    public static Map<String, String> getParamMap(String query) {
        if (query == null || query.isEmpty()) return Collections.emptyMap();
        return Stream.of(query.split("&"))
                .filter(s -> !s.isEmpty())
                .map(kv -> kv.split("=", 2))
                .collect(Collectors.toMap(x -> x[0], x-> x[1]));
    }

    public static DataManager getDataManager(){
        return dataManager;
    }
}