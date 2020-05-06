package me.TechsCode.SpigotAPI.server.spigot;

import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.Cookie;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import me.TechsCode.SpigotAPI.logging.ConsoleColor;
import me.TechsCode.SpigotAPI.logging.Logger;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Level;

public class VirtualBrowser {

    private WebClient webClient;
    private HashMap<String, String> cookies;

    public VirtualBrowser() {
        webClient = new WebClient(BrowserVersion.CHROME);
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setTimeout(15000);
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setRedirectEnabled(true);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setPrintContentOnFailingStatusCode(false);

        this.cookies = new HashMap<>();

        java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);
    }

    public String request(String url, HttpMethod httpMethod, NameValuePair... parameters){
        try {
            WebRequest wr = new WebRequest(new URL(url), httpMethod);

            // Inject Cookies
            cookies.forEach((key, value) -> webClient.getCookieManager().addCookie(new Cookie("spigotmc.org", key, value)));

            // Inject Parameters
            wr.setRequestParameters(Arrays.asList(parameters));

            HtmlPage htmlPage = webClient.getPage(wr);
            String xml = htmlPage.asXml();

            if(htmlPage.asText().contains("DDoS protection by Cloudflare")){
                Logger.log(ConsoleColor.YELLOW + "[CloudFlare] " + ConsoleColor.GREEN + "Bypassing Cloud Flare..");

                try {
                    Thread.sleep(9000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // Recursively trying again. Cloudflare should be bypassed next time
                return request(url, httpMethod, parameters);
            }

            htmlPage = null;
            webClient.close();

            return xml;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void close(){
        webClient.getCurrentWindow().getJobManager().removeAllJobs();
        webClient.close();
        webClient = null;
        System.gc();
    }
}
