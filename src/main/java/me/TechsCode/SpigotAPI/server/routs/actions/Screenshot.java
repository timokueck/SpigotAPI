package me.TechsCode.SpigotAPI.server.routs.actions;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import me.TechsCode.SpigotAPI.server.HttpRouter;
import me.TechsCode.SpigotAPI.server.Logger;
import org.json.simple.JSONObject;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URI;
import java.util.Arrays;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

public class Screenshot implements HttpHandler {
    public void handle(HttpExchange t) throws IOException {
        Map<String, String> params = HttpRouter.getParamMap(t.getRequestURI().getQuery());
        JSONObject obj = new JSONObject();
        String response;
        int responseCode;

        if(params.get("token") !=null){
            String token = params.get("token");
            if(HttpRouter.isTokenValid(token)){
                String currentPath = new java.io.File(".").getCanonicalPath();
                try {
                    Robot robot = new Robot();

                    robot.mouseMove(50, 50);
                    Thread.sleep(200);
                    robot.mouseMove(800, 800);
                    Thread.sleep(200);
                    robot.mouseMove(400, 400);
                    Thread.sleep(500);

                    BufferedImage image = robot.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
                    ImageIO.write(image, "png", new File(currentPath+"\\data\\screenshot.png"));

                    Logger.send("Someone took a screenshot", true);

                    Headers h = t.getResponseHeaders();
                    h.add("Content-Type", "image/png");
                    File file = new File(currentPath+"\\data\\screenshot.png");
                    byte [] bytearray  = new byte [(int)file.length()];
                    FileInputStream fis = new FileInputStream(file);
                    BufferedInputStream bis = new BufferedInputStream(fis);
                    bis.read(bytearray, 0, bytearray.length);

                    t.sendResponseHeaders(200, file.length());
                    OutputStream os = t.getResponseBody();
                    os.write(bytearray,0,bytearray.length);
                    os.close();

                    response = "";
                    responseCode = 200;
                } catch (Exception e) {
                    Logger.send(Arrays.toString(e.getStackTrace()), true);
                    obj.put("Status", "Error");
                    obj.put("Msg", e.getStackTrace());
                    response = obj.toString();
                    responseCode = 500;
                }
            }else{
                obj.put("Status", "Error");
                obj.put("Msg", "Invalid token");
                response = obj.toString();
                responseCode = 401;
            }
        }else{
            obj.put("Status", "Error");
            obj.put("Msg", "Missing token");
            response = obj.toString();
            responseCode = 401;
        }

        t.sendResponseHeaders(responseCode, response.length());
        OutputStream os = t.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}
