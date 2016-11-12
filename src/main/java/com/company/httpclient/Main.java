package com.company.httpclient;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.Cookie;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by khangtnse60992 on 1/26/2016.
 */
public class Main {
    public static void main(String[] args) {
        String url = "http://hitodzuma69.net/tag/censored-hd/page/2/";

        try (final WebClient webClient = new WebClient(BrowserVersion.CHROME)) {
            webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
            webClient.waitForBackgroundJavaScript(10000);
            webClient.getOptions().setUseInsecureSSL(true);
            webClient.setAjaxController(new NicelyResynchronizingAjaxController());
            webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
            webClient.getOptions().setThrowExceptionOnScriptError(false);
            webClient.getOptions().setCssEnabled(false);
            webClient.getOptions().setJavaScriptEnabled(true);
            webClient.getOptions().setRedirectEnabled(true);
            HtmlPage htmlPage = webClient.getPage(url);
            // DDOS protection
            try {
                Thread.sleep(6000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // restore
                // interrupted
                // status
            }
//            URL outputURL = webClient.getCurrentWindow().getEnclosedPage().getUrl();
//            Map<String, String> cookiesMap = new HashMap<String, String>();
//            for (Cookie cookie : webClient.getCookieManager().getCookies()) {
//                cookiesMap.put(cookie.getName(), cookie.getValue());
//            }
//            HtmlPage page2 = webClient.getPage(outputURL);
            Document doc = null;
            HtmlPage page3 = webClient.getPage(new URL("http://hitodzuma69.net/hd-rdt-265-%e5%90%91%e3%81%8b%e3%81%84%e3%81%ae%e9%83%a8%e5%b1%8b%e3%81%ae%e7%aa%93%e3%81%8b%e3%82%89%e8%a6%97%e3%81%8f%e5%b7%a8%e4%b9%b3%e7%be%8e%e5%a5%b3%e3%81%ae%e7%9d%80%e6%9b%bf%e3%81%88/"));
            doc = Jsoup.parse(((HtmlPage) page3).asXml());
            System.out.println(doc.html());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }



    }
}
