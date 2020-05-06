package me.TechsCode.SpigotAPI.server.spigot;

import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class AuthenticationException extends Exception {

    private String xml;

    public AuthenticationException(String xml) {
        this.xml = xml;
    }

    public String getPage() {
        return xml;
    }
}
