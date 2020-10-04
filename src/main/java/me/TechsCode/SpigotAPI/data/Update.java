package me.TechsCode.SpigotAPI.data;

import com.google.gson.JsonObject;

import java.util.Arrays;
import java.util.Objects;

public class Update extends JsonSerializable {

    private String id;
    private String resourceId;
    private String title;
    private String[] images;
    private String description;
    private Time time;

    public Update(String id, String resourceId, String title, String[] images, String description, Time time) {
        this.id = id;
        this.resourceId = resourceId;
        this.title = title;
        this.images = images;
        this.description = description;
        this.title = title;
        this.time = time;
    }

    public Update(JsonObject state){
        this.setState(state);
    }

    @Override
    public void setState(JsonObject jsonObject) {
        this.id = jsonObject.get("id").getAsString();
        this.resourceId = jsonObject.get("resourceId").getAsString();
        this.title = jsonObject.get("title").getAsString();
        this.images = Arrays.stream(jsonObject.get("images").getAsString().split(";")).filter(x -> !x.isEmpty()).toArray(String[]::new);
        this.description = jsonObject.get("description").getAsString();
        this.time = new Time(jsonObject.getAsJsonObject("time"));
    }

    @Override
    public JsonObject getState() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", id);
        jsonObject.addProperty("resourceId", resourceId);
        jsonObject.addProperty("title", title);
        jsonObject.addProperty("images", String.join(";", images));
        jsonObject.addProperty("description", description);
        jsonObject.add("time", time.toJsonObject());

        return jsonObject;
    }

    public Resource getResource(){
        return dataset.getResources().id(resourceId).orElse(null);
    }

    public String getId() {
        return id;
    }

    public String getResourceId() {
        return resourceId;
    }

    public String getTitle() {
        return title;
    }

    public String[] getImages() {
        return images;
    }

    public String getDescription() {
        return description;
    }

    public Time getTime() {
        return time;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Update update = (Update) o;
        return id.equals(update.id) &&
                resourceId.equals(update.resourceId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, resourceId);
    }
}