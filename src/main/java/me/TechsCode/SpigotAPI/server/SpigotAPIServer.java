package me.TechsCode.SpigotAPI.server;

import com.sun.net.httpserver.HttpServer;

public class SpigotAPIServer {
    private static HttpServer server;
    private static HttpRouter router;

    public static void main(String[] args){
        if(!Config.getInstance().isConfigured()){
            System.err.println("Please configure everything in the config.json!");
            return;
        }

        Logger.send("Starting up SpigotAPI Server...", true);

        DataManager dataManager = new DataManager();

        router = new HttpRouter(dataManager, Config.getInstance().getToken());
    }

    public static HttpServer getServer(){
        return server;
    }

    public static void setServer(HttpServer HttpServer){
        server = HttpServer;
    }

    public static HttpRouter getRouter(){
        return router;
    }

    public static void setRouter(HttpRouter HttpRouter){
        router = HttpRouter;
    }
}
