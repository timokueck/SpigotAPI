package me.TechsCode.SpigotAPI.server.routs;

import com.google.gson.JsonArray;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import me.TechsCode.SpigotAPI.server.*;
import me.TechsCode.SpigotAPI.server.browsers.SpigotVerifyBrowser;
import me.TechsCode.SpigotAPI.server.browsers.VirtualVerifyBrowser;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class VerifyUser implements HttpHandler {
    public void handle(HttpExchange t) throws IOException {
        Map<String, String> params = HttpRouter.getParamMap(t.getRequestURI().getQuery());
        JSONObject obj = new JSONObject();
        String response;
        int responseCode;

        if(params.get("token") !=null){
            String token = params.get("token");
            if(HttpRouter.isTokenValid(token)){

                if(params.get("userId") == null) {
                    obj.put("error", "Missing userId");
                    response = obj.toString();
                    responseCode = 401;
                }else{
                    String userId = params.get("userId");

                    JSONObject pendingData = new JSONObject();
                    pendingData.put("status", "pending");
                    pendingData.put("posts", new JsonArray());
                    pendingData.put("username", "");
                    pendingData.put("profileImg", "");
                    pendingData.put("discord", "");
                    VirtualVerifyBrowser.addVerifiedUser(userId, pendingData);

                    if(VirtualVerifyBrowser.isVerifying()){
                        obj.put("status", "error");
                        obj.put("msg", "A verification is already in progress");

                        response = obj.toString();
                        responseCode = 500;
                    }else {

                        Thread t1 = new Thread(() -> {
                            VirtualVerifyBrowser.setVerifying(true);
                            Config config = Config.getInstance();
                            long now = System.currentTimeMillis();

                            SpigotVerifyBrowser parser = null;
                            try {
                                VirtualVerifyBrowser.enableSpigotPreload();
                                parser = new SpigotVerifyBrowser(config.getSpigotUsername(), config.getSpigotPassword(), config.getSpigotUserId(), true);
                            } catch (Exception e) {
                                Logger.send(e.getMessage(), true);
                                Logger.send(Arrays.toString(e.getStackTrace()), true);
                                VirtualVerifyBrowser.setVerifying(false);
                            }

                            try {
                                if (parser != null) {

                                    parser.navigateToUserProfile(userId);
                                    String username = parser.getUsername();
                                    JsonArray posts = parser.collectPosts(username);
                                    String profileImg = parser.getProfileImgUrl();

                                    parser.navigateToUserProfileInfo(userId);
                                    String discord = parser.getDiscord();

                                    JSONObject data = new JSONObject();
                                    data.put("status", "complete");
                                    data.put("posts", posts);
                                    data.put("username", username);
                                    data.put("profileImg", profileImg);
                                    data.put("discord", discord);

                                    parser.close();

                                    VirtualVerifyBrowser.addVerifiedUser(userId, data);

                                    long delay = System.currentTimeMillis() - now;
                                    Logger.send("Fetched " + posts.size() + " Posts!", false);
                                    Logger.send("Completed SpigotMC User Verification in " + Math.round(TimeUnit.MILLISECONDS.toMinutes(delay)) + " minutes!", true);
                                } else {
                                    Logger.send("Failed SpigotMC User Verification", true);
                                }
                                VirtualVerifyBrowser.setVerifying(false);
                            } catch (Exception e) {
                                Logger.send(e.getMessage(), true);
                                Logger.send(Arrays.toString(e.getStackTrace()), true);
                                if (parser != null)
                                    parser.close();
                                VirtualVerifyBrowser.setVerifying(false);
                            }

                            try {
                                Thread.sleep(30000);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }

                            VirtualVerifyBrowser.setVerifying(false);
                        });
                        t1.start();

                        obj.put("status", "success");
                        obj.put("msg", "Verification Started");
                        response = obj.toString();
                        responseCode = 200;
                    }
                }
            }else{
                obj.put("status", "error");
                obj.put("msg", "Invalid token");
                response = obj.toString();
                responseCode = 401;
            }
        }else{
            obj.put("status", "error");
            obj.put("msg", "Missing token");
            response = obj.toString();
            responseCode = 401;
        }

        t.sendResponseHeaders(responseCode, response.length());
        OutputStream os = t.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}
