package me.TechsCode.SpigotAPI.server;

import me.TechsCode.SpigotAPI.logging.ConsoleColor;
import me.TechsCode.SpigotAPI.logging.Logger;

public class SpigotAPIServer {

    public static void main(String[] args){
        Logger.log("Starting up SpigotAPI Server...");

        if(args.length != 3){
            Logger.log(ConsoleColor.RED+"Invalid Start-Up Parameters. Use following:");
            Logger.log("java -jar SpigotAPI.jar <Spigot Username> <Spigot Password> <API Token>");
            Logger.log("");
            Logger.log("Two Factor Authentication has to be disabled first");
            Logger.log("API Token: A random String that should be secure");
            return;
        }

        String spigotUsername = args[0];
        String spigotPassword = args[1];
        String apiToken = args[2];

        DataCollectingThread dataManager = new DataCollectingThread(spigotUsername, spigotPassword);
        APIEndpoint webServer = new APIEndpoint(dataManager, apiToken);

        Logger.log(ConsoleColor.GREEN+"Listening on port "+webServer.getListeningPort()+" with token "+apiToken);
        Logger.log("Startup Completed");
    }
}
