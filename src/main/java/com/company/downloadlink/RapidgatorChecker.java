package com.company.downloadlink;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tnkhang on 3/22/2017.
 */
public class RapidgatorChecker {

  static OkHttpClient client = new OkHttpClient();
  static ObjectMapper objectMapper = new ObjectMapper();

  public static void main(String[] args) throws IOException {
    System.getProperties().put("http.proxyHost", "103.14.8.239");
    System.getProperties().put("http.proxyPort", "8080");
    System.setProperty("java.net.useSystemProxies", "true");
    List<LinkCommentDTO> linkCommentDTOs = new ArrayList<>();
    ObjectMapper mapper = new ObjectMapper();
    try {
      linkCommentDTOs = mapper.readValue(new File("C:\\testdielink.json"), new TypeReference<List<LinkCommentDTO>>(){});
    } catch (IOException e) {
      e.printStackTrace();
    }
//    String sessionId = getSessionId("trustme013@gmail.com","pKnTAb");
    linkCommentDTOs.forEach(linkCommentDTO -> {
      boolean linkNotDie = linkCommentDTO.rapidgator_net.stream().allMatch(s -> isNotDie(s));
      if(linkNotDie) {
        linkCommentDTO.rapidgator_net.stream().forEach(System.out::println);
      }
    });
  }

  private static boolean isNotDie(String link) {
    Document doc = null;
    try {
      doc = Jsoup.connect(link)
              .header("Referer","http://www.javlibrary.com/en")
              .header("User-Agent","Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36").get();
    } catch (IOException e) {
      e.printStackTrace();
    }
    if(doc.getElementsByTag("title").get(0).text().startsWith("Download file")) {
      return true;
    }
    return false;
  }

  private static boolean isNotDie(String sessionId, String link) {
    return false;
  }

  private static String getSessionId(String username,String password) throws IOException {
    Response response = null;
    MediaType mediaType = MediaType.parse("multipart/form-data; boundary=---011000010111000001101001");

    RequestBody formBody = new FormBody.Builder()
            .add("username", username).add("password",password)
            .build();
    Request request = new Request.Builder()
            .url("http://rapidgator.net/api/user/login")
            .post(formBody)
            .addHeader("content-type", "multipart/form-data; boundary=---011000010111000001101001")
            .addHeader("cache-control", "no-cache")
            .addHeader("postman-token", "755a1e04-b742-3c9e-530e-2a0dc4a8d78e")
            .build();

    response = client.newCall(request).execute();
    SessionResponse sessionResponse = objectMapper.readValue(response.body().string(),SessionResponse.class);
    return sessionResponse.response.session_id;
  }

  public class SessionResponse {
    public SessionDTO response;
    public Integer response_status;
    public String response_details;
  }

  public class SessionDTO {
    public String session_id;
    public Long expire_date;
    public String traffic_left;
    public Integer reset_in;
  }
}
