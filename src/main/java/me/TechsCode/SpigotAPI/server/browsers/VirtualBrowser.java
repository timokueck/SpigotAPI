package me.TechsCode.SpigotAPI.server.browsers;

import io.github.bonigarcia.wdm.WebDriverManager;
import me.TechsCode.SpigotAPI.server.Logger;
import me.TechsCode.SpigotAPI.server.SpigotAPIServer;
import me.TechsCode.SpigotAPI.server.routs.data.market.VerifyUser_Market;
import me.TechsCode.SpigotAPI.server.routs.data.spigot.VerifyUser_Spigot;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.Arrays;
import java.util.Collections;

public class VirtualBrowser {

    protected static ChromeDriver driver;
    private static boolean preloadSpigot = false;
    private static boolean preloadMarket = false;

    private static final String OS = System.getProperty("os.name").toLowerCase();

    public VirtualBrowser() {
        WebDriverManager.chromedriver().setup();
        System.setProperty(ChromeDriverService.CHROME_DRIVER_SILENT_OUTPUT_PROPERTY, "true");

        ChromeOptions options = new ChromeOptions();

        options.addArguments("--disable-blink-features=AutomationControlled");
        options.setExperimentalOption("useAutomationExtension", false);
        options.setExperimentalOption("excludeSwitches", Collections.singletonList("enable-automation"));
        options.addArguments("--disable-infobars");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("enable-features=NetworkServiceInProcess");

        if(!isWindows() && !isMac()) {
            options.addArguments("--disable-extensions");
            options.addArguments("--headless");
            options.addArguments("--disable-gpu");
            options.addArguments("--no-sandbox");
        }

        driver = new ChromeDriver(options);
        driver.navigate().to("https://google.com");

        try{
            preloadSites("");
        }catch (Exception e){
            Logger.send(e.getMessage(), true);
            Logger.send(Arrays.toString(e.getStackTrace()), true);
        }
    }

    public void preloadSites(String url) {
        if (preloadSpigot) {
            driver.executeScript("spigot_popup_window = window.open('https://www.spigotmc.org/" + SpigotAPIServer.getRandomInt() + "');");

            try {
                Thread.sleep(14000L);
            } catch (InterruptedException e) {
                Logger.send(e.getMessage(), true);
                Logger.send(Arrays.toString(e.getStackTrace()), true);
            }
        }

        if (preloadMarket) {
            driver.executeScript("market_popup_window = window.open('https://www.mc-market.org/" + SpigotAPIServer.getRandomInt() + "');");

            try {
                Thread.sleep(14000L);
            } catch (InterruptedException e) {
                Logger.send(e.getMessage(), true);
                Logger.send(Arrays.toString(e.getStackTrace()), true);
            }
        }

        if (!url.isEmpty()) {
            navigate(url);
        }
    }

    public static void enableSpigotPreload() {
        preloadSpigot = true;
    }

    public static void enableMarketPreload() {
        preloadMarket = true;
    }

    public static boolean isWindows() {
        return (OS.contains("win"));
    }

    public static boolean isMac() {
        return (OS.contains("mac"));
    }

    public void navigate(String url) {
        try{
            driver.get(url);

            // Bypass Cloudflare
            int i = 0;
            while (driver.getPageSource().contains("This process is automatic")) {
                if (i == 0)
                    Logger.send("Cloudflare detected. Bypassing it now...", true);

                if(i == 15 || i == 60){
                    Logger.send("Could not bypass cloudflare. Preloading site.", true);
                    driver.navigate().to("https://google.com");
                    preloadSites(url);
                }

                if(i > 90) {
                    Logger.send("<@&311178859171282944> Bypass not working! Shutting down", true);
                    System.exit(0);
                }

                sleep(1000);

                i++;
            }

            if (i != 0)
                Logger.send("Bypassed Cloudflare after " + i + " seconds", true);

            while (driver.getPageSource().contains("One more step") && driver.getPageSource().contains("Please complete the security check to access")) {
                sleep(1000);
                System.err.println("<@&311178859171282944> Detected an unsolvable captcha.. waiting...");
            }
        }catch (Exception e){
            Logger.send(e.getMessage(), true);
            Logger.send(Arrays.toString(e.getStackTrace()), true);
        }
    }

    public void close() {
        for(String winHandle : driver.getWindowHandles()) {
            driver.switchTo().window(winHandle);
            driver.close();
        }

        preloadSpigot = false;
        preloadMarket = false;

        try{
            Thread.sleep(500);
        }catch (Exception ignored){}

        if(!VerifyUser_Market.isVerifying && !VerifyUser_Spigot.isVerifying){
            SpigotAPIServer.KillProcess("chrome.exe");
        }
    }

    public static void quit() {
        preloadSpigot = false;
        preloadMarket = false;
        driver.quit();

        try{
            Thread.sleep(500);
        }catch (Exception ignored){}

        SpigotAPIServer.KillProcess("chrome.exe");
    }

    public void sleep(long millis) {
        try{
            Thread.sleep(millis);
        }catch (Exception e){
            Logger.send(e.getMessage(), true);
            Logger.send(Arrays.toString(e.getStackTrace()), true);
        }
    }
}
