package me.TechsCode.SpigotAPI.client;

import me.TechsCode.SpigotAPI.data.Dataset;
import me.TechsCode.SpigotAPI.data.lists.PurchasesList;
import me.TechsCode.SpigotAPI.data.lists.ResourcesList;
import me.TechsCode.SpigotAPI.data.lists.ReviewsList;
import me.TechsCode.SpigotAPI.data.lists.UpdatesList;

import java.util.Optional;

public class SpigotAPIClient extends Thread {

    private final DataManager dataManager;

    public SpigotAPIClient(String url, String token) {
        this.dataManager = new DataManager(url, token);
    }

    public Optional<Dataset> getData() {
        return Optional.ofNullable(dataManager.getData());
    }

    public long getLastParsed() {
        return dataManager.getLastParsed();
    }

    public long getRefreshTime() {
        return getData().map(Dataset::getTimeCreated).orElse(0L);
    }

    public ResourcesList getResources() {
        return getData().map(Dataset::getResources).orElse(new ResourcesList());
    }

    public UpdatesList getUpdates() {
        return getData().map(Dataset::getUpdates).orElse(new UpdatesList());
    }

    public ReviewsList getReviews() {
        return getData().map(Dataset::getReviews).orElse(new ReviewsList());
    }

    public PurchasesList getPurchases() {
        return getData().map(Dataset::getPurchases).orElse(new PurchasesList());
    }
}
