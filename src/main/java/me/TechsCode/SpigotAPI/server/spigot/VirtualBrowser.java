package me.TechsCode.SpigotAPI.server.spigot;

import io.github.bonigarcia.wdm.WebDriverManager;
import io.github.bonigarcia.wdm.managers.ChromeDriverManager;
import me.TechsCode.SpigotAPI.server.Logger;
import me.TechsCode.SpigotAPI.server.SpigotAPIServer;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;

import java.sql.Driver;
import java.util.Collections;

public class VirtualBrowser {

    protected ChromeDriver driver;
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
        options.addArguments("--disable-gpu");
        options.addArguments("--disable-infobars");
        options.addArguments("enable-features=NetworkServiceInProcess");

        if(!isWindows() && !isMac()) {
            options.addArguments("--disable-extensions");
            options.addArguments("--headless");
            options.addArguments("--disable-gpu");
            options.addArguments("--no-sandbox");
        }

        this.driver = new ChromeDriver(options);

        try{
            preloadSites("");
        }catch (Exception ignored){}
    }

    public void preloadSites(String url) throws InterruptedException {
        if(preloadSpigot){
            driver.executeScript("popup_window_spigot = window.open('https://www.spigotmc.org/"+ SpigotAPIServer.getRandomInt() +"')");

            try {
                Thread.sleep(12000L);
            } catch (InterruptedException ignored) { }

            driver.executeScript("popup_window_spigot.close()");

            try {
                Thread.sleep(2000L);
            } catch (InterruptedException ignored) { }
        }

        if(preloadMarket){
            driver.executeScript("popup_window_market = window.open('https://www.mc-market.org/"+ SpigotAPIServer.getRandomInt() +"')");

            try {
                Thread.sleep(12000L);
            } catch (InterruptedException ignored) { }

            driver.executeScript("popup_window_market.close()");

            try {
                Thread.sleep(2000L);
            } catch (InterruptedException ignored) { }
        }

        if(!url.isEmpty()){
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

    public void navigate(String url) throws InterruptedException {
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
    }

    public void close() {
        preloadSpigot = false;
        preloadMarket = false;
        driver.close();
    }

    public void sleep(long millis) throws InterruptedException {
        Thread.sleep(millis);
    }
}
