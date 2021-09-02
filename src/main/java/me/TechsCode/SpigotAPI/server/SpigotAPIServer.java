package me.TechsCode.SpigotAPI.server;

import com.sun.net.httpserver.HttpServer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Random;

public class SpigotAPIServer {
    private static HttpServer server;

    public static void main(String[] args) throws FileNotFoundException {
        if(!Config.getInstance().isConfigured()){
            System.err.println("Please configure everything in the config.json!");
            return;
        }

        Logger.send("Starting up SpigotAPI Server...", true);

        DataManager dataManager = new DataManager();

        new HttpRouter(dataManager, Config.getInstance().getToken());

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
        return r.nextInt(high-low) + low;
    }

    public static void KillProcess(String processName) {
        try {
            Process p = Runtime.getRuntime().exec("taskkill /F /IM "+processName+" /T");
        } catch (IOException e) {
            e.printStackTrace();
            Logger.send("Error killing process: "+processName, true);
            Logger.send(e.getMessage(), true);
        }
    }
}
