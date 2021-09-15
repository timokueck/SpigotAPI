package me.TechsCode.SpigotAPI.manager;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import me.TechsCode.SpigotAPI.manager.routs.*;
import me.TechsCode.SpigotAPI.server.Config;
import me.TechsCode.SpigotAPI.server.Logger;
import me.TechsCode.SpigotAPI.server.SpigotAPIServer;
import me.TechsCode.SpigotAPI.server.routs.NotFound;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HttpRouterManager {
    private static String apiToken;

    public HttpRouterManager(String apiToken) {
        HttpRouterManager.apiToken = apiToken;

        try {
            initServer();
            startServer();
        } catch (Exception e) {
            Logger.send(e.getMessage(), true);
            Logger.send(Arrays.toString(e.getStackTrace()), true);
        }

        Runtime.getRuntime().addShutdownHook(new Thread(HttpRouterManager::stopServer));
    }

    private void initServer() throws IOException {
        InetSocketAddress sockAddress = new InetSocketAddress("0.0.0.0", Config.getInstance().getManagerPort());
        SpigotAPIServer.setServer(HttpServer.create(sockAddress, 0));

        SpigotAPIServer.getServer().createContext("/", new NotFound());
        SpigotAPIServer.getServer().createContext("/docs", new DocsManager());
        SpigotAPIServer.getServer().createContext("/start", new StartManager());
        SpigotAPIServer.getServer().createContext("/stop", new StopManager());
        SpigotAPIServer.getServer().createContext("/restart", new RestartManager());
        SpigotAPIServer.getServer().createContext("/killchrome", new KillChromeManager());
        SpigotAPIServer.getServer().createContext("/upload", new UploadManager());
        SpigotAPIServer.getServer().createContext("/uploadFile", new FileUploadManager());

        SpigotAPIServer.getServer().setExecutor(null);
    }

    private void startServer() {
        try {
            if(SpigotAPIServer.getServer() != null) {
                SpigotAPIServer.getServer().start();
                Logger.send("Manager listening on port "+Config.getInstance().getManagerPort(), true);
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
                System.out.print("Manager stopped");
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
}