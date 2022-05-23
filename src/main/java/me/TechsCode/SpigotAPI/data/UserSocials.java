package me.TechsCode.SpigotAPI.data;

import com.google.gson.JsonObject;

import java.util.Objects;

public class UserSocials extends JsonSerializable {

    private User user;
    private String discord;

    public UserSocials(User user, String discord) {
        this.user = user;
        this.discord = discord;
    }

    public UserSocials(JsonObject state){
        setState(state);
    }

    public User getUser() {
        return user;
    }

    public String getDiscord() {
        return discord;
    }

    @Override
    public void setState(JsonObject jsonObject) {
        this.user = new User(jsonObject.getAsJsonObject("user"));
        this.discord = jsonObject.get("discord").getAsString();
    }

    @Override
    public JsonObject getState() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("user", user.toJsonObject());
        jsonObject.addProperty("discord", discord);

        return jsonObject;
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, discord);
    }

}