package com.company.httpclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by tnkhang on 1/17/2017.
 */
public class TestCookies {
  public static void main(String[] args) {
    try {

      URL url = new URL("https://drive.google.com/get_video_info?authuser=0&docid=0B6iOGhAfgoxVSE5qWEo1QW1sakk");
      HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
      urlConnection.setRequestMethod("GET");
      urlConnection.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36");
      urlConnection.setRequestProperty("Referer","https://drive.google.com/drive/u/0/my-drive");
      urlConnection.setRequestProperty("Cookie","DRIVE_STREAM=wK7B59qhAUc;S=explorer=CXo2OfkLBCZRn-msliOf1Xuat5dc-ySP");
      BufferedReader in = new BufferedReader(
              new InputStreamReader(urlConnection.getInputStream()));
      String inputLine;
      System.out.println(urlConnection.getHeaderFields().toString());
      StringBuffer response = new StringBuffer();

      while ((inputLine = in.readLine()) != null) {
        response.append(inputLine);
      }
      in.close();
      System.out.println(response.toString());

    } catch (MalformedURLException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
