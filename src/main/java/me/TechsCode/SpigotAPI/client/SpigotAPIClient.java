package me.TechsCode.SpigotAPI.client;

import me.TechsCode.SpigotAPI.data.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SpigotAPIClient extends Thread {

    private final DataManager dataManager;

    public SpigotAPIClient(String url, String token){
        this.dataManager = new DataManager(url, token);
    }

    public Optional<Dataset> getData(){
        return Optional.ofNullable(dataManager.getData());
    }

    public long getRefreshTime(){
        return getData().map(Dataset::getTimeCreated).orElse(0L);
    }

    public List<Resource> getResources(){
        return getData().map(Dataset::getResources).orElse(new ArrayList<>());
    }

    public List<Update> getUpdates(){
        return getData().map(Dataset::getUpdates).orElse(new ArrayList<>());
    }

    public List<Review> getReviews(){
        return getData().map(Dataset::getReviews).orElse(new ArrayList<>());
    }

    public List<Purchase> getPurchases(){
        return getData().map(Dataset::getPurchases).orElse(new ArrayList<>());
    }
}
