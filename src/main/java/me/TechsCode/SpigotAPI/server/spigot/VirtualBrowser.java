package me.TechsCode.SpigotAPI.server.spigot;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.Collections;

public class VirtualBrowser {

    protected ChromeDriver browser;

    public VirtualBrowser() {
        WebDriverManager.chromedriver().setup();
        System.setProperty(ChromeDriverService.CHROME_DRIVER_SILENT_OUTPUT_PROPERTY, "true");

        ChromeOptions options = new ChromeOptions();

        options.addArguments("--disable-blink-features=AutomationControlled");
        options.setExperimentalOption("useAutomationExtension", false);
        options.setExperimentalOption("excludeSwitches", Collections.singletonList("enable-automation"));

        this.browser = new ChromeDriver(options);
    }

    public void navigate(String url) throws InterruptedException {
        browser.get(url);

        // Bypass Cloudflare
        int i = 0;
        while (browser.getPageSource().contains("This process is automatic. Your browser will redirect to your requested content shortly.")) {
            if (i == 0)
                System.out.println("Cloudflare detected. Bypassing it now...");

            sleep(1000);

            i++;
        }

        if (i != 0)
            System.out.println("Bypassed Cloudflare after " + i + " seconds");

        while (browser.getPageSource().contains("One more step") && browser.getPageSource().contains("Please complete the security check to access")) {
            sleep(1000);
            System.err.println("Detected an unsolvable captcha.. waiting...");
        }

        if (i > 10 || browser.getPageSource().contains("ERR_TOO_MANY_REDIRECTS")) {
            sleep(5000);

            System.out.println("Taking too long... retrying to access " + url);
            navigate(url);
        }
    }

    public void close() {
        browser.close();
    }

    public void sleep(long millis) throws InterruptedException {
        Thread.sleep(millis);
    }
}
