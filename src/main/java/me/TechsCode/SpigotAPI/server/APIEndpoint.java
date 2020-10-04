package me.TechsCode.SpigotAPI.server;

import fi.iki.elonen.NanoHTTPD;
import me.TechsCode.SpigotAPI.data.Dataset;

import java.io.IOException;
import java.util.Map;

public class APIEndpoint extends NanoHTTPD {

    private final DataManager dataManager;
    private final String apiToken;

    public APIEndpoint(DataManager dataManager, String apiToken) {
        super(3333);

        this.dataManager = dataManager;
        this.apiToken = apiToken;

        try {
            start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Runtime.getRuntime().addShutdownHook(new Thread(APIEndpoint.super::stop));
    }

    @Override
    public Response serve(IHTTPSession session) {
        if(dataManager.getDataset() == null){
            return newFixedLengthResponse("Could not find any dataset!");
        }

        Map<String, String> params = session.getParms();

        if(!params.containsKey("token") || !params.get("token").equals(apiToken)){
            return newFixedLengthResponse("The token you provided is invalid!");
        }

        Dataset dataset = dataManager.getDataset();

        Response response = newFixedLengthResponse(dataset.toJsonObject().toString());
        response.addHeader("Content-Type", "application/json");
        return response;
    }
}
