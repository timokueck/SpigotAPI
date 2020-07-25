package me.TechsCode.SpigotAPI.server.data;

import org.json.simple.JSONObject;

import java.time.Month;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Entry {

    private Map<String, Object> data;

    public Entry() {
        data = new HashMap<>();
    }

    public Entry set(String key, Object value){
        data.put(key, value);
        return this;
    }

    public Entry setCost(String costString){
        if(costString == null) return this;

        double value = Double.parseDouble(costString.split(" ")[0]);
        String currency = costString.split(" ")[1];

        set("costValue", value);
        set("costCurrency", currency);
        return this;
    }

    public Entry setTime(String data){
        if(!data.contains("at")){
            set("humanTime", "Unknown");
            set("unixTime", "0");
            return this;
        }

        String date = data.split(" at ")[0].replace(",", ""); // Jun 29 2017
        String time = data.split(" at ")[1]; // 6:12 PM

        String monthString = date.split(" ")[0].toLowerCase();
        int month = Arrays.stream(Month.values()).filter(x -> x.name().toLowerCase().startsWith(monthString)).findFirst().orElse(null).getValue();
        int day = Integer.parseInt(date.split(" ")[1]);
        int year = Integer.parseInt(date.split(" ")[2]);

        boolean pm = date.endsWith("PM");
        int hour = Integer.parseInt(time.substring(0, time.length()-3).split(":")[0]);
        int minute = Integer.parseInt(time.substring(0, time.length()-3).split(":")[1]);

        Calendar c = Calendar.getInstance();
        c.set(year, month - 1, day, pm ? hour + 12 : hour, minute);

        set("humanTime", data);
        set("unixTime", c.getTimeInMillis() / 1000);
        return this;
    }

    public boolean has(String key){
        return data.containsKey(key);
    }

    public Object get(String key) { return data.get(key); }

    public String getString(String key){
        return (String) get(key);
    }

    public Map<String, Object> getAll() {
        return data;
    }

    public JSONObject toJSONObject() {
        return new JSONObject(data);
    }
}
