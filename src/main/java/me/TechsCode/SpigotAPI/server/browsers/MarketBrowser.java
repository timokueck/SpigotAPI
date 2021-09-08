package me.TechsCode.SpigotAPI.server.browsers;

import me.TechsCode.SpigotAPI.data.*;
import me.TechsCode.SpigotAPI.data.lists.PurchasesList;
import me.TechsCode.SpigotAPI.data.lists.ResourcesList;
import me.TechsCode.SpigotAPI.data.lists.ReviewsList;
import me.TechsCode.SpigotAPI.data.lists.UpdatesList;
import me.TechsCode.SpigotAPI.server.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.*;

public class MarketBrowser extends VirtualBrowser {

    public static final String BASE = "https://www.mc-market.org";

    private final String loggedInUserId;

    public MarketBrowser(String username, String password, String userId, Boolean loginRequired) {
        if(loginRequired.equals(true)){
            login(username, password);
            loggedInUserId = userId;
        }else{
            loggedInUserId = null;
        }
    }

    private void login(String username, String password) {
         try {
             navigate(BASE + "/login");

             WebElement loginDialog = driver.findElement(By.id("pageLogin"));
             WebElement usernameField = loginDialog.findElement(By.id("ctrl_pageLogin_login"));
             WebElement passwordField = loginDialog.findElement(By.id("ctrl_pageLogin_password"));

             if (usernameField == null || passwordField == null) {
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
         }catch (Exception e){
            Logger.send(e.getMessage(), true);
            Logger.send(Arrays.toString(e.getStackTrace()), true);
         }
    }

    public ResourcesList collectResources() {
        ResourcesList resources = new ResourcesList();
        try {

            if (loggedInUserId == null) {
                return resources;
            }

            navigate(BASE + "/resources/authors/" + loggedInUserId);

            Document resourcesPage = Jsoup.parse(driver.getPageSource());

            for (Element item : resourcesPage.getElementsByClass("resourceListItem")) {
                String id = item.id().split("-")[1];
                String name = item.getElementsByClass("title").first().getElementsByTag("a").first().text();
                String version = item.getElementsByClass("title").first().getElementsByTag("span").first().text();
                String tagLine = item.getElementsByClass("tagLine").first().text();

                Element resourceDetails = item.getElementsByClass("resourceDetails").first();
                String category = resourceDetails.getAllElements().last().text();

                Element costElement = item.getElementsByClass("cost").first();
                boolean isPremium = false;
                if (costElement != null) {
                    isPremium = true;
                }

                Cost cost = isPremium ? new Cost(costElement.text()) : null;
                Time time = new Time(resourceDetails.getElementsByClass("DateTime").attr("title"));

                resources.add(new Resource(id, name, tagLine, category, version, cost, time));
            }
        }catch (Exception e){
            Logger.send(e.getMessage(), true);
            Logger.send(Arrays.toString(e.getStackTrace()), true);
        }
        return resources;
    }

    public UpdatesList collectUpdates(List<Resource> resources){
        UpdatesList updates = new UpdatesList();

        try{
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
        }catch (Exception e){
            Logger.send(e.getMessage(), true);
            Logger.send(Arrays.toString(e.getStackTrace()), true);
        }

        return updates;
    }

    public ReviewsList collectReviews(List<Resource> resources) {
        ReviewsList reviews = new ReviewsList();

        try{
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
        }catch (Exception e){
            Logger.send(e.getMessage(), true);
            Logger.send(Arrays.toString(e.getStackTrace()), true);
        }
        return reviews;
    }

    public PurchasesList collectPurchases(List<Resource> resources) {
        PurchasesList purchases = new PurchasesList();

        try{
            for(Map.Entry<Resource, List<Element>> pair : collectElementsOfSubPage(resources, "market-place-purchases", "dataRow").entrySet()) {
                Resource resource = pair.getKey();

                for(Element element : pair.getValue()){
                    if(element.getElementsByClass("typePurchase") == null) continue;

                    String status = element.getElementsByClass("statusValidated").first().text();
                    if(!status.equals("Validated")) continue;

                    Element userElement = element.getElementsByClass("username").first().getElementsByTag("a").first();
                    String userId = userElement.attr("href").replace("members", "").replace("/", "");
                    String username = userElement.text();

                    User user = new User(userId, username, "");

                    String PurchaseDate = element.getElementsByClass("DateTime").first().attr("title");
                    Time time = new Time(PurchaseDate);

                    String costString = element.getElementsByClass("price").first().text();
                    Cost cost = !costString.equals("0.00 EUR") ? new Cost(costString) : null;

                    purchases.add(new Purchase(resource.getId(), user, time, cost));
                }
            }
        }catch (Exception e){
            Logger.send(e.getMessage(), true);
            Logger.send(Arrays.toString(e.getStackTrace()), true);
        }

        return purchases;
    }

    private Map<Resource, List<Element>> collectElementsOfSubPage(List<Resource> resources, String subPage, String uniqueClassName){
        HashMap<Resource, List<Element>> map = new HashMap<>();

        try{
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
        }catch (Exception e){
            Logger.send(e.getMessage(), true);
            Logger.send(Arrays.toString(e.getStackTrace()), true);
        }

        return map;
    }

    private String parseAvatarUrl(String url) {
        if(url.startsWith("data")) return BASE + "/" + url;
        if(url.startsWith("//static")) return "https:" + url;
        return url;
    }

    public ProfileComment[] getUserPosts(String userId, Boolean allMessages){
        final List<ProfileComment> comments = new ArrayList<>();
        try{
            navigate(BASE+"/members/"+userId);
            Document doc = Jsoup.parse(driver.getPageSource());

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
                        final String userId2 = getUserFromHref(item.getElementsByTag("a").first().attr("href")).getUserId();

                        Element messageInfo = item.getElementsByClass("messageInfo").first();

                        if(!userId.equalsIgnoreCase(userId2) && !allMessages) //Skip if comment is not from page user and allMessage is false
                            continue;

                        final String text = messageInfo.getElementsByClass("baseHtml").first().text();

                        comments.add(new ProfileComment(commentId, userId, text));
                    }
                }
            }
        }catch (Exception e){
            Logger.send(e.getMessage(), true);
            Logger.send(Arrays.toString(e.getStackTrace()), true);
        }

        return comments.toArray(new ProfileComment[0]);
    }

    private static User getUserFromHref(String href) {
        href = href.replace("members/", "").replace("/", "");
        final String id = href;

        return new User(id, null, null);
    }

}
