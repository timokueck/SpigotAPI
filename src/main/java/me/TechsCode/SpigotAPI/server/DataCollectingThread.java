package me.TechsCode.SpigotAPI.server;

import me.TechsCode.SpigotAPI.logging.ConsoleColor;
import me.TechsCode.SpigotAPI.logging.Logger;
import me.TechsCode.SpigotAPI.server.data.Data;
import me.TechsCode.SpigotAPI.server.data.Entry;
import me.TechsCode.SpigotAPI.server.spigot.AuthenticationException;
import me.TechsCode.SpigotAPI.server.spigot.Parser;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class DataCollectingThread extends Thread {

    private static final long REFRESH_DELAY = TimeUnit.MINUTES.toMillis(15);

    private String username, password;
    private Data latest;

    public DataCollectingThread(String username, String password) {
        this.username = username;
        this.password = password;
        this.latest = null;

        start();
    }

    @Override
    public void run() {
        while (true){
            if(latest == null || (System.currentTimeMillis() - latest.getRecordTime()) > REFRESH_DELAY){
                long now = System.currentTimeMillis();

                Logger.log("Logging into Spigot ..");

                Parser parser;

                try {
                    parser = new Parser(username, password);
                } catch (AuthenticationException e){
                    Logger.log("§cCould not authenticate with Spigot");
                    Logger.log(e.getMessage());
                    return;
                }

                List<Entry> resources = parser.retrieveResources();
                Logger.log("[1/4] Collected "+resources.size()+" Resources");

                List<Entry> updates = parser.retrieveUpdates(resources);
                Logger.log("[2/4] Collected "+updates.size()+" Updates");

                List<Entry> reviews = parser.retrieveReviews(resources);
                Logger.log("[3/4] Collected "+reviews.size()+" Reviews");

                List<Entry> purchases = parser.retrievePurchases(resources);
                Logger.log("[4/4] Collected "+purchases.size()+" Purchases");

                long delay = System.currentTimeMillis() - now;
                Logger.log(ConsoleColor.GREEN+"Completed Refreshing Cycle in "+Math.round(TimeUnit.MILLISECONDS.toMinutes(delay))+" minutes!");
                Logger.log("");

                Data data = new Data(System.currentTimeMillis());
                data.set("resources", resources);
                data.set("updates", updates);
                data.set("reviews", reviews);
                data.set("purchases", purchases);

                parser.close();

                latest = data;
            }
        }
    }

    public Data getData(){
        return latest;
    }
}
