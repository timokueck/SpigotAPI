package me.TechsCode.SpigotAPI.server.routs.data.market;

import com.google.gson.JsonArray;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import me.TechsCode.SpigotAPI.data.ProfileComment;
import me.TechsCode.SpigotAPI.server.Config;
import me.TechsCode.SpigotAPI.server.HttpRouter;
import me.TechsCode.SpigotAPI.server.spigot.SpigotBrowser;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class VerifyUser_Market implements HttpHandler {
    public void handle(HttpExchange t) throws IOException {
        Map<String, String> params = HttpRouter.getParamMap(t.getRequestURI().getQuery());
        JSONObject obj = new JSONObject();
        String response;
        int responseCode ;

        if(params.get("token") !=null){
            String token = params.get("token");
            if(HttpRouter.isTokenValid(token)){
                if(params.get("user") !=null){
                    String user = params.get("user");

                    if(!user.isEmpty()){
                        if(params.get("showAll") !=null){
                            String showAllParam = params.get("showAll");
                            if(showAllParam.equals("true") || showAllParam.equals("false")){
                                Boolean showAll = Boolean.parseBoolean(showAllParam);

                                long now = System.currentTimeMillis();
                                try {
                                    Config config = Config.getInstance();
                                    SpigotBrowser parser = new SpigotBrowser(config.getSpigotUsername(), config.getSpigotPassword(), false);

                                    JsonArray comments = new JsonArray();
                                    ProfileComment[] profileComments = parser.getUserPosts(user, showAll);
                                    Arrays.stream(profileComments).map(ProfileComment::getState).forEach(comments::add);

                                    parser.close();

                                    long delay = System.currentTimeMillis() - now;
                                    System.out.println("Completed Cycle in "+Math.round(TimeUnit.MILLISECONDS.toMinutes(delay))+" minutes!");

                                    obj.put("data", comments);
                                    response = obj.toString();
                                    responseCode = 200;
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                    obj.put("E", "Error");
                                    obj.put("Msg", e.getMessage());
                                    response = obj.toString();
                                    responseCode = 200;
                                }

                            }else{
                                obj.put("Status", "Error");
                                obj.put("Msg", "Invalid showAll value");
                                response = obj.toString();
                                responseCode = 403;
                            }
                        }else{
                            obj.put("Status", "Error");
                            obj.put("Msg", "Missing showAll");
                            response = obj.toString();
                            responseCode = 403;
                        }
                    }else{
                        obj.put("Status", "Error");
                        obj.put("Msg", "Missing user");
                        response = obj.toString();
                        responseCode = 403;
                    }
                }else{
                    obj.put("Status", "Error");
                    obj.put("Msg", "Missing user");
                    response = obj.toString();
                    responseCode = 403;
                }
            }else{
                obj.put("Status", "Error");
                obj.put("Msg", "Invalid token");
                response = obj.toString();
                responseCode = 403;
            }
        }else{
            obj.put("Status", "Error");
            obj.put("Msg", "Missing token");
            response = obj.toString();
            responseCode = 400;
        }

        t.sendResponseHeaders(responseCode, response.length());
        OutputStream os = t.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}