package me.TechsCode.SpigotAPI.data;

import com.google.gson.JsonObject;
import me.TechsCode.SpigotAPI.data.lists.PurchasesList;
import me.TechsCode.SpigotAPI.data.lists.ReviewsList;
import me.TechsCode.SpigotAPI.data.lists.UpdatesList;

import java.util.Objects;
import java.util.Optional;

public class Post extends JsonSerializable {

    private String user, message, date;

    public Post(String user, String message, String date) {
        this.user = user;
        this.message = message;
        this.date = date;
    }

    public Post(JsonObject state){
        setState(state);
    }

    @Override
    public void setState(JsonObject jsonObject) {
        this.user = jsonObject.get("id").getAsString();
        this.message = jsonObject.get("name").getAsString();
        this.date = jsonObject.get("tagLine").getAsString();
    }

    @Override
    public JsonObject getState() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("user", user);
        jsonObject.addProperty("message", message);
        jsonObject.addProperty("date", date);

        return jsonObject;
    }

    public String getUser() {
        return user;
    }

    public String getMessage() {
        return message;
    }

    public String getDate() {
        return date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Post resource = (Post) o;
        return user.equals(resource.user);
    }
}
