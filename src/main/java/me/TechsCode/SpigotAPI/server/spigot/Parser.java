package me.TechsCode.SpigotAPI.server.spigot;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

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
