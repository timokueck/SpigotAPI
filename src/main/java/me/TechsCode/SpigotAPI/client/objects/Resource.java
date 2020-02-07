package me.TechsCode.SpigotAPI.client.objects;

import me.TechsCode.SpigotAPI.client.SpigotAPIClient;
import me.TechsCode.SpigotAPI.client.collections.PurchaseCollection;
import me.TechsCode.SpigotAPI.client.collections.ReviewCollection;
import me.TechsCode.SpigotAPI.client.collections.UpdateCollection;
import org.json.simple.JSONObject;

public class Resource extends APIObject {

    public Resource(SpigotAPIClient client, JSONObject jsonObject) {
        super(client, jsonObject);
    }

    public String getResourceId() {
        return getStringProperty("resourceId");
    }

    public String getResourceName() {
        return getStringProperty("resourceName");
    }

    public String getSubTitle() {
        return getStringProperty("subTitle");
    }

    public String getCategory() {
        return getStringProperty("category");
    }

    public String getIcon() {
        return getStringProperty("icon");
    }

    public Cost getCost() {
        return new Cost(getDoubleProperty("costValue"), "EUR");
    }

    public Time getTime() {
        return new Time(getStringProperty("time"), getLongProperty("unixTime"));
    }

    public UpdateCollection getUpdates(){
        return client.getUpdates().resourceId(getResourceId());
    }

    public ReviewCollection getReviews(){
        return client.getReviews().resourceId(getResourceId());
    }

    public PurchaseCollection getPurchases(){
        return client.getPurchases().resourceId(getResourceId());
    }

    public boolean isPremium(){
        return getCategory().equalsIgnoreCase("premium");
    }
}
