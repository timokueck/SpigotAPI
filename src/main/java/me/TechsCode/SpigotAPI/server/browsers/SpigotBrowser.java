package me.TechsCode.SpigotAPI.server.browsers;

import me.TechsCode.SpigotAPI.data.*;
import me.TechsCode.SpigotAPI.data.lists.*;
import me.TechsCode.SpigotAPI.server.Config;
import me.TechsCode.SpigotAPI.server.Logger;
import me.TechsCode.SpigotAPI.server.TwoFactorAuth;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.*;

public class SpigotBrowser extends VirtualBrowser {

    public static final String BASE = "https://www.spigotmc.org";

    private final String loggedInUserId;

    public SpigotBrowser(String username, String password, String userId, Boolean loginRequired) {
        Logger.send("Initializing Spigot Browser...", false);
        if(loginRequired.equals(true)){
            login(username, password);
            loggedInUserId = userId;
        }else{
            loggedInUserId = null;
        }
    }

    private void login(String username, String password) {
        try{
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

            if (driver.findElementById("ctrl_totp_code") != null){
                resolve2FA();
            }

            sleep(2000);
        }catch (Exception e){
            Logger.send(e.getMessage(), true);
            Logger.send(Arrays.toString(e.getStackTrace()), true);
        }
    }

    private void resolve2FA() {
        try{
            WebElement twoFaField = driver.findElement(By.id("ctrl_totp_code"));

            if(twoFaField == null){
                throw new IllegalStateException("Could not find a username or password field!");
            }

            String twoFaToken = Config.getInstance().get2FAToken();
            String twoFaCode = new TwoFactorAuth(twoFaToken).getCode();

            // Fill in 2FA Code
            twoFaField.clear();
            twoFaField.sendKeys(twoFaCode);

            // Login!
            twoFaField.submit();

            sleep(1000);

            if(driver.getPageSource().contains("The two-step verification value could not be confirmed. Please try again.")){
                Logger.error("2FA Token is invalid! Waiting 35 seconds!", false);
                sleep(35000);
                resolve2FA();
                return;
            }

            sleep(1000);
        }catch (Exception e){
            Logger.send(e.getMessage(), true);
            Logger.send(Arrays.toString(e.getStackTrace()), true);
        }
    }

    public PurchasesList collectPurchases(List<Resource> resources) {
        PurchasesList purchases = new PurchasesList();

        try{
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
        }catch (Exception e){
            Logger.send(e.getMessage(), true);
            Logger.send(Arrays.toString(e.getStackTrace()), true);
        }

        return purchases;
    }

    private Map<Resource, List<Element>> collectElementsOfSubPage(List<Resource> resources, String subPage, String uniqueClassName){
        HashMap<Resource, List<Element>> map = new HashMap<>();

        try{
            Logger.info(resources.size()+" resources to fetch", true);
            int currentResource = 1;

            for(Resource resource : resources){
                List<Element> elements = new ArrayList<>();

                Logger.info("Starting fetch for "+resource.getName()+". "+currentResource+"/"+resources.size(), true);

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
                    sleep(800);
                }

                currentResource++;
                Logger.info("Fetch for "+resource.getName()+" finished. "+elements.size()+" purchases found.", true);
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

}
