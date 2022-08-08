package me.TechsCode.SpigotAPI.server.browsers;

import com.google.gson.JsonArray;
import me.TechsCode.SpigotAPI.server.Config;
import me.TechsCode.SpigotAPI.server.Logger;
import me.TechsCode.SpigotAPI.server.TwoFactorAuth;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class SpigotVerifyBrowser extends VirtualVerifyBrowser {

    public static final String BASE = "https://www.spigotmc.org";

    private final String loggedInUserId;

    public SpigotVerifyBrowser(String username, String password, String userId, Boolean loginRequired) {
        Logger.send("Initializing Spigot Verify Browser...", false);
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

    public void navigateToUserProfile(String userId) {
        navigate(BASE+"/members/"+userId);
    }

    public void navigateToUserProfileInfo(String userId) {
        navigate(BASE+"/members/"+userId+"/#info");
    }

    public JsonArray collectPosts(String username) {
        Logger.send("Collecting posts from "+username+"...", false);
        JsonArray postsList = new JsonArray();

        try{
            Document document = Jsoup.parse(driver.getPageSource());
            Element postsContainer = document.getElementById("ProfilePostList");
            for (int i = 0; i < postsContainer.children().size(); i++) {
                Element post = postsContainer.children().get(i);
                String user = post.getElementsByClass("username").first().text();
                String content = post.getElementsByTag("article").first().text();
                //Logger.send("Post: "+user+" - "+content, false);
                if(Objects.equals(user, username)){
                    postsList.add(content);
                }
                if (postsList.size() == 5) break;
            }
        }catch (Exception e){
            Logger.send(e.getMessage(), true);
            Logger.send(Arrays.toString(e.getStackTrace()), true);
        }

        return postsList;
    }

    public String getProfileImgUrl() {
        String url = "";

        try{
            Document document = Jsoup.parse(driver.getPageSource());
            Element profileImgContainer = document.getElementsByClass("avatarScaler").first();
            url = profileImgContainer.getElementsByTag("img").first().attr("src");
        }catch (Exception e){
            Logger.send(e.getMessage(), true);
            Logger.send(Arrays.toString(e.getStackTrace()), true);
        }

        return url;
    }

    public String getUsername() {
        String username = "";

        try{
            Document document = Jsoup.parse(driver.getPageSource());
            Element mainTextContainer = document.getElementsByClass("mainText").first();
            username = mainTextContainer.getElementsByClass("username").first().text();
        }catch (Exception e){
            Logger.send(e.getMessage(), true);
            Logger.send(Arrays.toString(e.getStackTrace()), true);
        }

        return username;
    }

    public String getDiscord() {
        AtomicReference<String> discord = new AtomicReference<>("");

        try{
            Document document = Jsoup.parse(driver.getPageSource());
            Element contactInfoContainer = document.getElementsByClass("contactInfo").first();

            contactInfoContainer.children().forEach(contactInfoItem -> {
                Element name = contactInfoItem.getElementsByTag("dt").first();
                Element value = contactInfoItem.getElementsByTag("dd").first();
                if(name.text().equals("Discord:")){
                    discord.set(value.text());
                }
            });
        }catch (Exception e){
            Logger.send(e.getMessage(), true);
            Logger.send(Arrays.toString(e.getStackTrace()), true);
        }

        return discord.get();
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
                sleep(500);
                WebElement errorOverlay = driver.findElement(By.className("errorOverlay"));
                errorOverlay.findElement(By.className("close")).click();

                twoFaField.clear();
                twoFaField.sendKeys("2FA Token is invalid! Waiting 35 seconds!");
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

}
