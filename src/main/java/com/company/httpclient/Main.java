package com.company.httpclient;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by khangtnse60992 on 1/26/2016.
 */
public class Main {
    public static void main(String[] args) {
        String url = "http://www.finjav.com/get_data/changePart/240p/1/46897/thai/mooplayer";

        HttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(url);

        // add request header
        request.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/47.0.2526.106 Safari/537.36");
        request.addHeader("Referer","http://www.finjav.com/movie/player/xTlG65cmz6NmwUcfDwYc");
        request.addHeader("Accept","application/json");
        HttpResponse response = null;
        try {
            response = client.execute(request);
            System.out.println("Response Code : "
                    + response.getStatusLine().getStatusCode());

            BufferedReader rd = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent()));

            StringBuffer result = new StringBuffer();
            String line = "";
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            System.out.println(result.toString());

        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
