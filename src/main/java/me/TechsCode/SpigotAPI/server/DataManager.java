package me.TechsCode.SpigotAPI.server;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.TechsCode.SpigotAPI.data.Dataset;
import me.TechsCode.SpigotAPI.data.Resource;
import me.TechsCode.SpigotAPI.data.UserVerification;
import me.TechsCode.SpigotAPI.data.UserVerificationStatus;
import me.TechsCode.SpigotAPI.data.lists.PurchasesList;
import me.TechsCode.SpigotAPI.data.lists.VerificationsList;
import me.TechsCode.SpigotAPI.server.browsers.SpigotBrowser;
import me.TechsCode.SpigotAPI.server.browsers.SpigotVerifyBrowser;
import me.TechsCode.SpigotAPI.server.browsers.VirtualBrowser;
import me.TechsCode.SpigotAPI.server.browsers.VirtualVerifyBrowser;
import org.apache.commons.io.FileUtils;
import org.json.JSONObject;

import javax.xml.crypto.Data;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class DataManager extends Thread {

    private static final long REFRESH_DELAY = TimeUnit.MINUTES.toMillis(Config.getInstance().getRefreshDelay());

    private static final VerificationsList verificationQueue = new VerificationsList();

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
        runVerification();
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

    public void runVerification(){
        Thread t1 = new Thread(() -> {
            Logger.send("Started user verification thread", false);
            while(true){
                VerificationsList verificationQueue = DataManager.getVerificationQueue().verificationChecked(false).minutesPast(5);
                Optional<UserVerification> optionalUserVerification = verificationQueue.stream().findFirst();

                if (optionalUserVerification.isPresent()) {
                    UserVerification verification = optionalUserVerification.get();
                    Logger.send("Verifying " + verification.getUserId(), false);

                    Config config = Config.getInstance();
                    long now = System.currentTimeMillis();

                    SpigotVerifyBrowser parser = null;
                    try {
                        VirtualVerifyBrowser.enableSpigotPreload();
                        parser = new SpigotVerifyBrowser(config.getSpigotUsername(), config.getSpigotPassword(), config.getSpigotUserId(), false);
                    } catch (Exception e) {
                        Logger.send(e.getMessage(), true);
                        Logger.send(Arrays.toString(e.getStackTrace()), true);
                    }

                    try {
                        if (parser != null) {
                            parser.navigateToUserProfile(verification.getUserId());
                            String username = parser.getUsername();
                            JsonArray posts = parser.collectPosts(username);

                            parser.navigateToUserProfileInfo(verification.getUserId());
                            String discord = parser.getDiscord();
                            verification.setDiscord(discord);

                            parser.close();

                            for (JsonElement post : posts) {
                                String content = post.getAsString();
//                                Logger.send("User Post: "+content, false);
                                if (content.equals(verification.getCode())) {
                                    verification.setVerified(true);
                                    verification.setVerificationStatus(UserVerificationStatus.VERIFIED);
                                    long delay = System.currentTimeMillis() - now;
                                    Logger.send("Completed SpigotMC User Verification in " + Math.round(TimeUnit.MILLISECONDS.toMinutes(delay)) + " minutes!", true);
                                    break;
                                } else {
                                    verification.setVerified(true);
                                    verification.setVerificationStatus(UserVerificationStatus.POST_NOT_FOUND);
                                    Logger.send("Failed SpigotMC User Verification", true);
                                }
                            }

                            Logger.send("Fetched " + posts.size() + " Posts!", false);
                        } else {
                            Logger.send("Failed SpigotMC User Verification", true);
                        }
                    } catch (Exception e) {
                        Logger.send(e.getMessage(), true);
                        Logger.send(Arrays.toString(e.getStackTrace()), true);
                        if (parser != null)
                            parser.close();
                    }
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        t1.start();
    }

    public static VerificationsList getVerificationQueue() {
        return verificationQueue;
    }

    public static void addVerification(UserVerification verification) {
        verificationQueue.add(verification);
    }

    public static void removeVerification(UserVerification verification) {
        verificationQueue.remove(verification);
    }

    public Dataset getDataset() {
        return latest;
    }

    public boolean isFetching() {
        return this.fetching;
    }
}
