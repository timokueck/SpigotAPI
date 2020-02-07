package me.TechsCode.SpigotAPI.client;

import me.TechsCode.SpigotAPI.client.objects.*;
import me.TechsCode.SpigotAPI.logging.Logger;
import org.apache.commons.io.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class APIScanner {

    private SpigotAPIClient client;
    private String url;
    private String token;

    public APIScanner(SpigotAPIClient client, String url, String token) {
        this.client = client;
        this.url = url;
        this.token = token;
    }

    public Data retrieveData(){
        JSONObject data;

        try {
            JSONParser parser = new JSONParser();
            String json = IOUtils.toString(new URI(url+"/?token="+token), "UTF-8");
            JSONObject root = (JSONObject) parser.parse(json);

            String status = (String) root.get("status");
            if(!status.equalsIgnoreCase("success")){
                Logger.log("API returned error message:");
                System.out.println(root.get("message"));
                return null;
            }

            data = (JSONObject) root.get("data");
        } catch (Exception e) {
            Logger.log("Could not reach SpigotAPI on "+url);
            return null;
        }

        Resource[] resources = getChilds(data.get("resources")).map(item -> new Resource(client, item)).toArray(Resource[]::new);
        Purchase[] purchases = getChilds(data.get("purchases")).map(item -> new Purchase(client, item)).toArray(Purchase[]::new);
        Review[] reviews = getChilds(data.get("reviews")).map(item -> new Review(client, item)).toArray(Review[]::new);
        Update[] updates = getChilds(data.get("updates")).map(item -> new Update(client, item)).toArray(Update[]::new);

        return new Data(System.currentTimeMillis(), resources, purchases, reviews, updates);
    }


    private Stream<JSONObject> getChilds(Object object){
        JSONArray jsonArray = (JSONArray) object;

        List<JSONObject> list = new ArrayList<>();
        for(int i = 0; i <= jsonArray.size()-1; i++){
            list.add((JSONObject) jsonArray.get(i));
        }

        return list.stream();
    }
}
