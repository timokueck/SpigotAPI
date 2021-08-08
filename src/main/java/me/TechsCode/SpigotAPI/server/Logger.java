package me.TechsCode.SpigotAPI.server;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.WebhookClientBuilder;

public class Logger {
    public static void send(String message, Boolean sendDiscord) {
        System.out.println(message);

        if(!sendDiscord)return;

        String url = Config.getInstance().getWebhookUrl();
        WebhookClientBuilder builder = new WebhookClientBuilder(url); // or id, token
        builder.setThreadFactory((job) -> {
            Thread thread = new Thread(job);
            thread.setName("Hello");
            thread.setDaemon(true);
            return thread;
        });
        builder.setWait(true);
        WebhookClient client = builder.build();

        client.send(message);

        client.close();
    }
}
