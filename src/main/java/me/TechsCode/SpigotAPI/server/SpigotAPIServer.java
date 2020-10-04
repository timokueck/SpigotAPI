package me.TechsCode.SpigotAPI.server;

public class SpigotAPIServer {

    public static void main(String[] args){
        System.out.println("Starting up SpigotAPI Server...");

        if(!Config.getInstance().isConfigured()){
            System.err.println("Please configure everything in the config.json!");
            return;
        }

        DataManager dataManager = new DataManager();
        APIEndpoint webServer = new APIEndpoint(dataManager, Config.getInstance().getToken());

        System.out.println("Listening on port "+webServer.getListeningPort()+" with token "+Config.getInstance().getToken());
    }
}
