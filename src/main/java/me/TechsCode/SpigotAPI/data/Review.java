package me.TechsCode.SpigotAPI.data;

import com.google.gson.JsonObject;

public class Review extends JsonSerializable {

    private String resourceId;
    private User user;
    private String text;
    private int rating;
    private Time time;

    public Review(String resourceId, User user, String text, int rating, Time time) {
        this.resourceId = resourceId;
        this.user = user;
        this.text = text;
        this.rating = rating;
        this.time = time;
    }

    public Review(JsonObject state){
        setState(state);
    }

    @Override
    public void setState(JsonObject jsonObject) {
        this.resourceId = jsonObject.get("resourceId").getAsString();
        this.user = new User(jsonObject.getAsJsonObject("user"));
        this.text = jsonObject.get("text").getAsString();
        this.rating = jsonObject.get("rating").getAsInt();
        this.time = new Time(jsonObject.getAsJsonObject("time"));
    }

    @Override
    public JsonObject getState() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("resourceId", resourceId);
        jsonObject.add("user", user.toJsonObject());
        jsonObject.addProperty("text", text);
        jsonObject.addProperty("rating", rating);
        jsonObject.add("time", time.toJsonObject());
        return jsonObject;
    }

    public Resource getResource() {
        return dataset.getResources().id(resourceId).orElse(null);
    }

    public User getUser() {
        return user;
    }

    public String getText() {
        return text;
    }

    public int getRating() {
        return rating;
    }

    public Time getTime() {
        return time;
    }
}