package me.TechsCode.SpigotAPI.server;

import com.sun.net.httpserver.HttpServer;
import me.TechsCode.SpigotAPI.manager.HttpRouterManager;

import java.io.IOException;
import java.util.Random;

public class SpigotAPIServer {
    private static HttpServer server;

    public static void main(String[] args) {
        boolean managerMode = false;

        if(!Config.getInstance().isConfigured()){
            System.err.println("Please configure everything in the config.json!");
            return;
        }

        if(args.length == 1){
            if(args[0].equals("manager")){
                managerMode = true;
            }
        }

        if(managerMode){
            Logger.send("Starting up SpigotAPI Manager Server...", true);
            new HttpRouterManager(Config.getInstance().getToken());
        }else{
            Logger.send("Starting up SpigotAPI Server...", true);

            DataManager dataManager = new DataManager();

            new HttpRouter(dataManager, Config.getInstance().getToken());
        }

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
