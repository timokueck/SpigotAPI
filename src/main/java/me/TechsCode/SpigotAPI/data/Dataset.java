package me.TechsCode.SpigotAPI.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.TechsCode.SpigotAPI.data.lists.PurchasesList;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class Dataset {

    private final long timeCreated;

    private final PurchasesList purchases;

    public Dataset(JsonObject jsonObject){
        this.purchases = StreamSupport.stream(jsonObject.getAsJsonArray("purchases").spliterator(), false)
                .map(state -> new Purchase((JsonObject) state))
                .collect(Collectors.toCollection(PurchasesList::new));

        this.timeCreated = jsonObject.get("timeCreated").getAsLong();

        this.purchases.forEach(x -> x.inject(this));
    }

    public Dataset(long timeCreated, PurchasesList purchases) {
        this.timeCreated = timeCreated;
        this.purchases = purchases;
    }

    public PurchasesList getPurchases() {
        return purchases;
    }

    public long getTimeCreated() {
        return timeCreated;
    }

    public JsonObject toJsonObject(){
        JsonObject jsonObject = new JsonObject();

        JsonArray purchases = new JsonArray();

        this.purchases.stream().map(Purchase::getState).forEach(purchases::add);

        jsonObject.add("purchases", purchases);
        jsonObject.addProperty("timeCreated", timeCreated);

        return jsonObject;
    }
}
