package me.TechsCode.SpigotAPI.server.spigot;

import io.github.bonigarcia.wdm.WebDriverManager;
import me.TechsCode.SpigotAPI.server.SpigotAPIServer;
import me.TechsCode.SpigotAPI.server.data.Entry;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SpigotParser {

    public static final String BASE = "https://www.spigotmc.org";

    private ChromeDriver browser;
    private String ownUserId;

    public SpigotParser(String username, String password) {
        WebDriverManager.chromedriver().setup();

        System.setProperty(ChromeDriverService.CHROME_DRIVER_SILENT_OUTPUT_PROPERTY, "true");

        ChromeOptions chromeOptions = new ChromeOptions();
        //chromeOptions.addArguments("--no-sandbox");
        //chromeOptions.addArguments("--headless");

        browser = new ChromeDriver(chromeOptions);

        login(username, password);
    }

    public void close(){
        browser.close();
    }

    private void login(String username, String password){
        setPage("login");

        for(WebElement element : browser.findElements(By.id("ctrl_pageLogin_login"))){
            if(element.isDisplayed()) element.sendKeys(username);
        }

        for(WebElement element : browser.findElements(By.id("ctrl_pageLogin_password"))){
            if(element.isDisplayed()){
                element.sendKeys(password);
                element.submit();
            }
        }

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        WebElement link = browser.findElement(By.className("sidebar"))
                .findElement(By.className("visitorPanel"))
                .findElement(By.className("avatar"));

        ownUserId = link.getAttribute("href")
                .split("/members/")[1]
                .replace("/", "")
                .split("[.]")[1];
    }

    private void setPage(String url){
        browser.get(BASE+"/"+url);

        // Bypass Cloudflare
        int i = 0;
        while (browser.getPageSource().contains("This process is automatic. Your browser will redirect to your requested content shortly.")){
            if(i == 0) System.out.println("Cloudflare detected. Bypassing it now...");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            i++;
        }

        if(i != 0) System.out.println("Bypassed Cloudflare after "+i+" seconds");

        while (browser.getPageSource().contains("One more step") && browser.getPageSource().contains("Please complete the security check to access")){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public List<Entry> retrieveResources(){
        List<Entry> list = new ArrayList<>();

        setPage("resources/authors/"+ownUserId);
        Document resourcesPage = Jsoup.parse(browser.getPageSource());

        for(Element item : resourcesPage.getElementsByClass("resourceListItem")){

            Elements resourceDetails = item.getElementsByClass("resourceDetails").first().getAllElements();

            String id = item.id().split("-")[1];
            String name = item.getElementsByClass("title").first().getElementsByTag("a").first().text();
            String version = item.getElementsByClass("title").first().getElementsByTag("span").first().text();
            String tagLine = item.getElementsByClass("tagLine").first().text();
            String category = resourceDetails.get(4).text();
            String costString = category.equalsIgnoreCase("premium") ? item.getElementsByClass("cost").first().text() : null;
            String icon = BASE+"/"+item.getElementsByClass("resourceIcon").select("img").attr("src");
            String time = resourceDetails.get(2).getAllElements().first().attr("title");

            Entry entry = new Entry()
                    .set("id", id)
                    .set("name", name)
                    .set("version", version)
                    .set("tagLine", tagLine)
                    .set("category", category)
                    .setCost(costString)
                    .setTime(time)
                    .set("icon", icon);

            list.add(entry);
        }

        return list;
    }

    public List<Entry> retrieveUpdates(List<Entry> resources){
        List<Entry> list = new ArrayList<>();

        for(Map.Entry<Element, Entry> pair : collectElementsOfSubPage(resources, "updates", "resourceUpdate").entrySet()){
            Element element = pair.getKey();
            Entry resource = pair.getValue();

            if(element.getElementsByClass("textHeading").isEmpty()) continue; // If there are no updates, spigot redirects to the homepage. Since that div is also used there we have to block it.

            String resourceId = resource.getString("id");
            String resourceName = resource.getString("name");
            String id = element.id().split("-")[1];
            String title = element.getElementsByClass("textHeading").first().text();
            String description = element.getElementsByClass("messageText").first().text();
            String time = element.getElementsByClass("DateTime").first().attr("title");

            Elements imageElements = element.select("img[data-url]");
            List<String> imagesEntry = imageElements.stream().map(elements -> elements.attr("data-url")).collect(Collectors.toList());

            Entry entry = new Entry()
                    .set("id", id)
                    .set("resourceId", resourceId)
                    .set("resourceName", resourceName)
                    .set("title", title)
                    .set("description", description)
                    .setTime(time)
                    .set("images", imagesEntry);

            list.add(entry);
        }

        return list;
    }

    public List<Entry> retrieveReviews(List<Entry> resources){
        List<Entry> list = new ArrayList<>();

        for(Map.Entry<Element, Entry> pair : collectElementsOfSubPage(resources, "reviews", "review").entrySet()) {
            Element element = pair.getKey();
            Entry resource = pair.getValue();

            String id = element.id().split("-")[1];
            String resourceId = resource.getString("id");
            String resourceName = resource.getString("name");
            String text = element.getElementsByTag("blockquote").text().replace("<br>", "\n");
            int rating = Math.round(Float.parseFloat(element.getElementsByClass("ratings").first().attr("title")));
            String username = element.attr("data-author");
            String userId = element.id().split("-")[2];
            String avatarUrl = element.select("img").attr("src");

            String time = element.getElementsByClass("DateTime").first().attr("title");

            Entry entry = new Entry()
                    .set("id", id)
                    .set("resourceId", resourceId)
                    .set("resourceName", resourceName)
                    .set("text", text)
                    .set("rating", rating)
                    .set("username", username)
                    .set("userId", userId)
                    .set("avatarUrl", parseAvatarUrl(avatarUrl))
                    .setTime(time);

            list.add(entry);
        }

        return list;
    }

    public List<Entry> retrievePurchases(List<Entry> resources){
        List<Entry> list = new ArrayList<>();

        for(Map.Entry<Element, Entry> pair : collectElementsOfSubPage(resources, "buyers", "memberListItem").entrySet()) {
            Element element = pair.getKey();
            Entry resource = pair.getValue();

            Element link = element.getElementsByClass("StatusTooltip").first();

            Element costElement = element
                    .getElementsByClass("extra").last()
                    .getAllElements().last();

            String username = link.text();
            String userId = link.attr("href").replace("/", "").split("[.]")[1];

            String time = element.getElementsByClass("DateTime").first().parent().html()
                    .split("title=\"")[1]
                    .split("\">")[0];

            String costString = costElement.tagName().equalsIgnoreCase("div") ? costElement.text().split(": ")[1] : null;
            String avatarUrl = element.getElementsByClass("s").first().attr("style").split("'")[1].split("'")[0];

            String resourceId = resource.getString("id");
            String resourceName = resource.getString("name");

            Entry entry = new Entry()
                    .set("id", resourceId + "-" + userId)
                    .set("resourceId", resourceId)
                    .set("resourceName", resourceName)
                    .set("username", username)
                    .set("userId", userId)
                    .set("avatarUrl", parseAvatarUrl(avatarUrl))
                    .setTime(time)
                    .setCost(costString);

            list.add(entry);
        }

        return list;
    }

    private Map<Element, Entry> collectElementsOfSubPage(List<Entry> resources, String subPage, String uniqueClassName){
        HashMap<Element, Entry> map = new HashMap<>();

        for(Entry resource : resources){
            String resourceId = resource.getString("id");
            String category = resource.getString("category");

            if(!category.equalsIgnoreCase("premium")) continue;

            int pageAmount = 1;
            int currentPage = 1;

            while (currentPage <= pageAmount){
                setPage("resources/"+resourceId+"/"+subPage+"?page="+currentPage);
                Document document = Jsoup.parse(browser.getPageSource());

                for(Element element : document.getElementsByClass(uniqueClassName)){
                    map.put(element, resource);
                }

                pageAmount = getPageAmount(document);
                currentPage++;
            }
        }

        return map;
    }

    private int getPageAmount(Document document){
        if(document.getElementsByClass("pageNavHeader").isEmpty()) return 1;

        Element pageNavHeader = document.getElementsByClass("pageNavHeader").first();
        String value = pageNavHeader.text().split(" of ")[1];
        return Integer.parseInt(value);
    }

    private String parseAvatarUrl(String url) {
        if(url.startsWith("data")) return BASE + "/" + url;
        if(url.startsWith("//static")) return "https:" + url;
        return url;
    }

    public String getOwnUserId() {
        return ownUserId;
    }
}

