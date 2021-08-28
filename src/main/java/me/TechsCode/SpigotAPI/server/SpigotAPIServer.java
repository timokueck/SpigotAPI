package me.TechsCode.SpigotAPI.server;

import com.sun.net.httpserver.HttpServer;

import java.io.FileNotFoundException;
import java.util.Random;

public class SpigotAPIServer {
    private static HttpServer server;
    private static HttpRouter router;

    public static void main(String[] args) throws FileNotFoundException {
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

    public static int getRandomInt(){
        Random r = new Random();
        int low = 10;
        int high = 100;
        int result = r.nextInt(high-low) + low;
        return result;
    }
}
