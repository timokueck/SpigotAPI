package me.TechsCode.SpigotAPI.server;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.WebhookClientBuilder;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {

    private static String getCurrentDateTime(){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        return "<"+dtf.format(now)+"> ";
    }

    public static void send(String message, Boolean sendDiscord) {
        System.out.println(getCurrentDateTime() + message);

        saveToFile(message);

        if(!sendDiscord){
            sendDiscord(message);
        }
    }

    public static void sendDiscord(String message){
        String url = Config.getInstance().getWebhookUrl();
        if(url.equals("someurl"))return;
        if(!url.startsWith("https://discord.com/api/webhooks/")){
            System.err.println(getCurrentDateTime() + "Invalid Discord webhook url");
            saveToFile(getCurrentDateTime() + "Invalid Discord webhook url");
            return;
        }

        WebhookClientBuilder builder = new WebhookClientBuilder(url); // or id, token
        builder.setThreadFactory((job) -> {
            Thread thread = new Thread(job);
            thread.setDaemon(true);
            return thread;
        });
        builder.setWait(true);
        WebhookClient client = builder.build();
        client.send(getCurrentDateTime() + " > " + message);
        client.close();
    }

    public static void saveToFile(String message){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime now = LocalDateTime.now();
        String currentDate = dtf.format(now);

        String path = "data/logs/";

        File file = new File(path);
        if(!file.exists()){
            try{
                file.mkdir();
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        try{
            FileWriter fw = new FileWriter(path+"/"+currentDate+".log", true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(getCurrentDateTime() + message);
            bw.newLine();
            bw.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
