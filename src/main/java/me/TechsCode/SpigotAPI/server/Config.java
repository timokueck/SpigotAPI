package me.TechsCode.SpigotAPI.server;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;

public class Config {

    private static Config instance;

    public static Config getInstance(){
        if(instance == null){
            instance = new Config();
        }

        return instance;
    }

    private JsonObject root;

    private Config() {
        File file = new File("config.json");

        if(!file.exists()){
            try {
                InputStream src = Config.class.getResourceAsStream("/config.json");
                Files.copy(src, Paths.get(file.toURI()), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                Logger.send(e.getMessage(), true);
                Logger.send(Arrays.toString(e.getStackTrace()), true);
            }
        }

        try {
            String json = FileUtils.readFileToString(file, StandardCharsets.UTF_8);

            JsonParser jsonParser = new JsonParser();
            root = (JsonObject) jsonParser.parse(json);
        } catch (IOException e) {
            Logger.send(e.getMessage(), true);
            Logger.send(Arrays.toString(e.getStackTrace()), true);
        }
    }

    public boolean isConfigured(){
        return !getSpigotUsername().equals("someuser") || !getMarketUsername().equals("someuser") || !getMarketUserId().equals("someid") || !getSpigotUserId().equals("someid") || !getWebhookUrl().equals("someurl");
    }

    public String getSpigotUsername(){
        return root.get("spigotUsername").getAsString();
    }

    public String getSpigotPassword(){
        return root.get("spigotPassword").getAsString();
    }

    public String getSpigotUserId(){
        return root.get("spigotUserId").getAsString();
    }

    public String getMarketUsername(){
        return root.get("marketUsername").getAsString();
    }

    public String getMarketPassword(){
        return root.get("marketPassword").getAsString();
    }

    public String getMarketUserId(){
        return root.get("marketUserId").getAsString();
    }

    public String getToken(){
        return root.get("token").getAsString();
    }

    public String getWebhookUrl(){
        return root.get("discordWebhookUrl").getAsString();
    }

    public int getPort(){
        return root.get("port").getAsInt();
    }

    public int getManagerPort(){
        return root.get("managerPort").getAsInt();
    }

    public int getSpigotRefreshDelay(){
        return root.get("spigotRefreshDelay").getAsInt();
    }

    public int getMarketRefreshDelay(){
        return root.get("marketRefreshDelay").getAsInt();
    }

}
