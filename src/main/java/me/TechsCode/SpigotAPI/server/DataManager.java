package me.TechsCode.SpigotAPI.server;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.TechsCode.SpigotAPI.data.Dataset;
import me.TechsCode.SpigotAPI.data.lists.PurchasesList;
import me.TechsCode.SpigotAPI.data.lists.ResourcesList;
import me.TechsCode.SpigotAPI.data.lists.ReviewsList;
import me.TechsCode.SpigotAPI.data.lists.UpdatesList;
import me.TechsCode.SpigotAPI.server.spigot.SpigotBrowser;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

public class DataManager extends Thread {

    private static final long REFRESH_DELAY = TimeUnit.MINUTES.toMillis(15);

    private Dataset latest;

    public DataManager() {
        this.latest = load();

        start();
    }

    private void save(Dataset dataset){
        File file = new File("lastDataset.json");

        if(file.exists()) file.delete();

        String json = dataset.toJsonObject().toString();

        try {
            FileUtils.writeStringToFile(file, json, Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Dataset load(){
        File file = new File("lastDataset.json");

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
            if(latest == null || (System.currentTimeMillis() - latest.getTimeCreated()) > REFRESH_DELAY){
                try {
                    long now = System.currentTimeMillis();

                    Config config = Config.getInstance();
                    SpigotBrowser parser = new SpigotBrowser(config.getSpigotUsername(), config.getSpigotPassword());

                    ResourcesList resources = parser.collectResources();
                    System.out.println("[1/4] Collected "+resources.size()+" Resources");

                    UpdatesList updates = parser.collectUpdates(resources);
                    System.out.println("[2/4] Collected "+updates.size()+" Updates");

                    ReviewsList reviews = parser.collectReviews(resources);
                    System.out.println("[3/4] Collected "+reviews.size()+" Reviews");

                    PurchasesList purchases = parser.collectPurchases(resources);
                    System.out.println("[4/4] Collected "+purchases.size()+" Purchases");

                    parser.close();

                    latest = new Dataset(now, resources, purchases, updates, reviews);
                    save(latest);

                    long delay = System.currentTimeMillis() - now;
                    System.out.println("Completed Refreshing Cycle in "+Math.round(TimeUnit.MILLISECONDS.toMinutes(delay))+" minutes!");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public Dataset getDataset(){
        return latest;
    }
}
