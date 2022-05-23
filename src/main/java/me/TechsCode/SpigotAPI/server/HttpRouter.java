package me.TechsCode.SpigotAPI.server;

import com.sun.net.httpserver.HttpServer;
import me.TechsCode.SpigotAPI.server.routs.*;
import me.TechsCode.SpigotAPI.server.routs.actions.CloseChrome;
import me.TechsCode.SpigotAPI.server.routs.actions.Restart;
import me.TechsCode.SpigotAPI.server.routs.actions.Stop;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Arrays;
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
            Logger.send(e.getMessage(), true);
            Logger.send(Arrays.toString(e.getStackTrace()), true);
        }

        Runtime.getRuntime().addShutdownHook(new Thread(HttpRouter::stopServer));
    }

    private void initServer() throws IOException {
        InetSocketAddress sockAddress = new InetSocketAddress("0.0.0.0", Config.getInstance().getPort());
        SpigotAPIServer.setServer(HttpServer.create(sockAddress, 0));

        SpigotAPIServer.getServer().createContext("/", new NotFound());
        SpigotAPIServer.getServer().createContext("/docs", new Docs());
        SpigotAPIServer.getServer().createContext("/status", new Status());
        SpigotAPIServer.getServer().createContext("/actions", new Actions());
        SpigotAPIServer.getServer().createContext("/actions/stop", new Stop());
        SpigotAPIServer.getServer().createContext("/actions/restart", new Restart());
        SpigotAPIServer.getServer().createContext("/actions/killchrome", new CloseChrome());

        SpigotAPIServer.getServer().createContext("/purchases", new Purchases());
        SpigotAPIServer.getServer().createContext("/verify_user", new CheckUserVerification());
        SpigotAPIServer.getServer().createContext("/request_verify_user", new VerifyUser());

        SpigotAPIServer.getServer().setExecutor(null);
    }

    private void startServer() {
        try {
            if(SpigotAPIServer.getServer() != null) {
                SpigotAPIServer.getServer().start();
                Logger.info("API token: "+Config.getInstance().getToken(), false);
                Logger.info("API listening on port "+Config.getInstance().getPort(), false);
            }
        } catch (Exception e) {
            Logger.send(e.getMessage(), true);
            Logger.send(Arrays.toString(e.getStackTrace()), true);
        }
    }

    public static void stopServer() {
        try {
            if(SpigotAPIServer.getServer() != null){
                SpigotAPIServer.getServer().stop(0);
                System.out.print("API stopped");
            }
        } catch (Exception e) {
            Logger.send(e.getMessage(), true);
            Logger.send(Arrays.toString(e.getStackTrace()), true);
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