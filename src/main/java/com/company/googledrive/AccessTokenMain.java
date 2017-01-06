package com.company.googledrive;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.util.StringTokenizer;

/**
 * Created by tnkhang on 1/6/2017.
 */
public class AccessTokenMain {
  public static void main(String[] args) {
    try {

      Credential credential = DriveQuickstart.authorize();
      String at = credential.getAccessToken();
      System.out.println(at);
//      Drive service = new Drive.Builder(new NetHttpTransport(), new JacksonFactory(), credential).setApplicationName("app1").build();
      Drive service = new Drive.Builder(new NetHttpTransport(), new JacksonFactory(), null)
              .setHttpRequestInitializer(credential).setApplicationName("isentropic-tape-142809").build();
      String content = "";
      String fileId = "0B6iOGhAfgoxVSE5qWEo1QW1sakk"; //Your video docid in Google Drive
      HttpResponse resp = service.getRequestFactory()
              .buildGetRequest(new GenericUrl("https://docs.google.com/get_video_info?authuser=&app=scaca&access_token="+ at+"&docid=" + fileId)).execute();
      Drive.Files.Get response = service.files().get(fileId);
      System.out.println(response);
      InputStreamReader isr = new InputStreamReader(resp.getContent());
      int ch = 0;
      while (ch != -1) {
        ch = isr.read();
        if (ch != -1) content += (char)ch;
      }
      System.out.println(content);
//Split response in pairs field / value
      StringTokenizer st = new StringTokenizer(content, "&");

      while (st.hasMoreTokens()) {
        String token = st.nextToken();
        if (token.split("=").length == 2) {
          String field = token.split("=")[0];
          String value = URLDecoder.decode(token.split("=")[1], "UTF-8");
          if ("url_encoded_fmt_stream_map".equals(field)) {
            //Prints info of each available video Stream
            String[] urlList = value.split(",");
            for (int i=0; i < urlList.length; i++) {
              System.out.println("Stream #" + i + ":");
              System.out.println("URL: " + URLDecoder.decode(urlList[i].split("&")[1].split("=")[1], "UTF-8"));
              System.out.println("Quality: " + URLDecoder.decode(urlList[i].split("&")[3].split("=")[1], "UTF-8"));
              String type = URLDecoder.decode(urlList[i].split("&")[2], "UTF-8");
              if (type.indexOf(';') > 0) {
                System.out.println("Mime type: " + type.substring(5, type.indexOf(';')));
                System.out.println("Codecs: " + type.substring(type.indexOf("codecs=\"") + 8, type.lastIndexOf('"')));
              }
              else {
                System.out.println("Mime type: " + type.substring(5));
              }
            }
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
