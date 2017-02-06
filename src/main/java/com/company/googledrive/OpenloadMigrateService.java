package com.company.googledrive;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.HttpClients;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Administrator on 1/21/2017.
 */
public class OpenloadMigrateService implements MigrateService {
  @Override
  public void migrate(String urlG,String newname) throws Exception{
    String uploadUrl = getLinkUpload("","");
    URL url = new URL(urlG);
    System.out.println("Download At : " + urlG);
    System.out.println("Upload At : " + uploadUrl);
    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
    urlConnection.setRequestMethod("GET");
    urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36");
    urlConnection.setRequestProperty("Referer", "https://drive.google.com/drive/u/0/my-drive");
    urlConnection.setRequestProperty("Range", "bytes=0-");
    System.out.println(urlConnection.getHeaderFields().toString());
    BufferedInputStream inputStream = new BufferedInputStream(urlConnection.getInputStream(),16000000);
    HttpClient client = HttpClients.createDefault();
    HttpPost httppost = new HttpPost(uploadUrl);
    HttpEntity entity =
        MultipartEntityBuilder.create().addBinaryBody("file1", inputStream,
            ContentType.create("application/x-www-form-urlencoded"),newname+".mp4").build();
    httppost.setEntity(entity);
    HttpResponse httpResponse = client.execute(httppost);
    System.out.println("Status Code = " + httpResponse.getStatusLine().getStatusCode());
  }

  private String getLinkUpload(String username,String pass) throws IOException {
    String url = "https://api.openload.co/1/file/ul?login=2956f80c44359e25&key=rf8uEike&folder=2789453";
    RestTemplate restTemplate = new RestTemplate();
    String uploadUrl = restTemplate.getForObject(url,String.class);
    ObjectMapper mapper = new ObjectMapper();
    UploadUrlDTO uploadUrlDTO = mapper.readValue(uploadUrl,UploadUrlDTO.class);
    return uploadUrlDTO.getUrlUpload();
  }
}
