package me.TechsCode.SpigotAPI.server.spigot;

import me.TechsCode.SpigotAPI.data.*;
import me.TechsCode.SpigotAPI.data.lists.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.*;

public class SpigotBrowser extends VirtualBrowser {

    public static final String BASE = "https://www.spigotmc.org";

    private final String loggedInUserId;

    public SpigotBrowser(String username, String password, String userId, Boolean loginRequired) throws InterruptedException {
        if(loginRequired.equals(true)){
            login(username, password);
            loggedInUserId = userId;
        }else{
            loggedInUserId = null;
        }
    }

    private void login(String username, String password) throws InterruptedException {
        navigate(BASE+"/login");

        WebElement loginDialog = driver.findElement(By.id("pageLogin"));
        WebElement usernameField = loginDialog.findElement(By.id("ctrl_pageLogin_login"));
        WebElement passwordField = loginDialog.findElement(By.id("ctrl_pageLogin_password"));

        if(usernameField == null || passwordField == null){
            throw new IllegalStateException("Could not find a username or password field!");
        }

        // Fill in credentials
        usernameField.clear();
        passwordField.clear();
        usernameField.sendKeys(username);
        passwordField.sendKeys(password);

        // Login!
        passwordField.submit();

        sleep(2000);
    }

    public ResourcesList collectResources() throws InterruptedException {
        ResourcesList resources = new ResourcesList();

        if(loggedInUserId == null){
            return resources;
        }

        navigate(BASE+"/resources/authors/"+loggedInUserId);

        Document resourcesPage = Jsoup.parse(driver.getPageSource());

        for(Element item : resourcesPage.getElementsByClass("resourceListItem")){
            String id = item.id().split("-")[1];
            String name = item.getElementsByClass("title").first().getElementsByTag("a").first().text();
            String version = item.getElementsByClass("title").first().getElementsByTag("span").first().text();
            String tagLine = item.getElementsByClass("tagLine").first().text();

            Element resourceDetails = item.getElementsByClass("resourceDetails").first();
            String category = resourceDetails.getAllElements().get(4).text();
            boolean isPremium = category.equalsIgnoreCase("premium");

            Cost cost = isPremium ? new Cost(item.getElementsByClass("cost").first().text()) : null;
            Time time = new Time(resourceDetails.getElementsByClass("DateTime").attr("title"));

            resources.add(new Resource(id, name, tagLine, category, version, cost, time));
        }

        return resources;
    }

    public UpdatesList collectUpdates(List<Resource> resources) throws InterruptedException{
        UpdatesList updates = new UpdatesList();

        for(Map.Entry<Resource, List<Element>> pair : collectElementsOfSubPage(resources, "updates", "resourceUpdate").entrySet()){
            Resource resource = pair.getKey();

            for(Element element : pair.getValue()){
                // If there are no updates, spigot redirects to the homepage. Since that div is also used there we have to block it.
                if(element.getElementsByClass("textHeading").isEmpty()) continue;

                String id = element.id().split("-")[1];
                String title = element.getElementsByClass("textHeading").first().text();
                String description = element.getElementsByClass("messageText").first().text();
                Time time = new Time(element.getElementsByClass("DateTime").first().attr("title"));

                String[] images = element.select("img[data-url]").stream()
                        .map(x -> x.attr("data-url"))
                        .toArray(String[]::new);

                updates.add(new Update(id, resource.getId(), title, images, description, time));
            }
        }

        return updates;
    }

    public ReviewsList collectReviews(List<Resource> resources) throws InterruptedException {
        ReviewsList reviews = new ReviewsList();

        for(Map.Entry<Resource, List<Element>> pair : collectElementsOfSubPage(resources, "reviews", "review").entrySet()) {
            Resource resource = pair.getKey();

            for(Element element : pair.getValue()){
                String id = element.id().split("-")[1];
                String username = element.attr("data-author");
                String userId = element.id().split("-")[2];
                String avatarUrl = element.select("img").attr("src");
                User user = new User(userId, username, parseAvatarUrl(avatarUrl));

                String text = element.getElementsByTag("blockquote").text().replace("<br>", "\n");
                int rating = Math.round(Float.parseFloat(element.getElementsByClass("ratings").first().attr("title")));

                Time time = new Time(element.getElementsByClass("DateTime").first().attr("title"));

                reviews.add(new Review(id, resource.getId(), user, text, rating, time));
            }

        }

        return reviews;
    }

    public PurchasesList collectPurchases(List<Resource> resources) throws InterruptedException {
        PurchasesList purchases = new PurchasesList();

        for(Map.Entry<Resource, List<Element>> pair : collectElementsOfSubPage(resources, "buyers", "memberListItem").entrySet()) {
            Resource resource = pair.getKey();

            for(Element element : pair.getValue()){
                Element link = element.getElementsByClass("StatusTooltip").first();

                Element costElement = element
                        .getElementsByClass("extra").last()
                        .getAllElements().last();

                String userId = link.attr("href").replace("/", "").split("[.]")[1];
                String username = link.text();
                String avatarUrl = parseAvatarUrl(element.getElementsByClass("s").first().attr("style").split("'")[1].split("'")[0]);
                User user = new User(userId, username, avatarUrl);

                Time time = new Time(element.getElementsByClass("DateTime").attr("title"));
                Cost cost = costElement.tagName().equalsIgnoreCase("div") ? new Cost(costElement.text().split(": ")[1]) : null;

                purchases.add(new Purchase(resource.getId(), user, time, cost));
            }

        }

        return purchases;
    }

    private Map<Resource, List<Element>> collectElementsOfSubPage(List<Resource> resources, String subPage, String uniqueClassName) throws InterruptedException {
        HashMap<Resource, List<Element>> map = new HashMap<>();

        for(Resource resource : resources){
            if(!resource.isPremium()){
                map.put(resource, Collections.emptyList());
                continue;
            }

            List<Element> elements = new ArrayList<>();

            int pageAmount = 1;
            int currentPage = 1;

            while (currentPage <= pageAmount){
                navigate(BASE + "/resources/"+resource.getId()+"/"+subPage+"?page="+currentPage);

                Document document = Jsoup.parse(driver.getPageSource());

                elements.addAll(document.getElementsByClass(uniqueClassName));

                /* Retrieving Page Amount */
                Optional<Element> pageNavHeader = document.getElementsByClass("pageNavHeader").stream().findFirst();
                if(pageNavHeader.isPresent()){
                    String value = pageNavHeader.get().text().split(" of ")[1];
                    pageAmount = Integer.parseInt(value);
                }

                currentPage++;
            }

            map.put(resource, elements);
        }

        return map;
    }

    private String parseAvatarUrl(String url) {
        if(url.startsWith("data")) return BASE + "/" + url;
        if(url.startsWith("//static")) return "https:" + url;
        return url;
    }

    public ProfileComment[] getUserPosts(String userId, Boolean allMessages) throws InterruptedException {
        navigate(BASE+"/members/"+userId);
        Document doc = Jsoup.parse(driver.getPageSource());
        final List<ProfileComment> comments = new ArrayList<>();

        final Elements pageCounter = doc.getElementsByClass("pageNavHeader");
        int pages = 1;

        if (!pageCounter.isEmpty())
            pages = Integer.parseInt(pageCounter.first().text().split("of ")[1]);

        for (int page = 1; page <= pages; ++page) {
            navigate(BASE+"/members/"+userId+"?page="+page);
            Document pageDoc = Jsoup.parse(driver.getPageSource());

            if (pageDoc != null) {
                for(Element item : pageDoc.getElementById("ProfilePostList").getElementsByClass("messageSimple")){
                    final String commentId = item.attr("id").split("-")[2];
                    final String user = item.attr("data-author");

                    Element messageInfo = item.getElementsByClass("messageInfo").first();
                    final String user2 = getUserFromHref(userId).getUsername();

                    if(!user2.equalsIgnoreCase(user) && !allMessages) //Skip if comment is not from
                        continue;

                    final String text = messageInfo.getElementsByTag("blockquote").text();

                    comments.add(new ProfileComment(commentId, userId, text));
                }
            }
        }

        return comments.toArray(new ProfileComment[0]);
    }

    private static User getUserFromHref(String href) {
        href = href.replace("members/", "").replace("/", "");
        final String username = href.split("[.]")[0];
        final String id = href.split("[.]")[1];

        return new User(id, username, null);
    }

    public String getAPIStatus() throws InterruptedException {
        navigate(BASE+"/");
        Document doc = Jsoup.parse(driver.getPageSource());

        Element el = doc.getElementById("content");
        if(el == null){
            return "offline";
        }else{
            return "online";
        }
    }
}
