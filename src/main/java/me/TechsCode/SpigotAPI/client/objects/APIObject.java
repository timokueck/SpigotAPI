package me.TechsCode.SpigotAPI.client.objects;

import me.TechsCode.SpigotAPI.client.SpigotAPIClient;
import org.json.simple.JSONObject;

public class APIObject {

    protected SpigotAPIClient client;
    private JSONObject jsonObject;

    public APIObject(SpigotAPIClient client, JSONObject jsonObject) {
        this.client = client;
        this.jsonObject = jsonObject;
    }

    protected Object getProperty(String property){
        return jsonObject.get(property);
    }

    protected String getStringProperty(String property){
        return (String) jsonObject.get(property);
    }

    protected long getLongProperty(String property){
        return (long) jsonObject.get(property);
    }

    protected double getDoubleProperty(String property){
        return (double) jsonObject.get(property);
    }

}
