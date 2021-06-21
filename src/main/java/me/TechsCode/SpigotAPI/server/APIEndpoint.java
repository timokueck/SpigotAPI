package me.TechsCode.SpigotAPI.server;

import fi.iki.elonen.NanoHTTPD;
import me.TechsCode.SpigotAPI.data.Dataset;

import java.io.IOException;
import java.util.Map;

public class APIEndpoint extends NanoHTTPD {

    private final DataManager dataManager;
    private final String apiToken;

    public APIEndpoint(DataManager dataManager, String apiToken) {
        super(80);
        this.dataManager = dataManager;
        this.apiToken = apiToken;
        try {
            start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Runtime.getRuntime().addShutdownHook(new Thread(APIEndpoint.super::stop));

    }

    public NanoHTTPD.Response serve(NanoHTTPD.IHTTPSession session) {
        if (this.dataManager.getDataset() == null)
            return newFixedLengthResponse("Could not find any dataset!");
        Map<String, String> params = session.getParms();
        if (((String)params.get("token")).equals(this.apiToken + "restart")) {
            try {
                Runtime.getRuntime().exec("cmd.exe /c start C:\\Users\\Administrator\\Desktop\\SpigotAPI\\start.bat");
                Thread.sleep(1000L);
            } catch (IOException|InterruptedException e) {
                e.printStackTrace();
            }
            System.exit(0);
        }
        if (!params.containsKey("token") || !((String)params.get("token")).equals(this.apiToken))
            return newFixedLengthResponse("The token you provided is invalid!");
        Dataset dataset = this.dataManager.getDataset();
        NanoHTTPD.Response response = newFixedLengthResponse(dataset.toJsonObject().toString());
        response.addHeader("Content-Type", "application/json");
        return response;
    }
}
