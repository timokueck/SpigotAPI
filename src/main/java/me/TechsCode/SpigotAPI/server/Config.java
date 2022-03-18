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
        return !getSpigotUsername().equals("someuser") || !getSpigotUserId().equals("someid") || !getWebhookUrl().equals("someurl");
    }

    public String getSpigotUsername(){
        return root.get("username").getAsString();
    }

    public String getSpigotPassword(){
        return root.get("password").getAsString();
    }

    public String getSpigotUserId(){
        return root.get("userId").getAsString();
    }

    public String get2FAToken() {
        return root.get("2faToken").getAsString();
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

    public int getRefreshDelay(){
        return root.get("refreshDelay").getAsInt();
    }

}
