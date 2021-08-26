package me.TechsCode.SpigotAPI.data;

import com.google.gson.JsonObject;
import me.TechsCode.SpigotAPI.data.lists.PurchasesList;
import me.TechsCode.SpigotAPI.data.lists.ReviewsList;
import me.TechsCode.SpigotAPI.data.lists.UpdatesList;
import me.TechsCode.SpigotAPI.server.Logger;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public class Resource extends JsonSerializable {

    private String id, name, tagLine, category, version;
    private Cost cost;
    private Time time;

    public Resource(String id, String name, String tagLine, String category, String version, Cost cost, Time time) {
        this.id = id;
        this.name = name;
        this.tagLine = tagLine;
        this.category = category;
        this.version = version;
        this.cost = cost;
        this.time = time;
    }

    public Resource(JsonObject state){
        setState(state);
    }

    @Override
    public void setState(JsonObject jsonObject) {
        this.id = jsonObject.get("id").getAsString();
        this.name = jsonObject.get("name").getAsString();
        this.tagLine = jsonObject.get("tagLine").getAsString();
        this.category = jsonObject.get("category").getAsString();
        this.version = jsonObject.get("version").getAsString();
        this.cost = jsonObject.has("cost") ? new Cost(jsonObject.getAsJsonObject("cost")) : null;
        this.time = new Time(jsonObject.getAsJsonObject("time"));
    }

    @Override
    public JsonObject getState() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", id);
        jsonObject.addProperty("name", name);
        jsonObject.addProperty("tagLine", tagLine);
        jsonObject.addProperty("category", category);
        jsonObject.addProperty("version", version);
        if(cost != null) jsonObject.add("cost", cost.toJsonObject());
        jsonObject.add("time", time.toJsonObject());

        return jsonObject;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getTagLine() {
        return tagLine;
    }

    public String getCategory() {
        return category;
    }

    public String getVersion() {
        return version;
    }

    public Optional<Cost> getCost() {
        return Optional.ofNullable(cost);
    }

    public Time getTime() {
        return time;
    }

    public boolean isPremium(){
        return cost != null;
    }

    public boolean isFree(){
        return cost == null;
    }

    public String getIcon() {
        try {
            int resourceId = Integer.parseInt(id);
            return String.format("https://www.spigotmc.org/data/resource_icons/%d/%d.jpg", (int) Math.floor(resourceId / 1000d), resourceId);
        } catch (NumberFormatException e) {
            Logger.send(e.getMessage(), true);
Logger.send(Arrays.toString(e.getStackTrace()), true);
            return "https://static.spigotmc.org/styles/spigot/xenresource/resource_icon.png";
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Resource resource = (Resource) o;
        return id.equals(resource.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public UpdatesList getUpdates() {
        return dataset.getUpdates().resource(id);
    }

    public ReviewsList getReviews() {
        return dataset.getReviews().resource(id);
    }

    public PurchasesList getPurchases() {
        return dataset.getPurchases().resource(id);
    }
}
