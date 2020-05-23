package me.TechsCode.SpigotAPI.server.spigot;

import me.TechsCode.SpigotAPI.server.data.Entry;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Parser {

    private String ownUserId;


    private String getLoggedInUserId(String xml){
        Document document = Jsoup.parse(xml);

        try {
            Element link = document
                    .getElementsByClass("sidebar").first()
                    .getElementsByClass("visitorPanel").first()
                    .getElementsByClass("avatar").first();

            String userId = link.attr("href")
                    .replace("/", "")
                    .split("[.]")[1];

            return userId;
        } catch (Exception e){
            return null;
        }
    }


}
