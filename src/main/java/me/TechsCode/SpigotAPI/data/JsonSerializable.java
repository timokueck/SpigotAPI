package me.TechsCode.SpigotAPI.data;

import com.google.gson.JsonObject;

public abstract class JsonSerializable {

    protected Dataset dataset;


    public void inject(Dataset dataset){
        this.dataset = dataset;
    }

    public abstract void setState(JsonObject jsonObject);

    public abstract JsonObject getState();

}
