package me.TechsCode.SpigotAPI.data;

import com.google.gson.JsonObject;

import java.util.Objects;

public class Review extends JsonSerializable {

    private String id, text, resourceId;
    private User user;
    private int rating;
    private Time time;

    public Review(String id, String resourceId, User user, String text, int rating, Time time) {
        this.id = id;
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
        this.id = jsonObject.get("id").getAsString();
        this.time = new Time(jsonObject.getAsJsonObject("time"));
    }

    @Override
    public JsonObject getState() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", id);
        jsonObject.addProperty("resourceId", resourceId);
        jsonObject.add("user", user.toJsonObject());
        jsonObject.addProperty("text", text);
        jsonObject.addProperty("rating", rating);
        jsonObject.add("time", time.toJsonObject());

        return jsonObject;
    }

    public String getId() {
        return id;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Review review = (Review) o;
        return id.equals(review.id) &&
                resourceId.equals(review.resourceId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(resourceId);
    }
}