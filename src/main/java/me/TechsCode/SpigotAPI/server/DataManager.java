package me.TechsCode.SpigotAPI.server;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.TechsCode.SpigotAPI.data.Dataset;
import me.TechsCode.SpigotAPI.data.Resource;
import me.TechsCode.SpigotAPI.data.lists.PurchasesList;
import me.TechsCode.SpigotAPI.server.browsers.SpigotBrowser;
import me.TechsCode.SpigotAPI.server.browsers.VirtualBrowser;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class DataManager extends Thread {

    private static final long REFRESH_DELAY = TimeUnit.MINUTES.toMillis(Config.getInstance().getRefreshDelay());

    private Dataset latest;

    private boolean parseDone = true;
    private boolean fetching = false;

    public DataManager() {
        this.latest = load();

        start();
    }

    private void save(Dataset dataset) {
        Logger.send("Saved ", true);
        File file = new File("data/lastDataset.json");

        if (file.exists()) file.delete();

        String json = dataset.toJsonObject().toString();

        try {
            FileUtils.writeStringToFile(file, json, Charset.defaultCharset());
        } catch (IOException e) {
            Logger.send(e.getMessage(), true);
            Logger.send(Arrays.toString(e.getStackTrace()), true);
        }
    }

    private Dataset load() {
        File file = new File("data/lastDataset.json");

        if (!file.exists()) return null;

        try {
            String json = FileUtils.readFileToString(file, Charset.defaultCharset());

            JsonParser parser = new JsonParser();
            JsonObject jsonObject = (JsonObject) parser.parse(json);

            return new Dataset(jsonObject);
        } catch (IOException e) {
            Logger.send(e.getMessage(), true);
            Logger.send(Arrays.toString(e.getStackTrace()), true);
            return null;
        }
    }

    @Override
    public void run() {
        while (true) {
            if ((latest == null || (System.currentTimeMillis() - latest.getTimeCreated()) > REFRESH_DELAY) && parseDone) {
                parseDone = false;
                fetching = true;
                Config config = Config.getInstance();
                long now = System.currentTimeMillis();

                SpigotBrowser parser = null;
                try {
                    VirtualBrowser.enableSpigotPreload();
                    parser = new SpigotBrowser(config.getSpigotUsername(), config.getSpigotPassword(), config.getSpigotUserId(), true);
                } catch (Exception e) {
                    Logger.send(e.getMessage(), true);
                    Logger.send(Arrays.toString(e.getStackTrace()), true);
                    parseDone = true;
                }

                try {
                    if (parser != null) {

                        List<Resource> resources = Resource.getAllResources();

                        PurchasesList purchases = parser.collectPurchases(resources);
                        Logger.send("Collected " + purchases.size() + " Purchases on SpigotMC", true);
                        if (purchases.isEmpty()) {
                            purchases = HttpRouter.getDataManager().latest.getPurchases();
                        }

                        parser.close();

                        latest = new Dataset(now, purchases);
                        save(latest);

                        long delay = System.currentTimeMillis() - now;
                        Logger.send("Completed SpigotMC Refreshing Cycle in " + Math.round(TimeUnit.MILLISECONDS.toMinutes(delay)) + " minutes!", true);
                    } else {
                        Logger.send("Failed SpigotMC Refreshing Cycle", true);
                    }
                    parseDone = true;
                } catch (Exception e) {
                    Logger.send(e.getMessage(), true);
                    Logger.send(Arrays.toString(e.getStackTrace()), true);
                    if (parser != null)
                        parser.close();
                    parseDone = true;
                }
                fetching = false;
            }
        }
    }

    public Dataset getDataset() {
        return latest;
    }

    public boolean isFetching() {
        return this.fetching;
    }
}
