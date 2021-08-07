package me.TechsCode.SpigotAPI.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.TechsCode.SpigotAPI.data.lists.PurchasesList;
import me.TechsCode.SpigotAPI.data.lists.ResourcesList;
import me.TechsCode.SpigotAPI.data.lists.ReviewsList;
import me.TechsCode.SpigotAPI.data.lists.UpdatesList;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class Dataset {

    private final long timeCreated;

    private final ResourcesList resources;
    private final PurchasesList purchases;
    private final UpdatesList updates;
    private final ReviewsList reviews;
    private final String market;

    public Dataset(JsonObject jsonObject){
        this.resources = StreamSupport.stream(jsonObject.getAsJsonArray("resources").spliterator(), false)
                .map(state -> new Resource((JsonObject) state))
                .collect(Collectors.toCollection(ResourcesList::new));

        this.purchases = StreamSupport.stream(jsonObject.getAsJsonArray("purchases").spliterator(), false)
                .map(state -> new Purchase((JsonObject) state))
                .collect(Collectors.toCollection(PurchasesList::new));

        this.updates = StreamSupport.stream(jsonObject.getAsJsonArray("updates").spliterator(), false)
                .map(state -> new Update((JsonObject) state))
                .collect(Collectors.toCollection(UpdatesList::new));

        this.reviews = StreamSupport.stream(jsonObject.getAsJsonArray("reviews").spliterator(), false)
                .map(state -> new Review((JsonObject) state))
                .collect(Collectors.toCollection(ReviewsList::new));

        this.timeCreated = jsonObject.get("timeCreated").getAsLong();

        // Inject Dataset instance for cross references
        this.resources.forEach(x -> x.inject(this));
        this.purchases.forEach(x -> x.inject(this));
        this.updates.forEach(x -> x.inject(this));
        this.reviews.forEach(x -> x.inject(this));
        this.market = jsonObject.get("market").getAsString();
    }

    public Dataset(long timeCreated, ResourcesList resources, PurchasesList purchases, UpdatesList updates, ReviewsList reviews, String market) {
        this.timeCreated = timeCreated;
        this.resources = resources;
        this.purchases = purchases;
        this.updates = updates;
        this.reviews = reviews;
        this.market = market;
    }

    public ResourcesList getResources() {
        return resources;
    }

    public PurchasesList getPurchases() {
        return purchases;
    }

    public UpdatesList getUpdates() {
        return updates;
    }

    public ReviewsList getReviews() {
        return reviews;
    }

    public String getMarket() {
        return market;
    }

    public long getTimeCreated() {
        return timeCreated;
    }

    public JsonObject toJsonObject(){
        JsonObject jsonObject = new JsonObject();

        JsonArray resources = new JsonArray();
        JsonArray purchases = new JsonArray();
        JsonArray updates = new JsonArray();
        JsonArray reviews = new JsonArray();

        this.resources.stream().map(Resource::getState).forEach(resources::add);
        this.purchases.stream().map(Purchase::getState).forEach(purchases::add);
        this.updates.stream().map(Update::getState).forEach(updates::add);
        this.reviews.stream().map(Review::getState).forEach(reviews::add);

        jsonObject.add("resources", resources);
        jsonObject.add("purchases", purchases);
        jsonObject.add("updates", updates);
        jsonObject.add("reviews", reviews);
        jsonObject.addProperty("timeCreated", timeCreated);
        jsonObject.addProperty("market", market);

        return jsonObject;
    }
}
