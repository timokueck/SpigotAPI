package me.TechsCode.SpigotAPI.server;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.TechsCode.SpigotAPI.data.Dataset;
import me.TechsCode.SpigotAPI.data.lists.PurchasesList;
import me.TechsCode.SpigotAPI.data.lists.ResourcesList;
import me.TechsCode.SpigotAPI.data.lists.ReviewsList;
import me.TechsCode.SpigotAPI.data.lists.UpdatesList;
import me.TechsCode.SpigotAPI.server.browsers.MarketBrowser;
import me.TechsCode.SpigotAPI.server.browsers.SpigotBrowser;
import me.TechsCode.SpigotAPI.server.browsers.VirtualBrowser;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class DataManager extends Thread {

    private static final long SPIGOT_REFRESH_DELAY = TimeUnit.MINUTES.toMillis(Config.getInstance().getSpigotRefreshDelay());
    private static final long MARKET_REFRESH_DELAY = TimeUnit.MINUTES.toMillis(Config.getInstance().getMarketRefreshDelay());

    private Dataset latest_spigot;
    private Dataset latest_market;

    private boolean spigotParseDone = true;
    private boolean marketParseDone = true;

    public DataManager() {
        this.latest_spigot = load("spigot");
        this.latest_market = load("market");

        start();
    }

    private void save(Dataset dataset) {
        Logger.send("Saved "+dataset.getMarket(), true);
        File file = new File("data/lastDataset_" + dataset.getMarket() + ".json");

        if (file.exists()) file.delete();

        String json = dataset.toJsonObject().toString();

        try {
            FileUtils.writeStringToFile(file, json, Charset.defaultCharset());
        } catch (IOException e) {
            Logger.send(Arrays.toString(e.getStackTrace()), true);
        }
    }

    private Dataset load(String market) {
        File file = new File("data/lastDataset_" + market + ".json");

        if (!file.exists()) return null;

        try {
            String json = FileUtils.readFileToString(file, Charset.defaultCharset());

            JsonParser parser = new JsonParser();
            JsonObject jsonObject = (JsonObject) parser.parse(json);

            return new Dataset(jsonObject);
        } catch (IOException e) {
            Logger.send(Arrays.toString(e.getStackTrace()), true);
            return null;
        }
    }

    @Override
    public void run() {
        while (true) {
            if ((latest_spigot == null || (System.currentTimeMillis() - latest_spigot.getTimeCreated()) > SPIGOT_REFRESH_DELAY) && spigotParseDone) {
                spigotParseDone = false;
                Config config = Config.getInstance();
                long now = System.currentTimeMillis();

                SpigotBrowser parser = null;
                try{
                    VirtualBrowser.enableSpigotPreload();
                    parser = new SpigotBrowser(config.getSpigotUsername(), config.getSpigotPassword(), config.getSpigotUserId(), true);
                }catch (InterruptedException e){
                    Logger.send(Arrays.toString(e.getStackTrace()), true);
                    spigotParseDone = true;
                }

                try {
                    if(parser != null){
                        ResourcesList resources = parser.collectResources();
                        Logger.send("[1/4] Collected " + resources.size() + " Resources on SpigotMC", false);

                        UpdatesList updates = parser.collectUpdates(resources);
                        Logger.send("[2/4] Collected " + updates.size() + " Updates on SpigotMC", false);

                        ReviewsList reviews = parser.collectReviews(resources);
                        Logger.send("[3/4] Collected " + reviews.size() + " Reviews on SpigotMC", false);

                        PurchasesList purchases = parser.collectPurchases(resources);
                        Logger.send("[4/4] Collected " + purchases.size() + " Purchases on SpigotMC", false);

                        parser.close();

                        latest_spigot = new Dataset(now, resources, purchases, updates, reviews, "spigot");
                        save(latest_spigot);

                        long delay = System.currentTimeMillis() - now;
                        Logger.send("Completed SpigotMC Refreshing Cycle in " + Math.round(TimeUnit.MILLISECONDS.toMinutes(delay)) + " minutes!", true);
                    }else{
                        Logger.send("Failed SpigotMC Refreshing Cycle", true);
                    }
                    spigotParseDone = true;
                } catch (InterruptedException e) {
                    Logger.send(Arrays.toString(e.getStackTrace()), true);
                    if(parser != null)
                        parser.close();
                    spigotParseDone = true;
                }
            }

            if ((latest_market == null || (System.currentTimeMillis() - latest_market.getTimeCreated()) > MARKET_REFRESH_DELAY) && marketParseDone) {
                marketParseDone = false;
                long now = System.currentTimeMillis();

                MarketBrowser parser = null;
                try{
                    Config config = Config.getInstance();
                    VirtualBrowser.enableMarketPreload();
                    parser = new MarketBrowser(config.getMarketUsername(), config.getMarketPassword(), config.getMarketUserId(), true);
                }catch (InterruptedException e){
                    Logger.send(Arrays.toString(e.getStackTrace()), true);
                    marketParseDone = true;
                }

                try {
                    if(parser != null) {
                        ResourcesList resources = parser.collectResources();
                        Logger.send("[1/4] Collected " + resources.size() + " Resources on MC-Market", false);

                        UpdatesList updates = parser.collectUpdates(resources);
                        Logger.send("[2/4] Collected " + updates.size() + " Updates on MC-Market", false);

                        ReviewsList reviews = parser.collectReviews(resources);
                        Logger.send("[3/4] Collected " + reviews.size() + " Reviews on MC-Market", false);

                        PurchasesList purchases = new PurchasesList();
                        //PurchasesList purchases = parser.collectPurchases(resources);
                        //Logger.send("[4/4] Collected " + purchases.size() + " Purchases on MC-Market", false);

                        parser.close();

                        latest_market = new Dataset(now, resources, purchases, updates, reviews, "market");
                        save(latest_market);

                        long delay = System.currentTimeMillis() - now;
                        Logger.send("Completed MC-Market Refreshing Cycle in " + Math.round(TimeUnit.MILLISECONDS.toMinutes(delay)) + " minutes!", true);
                    }else{
                        Logger.send("Failed MC-Market Refreshing Cycle", true);
                    }
                    marketParseDone = true;
                } catch (InterruptedException e) {
                    Logger.send(Arrays.toString(e.getStackTrace()), true);
                    if(parser != null)
                        parser.close();
                    marketParseDone = true;
                }
            }

        }
    }

    public Dataset getDataset_spigot() {
        return latest_spigot;
    }

    public Dataset getDataset_market() {
        return latest_market;
    }
}
