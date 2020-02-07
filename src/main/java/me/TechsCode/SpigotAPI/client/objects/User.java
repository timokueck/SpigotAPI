package me.TechsCode.SpigotAPI.client.objects;

import me.TechsCode.SpigotAPI.client.SpigotAPIClient;
import me.TechsCode.SpigotAPI.client.collections.PurchaseCollection;
import me.TechsCode.SpigotAPI.client.collections.ReviewCollection;

public class User {

    private SpigotAPIClient client;
    private String userId, username;

    public User(SpigotAPIClient client, String userId, String username) {
        this.client = client;
        this.userId = userId;
        this.username = username;
    }

    public ReviewCollection getReviews(){
        return client.getReviews().userId(userId);
    }

    public PurchaseCollection getPurchases(){
        return client.getPurchases().userId(userId);
    }

    public String getUsername() {
        return username;
    }
}
