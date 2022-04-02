package me.TechsCode.SpigotAPI.server.browsers;

import io.github.bonigarcia.wdm.WebDriverManager;
import me.TechsCode.SpigotAPI.server.Logger;
import me.TechsCode.SpigotAPI.server.SpigotAPIServer;
import org.apache.commons.lang3.SystemUtils;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class VirtualBrowser {

    protected static ChromeDriver driver;
    private static boolean preload = false;

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
        options.addArguments("--disable-extensions");
        options.addArguments("--no-sandbox");

        Map<String, Object> prefs = new HashMap<String, Object>();
        prefs.put("credentials_enable_service", false);
        prefs.put("profile.password_manager_enabled", false);
        options.setExperimentalOption("prefs", prefs);

        String desktop = System.getenv("XDG_CURRENT_DESKTOP");
        if(!isWindows() && !isMac() && desktop != null) {
            Logger.error("This app can only be run on a system with a desktop environment!!!", true);
            return;
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
        if (preload) {
            driver.executeScript("spigot_popup_window = window.open('https://www.spigotmc.org/" + SpigotAPIServer.getRandomInt() + "');");

            try {
                Thread.sleep(14000L);
            } catch (InterruptedException e) {
                Logger.send(e.getMessage(), true);
                Logger.send(Arrays.toString(e.getStackTrace()), true);
            }

            if (driver.getWindowHandles().stream().findFirst().isPresent()){
                String firstWindow = driver.getWindowHandles().stream().findFirst().get();
                driver.switchTo().window(firstWindow);
            }

        }

        if (!url.isEmpty()) {
            navigate(url);
        }
    }

    public static void enableSpigotPreload() {
        preload = true;
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

            while (driver.getPageSource().contains("429 Too Many Requests")) {
                Logger.send("Too Many Requests", true);
                sleep(2000);
                navigate(url);
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

        preload = false;

        try{
            Thread.sleep(500);
        }catch (Exception ignored){}
    }

    public static void quit() {
        preload = false;
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
