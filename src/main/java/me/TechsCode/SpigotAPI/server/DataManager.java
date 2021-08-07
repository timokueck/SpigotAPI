package me.TechsCode.SpigotAPI.server;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.TechsCode.SpigotAPI.data.Dataset;
import me.TechsCode.SpigotAPI.data.lists.*;
import me.TechsCode.SpigotAPI.server.spigot.MarketBrowser;
import me.TechsCode.SpigotAPI.server.spigot.SpigotBrowser;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

public class DataManager extends Thread {

    private static final long REFRESH_DELAY = TimeUnit.MINUTES.toMillis(15);

    private Dataset latest_spigot;
    private Dataset latest_market;

    public DataManager() {
        this.latest_spigot = load("spigot");
        this.latest_market = load("market");

        start();
    }

    private void save(Dataset dataset){
        File file = new File("data/lastDataset_"+dataset.getMarket()+".json");

        if(file.exists()) file.delete();

        String json = dataset.toJsonObject().toString();

        try {
            FileUtils.writeStringToFile(file, json, Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Dataset load(String market){
        File file = new File("data/lastDataset_"+market+".json");

        if(!file.exists()) return null;

        try {
            String json = FileUtils.readFileToString(file, Charset.defaultCharset());

            JsonParser parser = new JsonParser();
            JsonObject jsonObject = (JsonObject) parser.parse(json);

            return new Dataset(jsonObject);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void run() {
        while (true){
            if(latest_spigot == null || (System.currentTimeMillis() - latest_spigot.getTimeCreated()) > REFRESH_DELAY){
                try {
                    long now = System.currentTimeMillis();

                    Config config = Config.getInstance();
                    SpigotBrowser parser = new SpigotBrowser(config.getMarketUsername(), config.getMarketPassword(), true);

                    ResourcesList resources = parser.collectResources();
                    System.out.println("[1/4] Collected "+resources.size()+" Resources");

                    UpdatesList updates = parser.collectUpdates(resources);
                    System.out.println("[2/4] Collected "+updates.size()+" Updates");

                    ReviewsList reviews = parser.collectReviews(resources);
                    System.out.println("[3/4] Collected "+reviews.size()+" Reviews");

                    PurchasesList purchases = parser.collectPurchases(resources);
                    System.out.println("[4/4] Collected "+purchases.size()+" Purchases");

                    String spigotStatus = parser.getAPIStatus();
                    SpigotAPIServer.setSpigotStatus(spigotStatus);

                    parser.close();

                    latest_spigot = new Dataset(now, resources, purchases, updates, reviews, "spigot");
                    save(latest_spigot);

                    long delay = System.currentTimeMillis() - now;
                    System.out.println("Completed Refreshing Cycle in "+Math.round(TimeUnit.MILLISECONDS.toMinutes(delay))+" minutes!");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            if(latest_market == null || (System.currentTimeMillis() - latest_market.getTimeCreated()) > REFRESH_DELAY){
                try {
                    long now = System.currentTimeMillis();

                    Config config = Config.getInstance();
                    MarketBrowser parser = new MarketBrowser(config.getMarketUsername(), config.getMarketPassword(), true);

                    ResourcesList resources = parser.collectResources();
                    System.out.println("[1/4] Collected "+resources.size()+" Resources");

                    UpdatesList updates = parser.collectUpdates(resources);
                    System.out.println("[2/4] Collected "+updates.size()+" Updates");

                    ReviewsList reviews = parser.collectReviews(resources);
                    System.out.println("[3/4] Collected "+reviews.size()+" Reviews");

                    PurchasesList purchases = parser.collectPurchases(resources);
                    System.out.println("[4/4] Collected "+purchases.size()+" Purchases");

                    String spigotStatus = parser.getAPIStatus();
                    SpigotAPIServer.setSpigotStatus(spigotStatus);

                    parser.close();

                    latest_market = new Dataset(now, resources, purchases, updates, reviews, "market");
                    save(latest_market);

                    long delay = System.currentTimeMillis() - now;
                    System.out.println("Completed Refreshing Cycle in "+Math.round(TimeUnit.MILLISECONDS.toMinutes(delay))+" minutes!");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    public Dataset getDataset_spigot(){
        return latest_spigot;
    }

    public Dataset getDataset_market(){
        return latest_market;
    }
}
