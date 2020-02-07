package me.TechsCode.SpigotAPI.client.objects;

import me.TechsCode.SpigotAPI.client.SpigotAPIClient;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.Arrays;

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

    protected String[] getStringArrayProperty(String property){
        return Arrays.stream((JSONArray[]) jsonObject.get("images")).map(x -> x.toString()).toArray(String[]::new);
    }

    protected long getLongProperty(String property){
        return (long) jsonObject.get(property);
    }

    protected double getDoubleProperty(String property){
        return (double) jsonObject.get(property);
    }

}
