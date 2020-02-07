package me.TechsCode.SpigotAPI.client.objects;

import me.TechsCode.SpigotAPI.client.SpigotAPIClient;
import org.json.simple.JSONObject;

public class Purchase extends APIObject {

    public Purchase(SpigotAPIClient client, JSONObject jsonObject) {
        super(client, jsonObject);
    }

    public Resource getResource(){
        return client.getResources().id(getResourceId()).get();
    }

    public User getUser(){
        return new User(client, getUserId(), getUsername());
    }

    public String getPurchaseId() {
        return getStringProperty("purchaseId");
    }

    public String getResourceId() {
        return getStringProperty("resourceId");
    }

    public String getResourceName() {
        return getStringProperty("resourceName");
    }

    public String getUserId() {
        return getStringProperty("userId");
    }

    public String getUsername() {
        return getStringProperty("username");
    }

    public Time getTime() {
        return new Time(getStringProperty("time"), getLongProperty("unixTime"));
    }

    public Cost getCost() {
        return new Cost(getDoubleProperty("costValue"), "EUR");
    }}
