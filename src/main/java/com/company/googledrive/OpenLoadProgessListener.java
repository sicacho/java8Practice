package com.company.googledrive;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.media.MediaHttpDownloader;
import com.google.api.client.googleapis.media.MediaHttpDownloaderProgressListener;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.HttpClients;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

/**
 * Created by Administrator on 2/6/2017.
 */
public class OpenLoadProgessListener implements MediaHttpDownloaderProgressListener {

  private String newname;
  private OutputStream outputStream;

  public OpenLoadProgessListener(String newname, OutputStream outputStream) {
    this.newname = newname;
    this.outputStream = outputStream;
  }

  @Override
  public void progressChanged(MediaHttpDownloader downloader) throws IOException {
    switch (downloader.getDownloadState()) {
      case MEDIA_IN_PROGRESS:
        break;
      case MEDIA_COMPLETE:
        System.out.println("Download is complete!");
        java.io.File mediaFile = new java.io.File(java.io.File.separator + System.getProperty("user.home")+ java.io.File.separator +"downloadtest"+ java.io.File.separator +newname);
        InputStream inputStream =  new BufferedInputStream(new FileInputStream(mediaFile),16000000);
        try {
          String uploadUrl = getLinkUpload("","");
          System.out.println("Upload At : " + uploadUrl);
          HttpClient client = HttpClients.createDefault();
          HttpPost httppost = new HttpPost(uploadUrl);
          HttpEntity entity =
              MultipartEntityBuilder.create().addBinaryBody("file1",inputStream,
                  ContentType.create("application/x-www-form-urlencoded"),newname+".mp4").build();
          httppost.setEntity(entity);
          HttpResponse httpResponse = client.execute(httppost);
          System.out.println("Status Code = " + httpResponse.getStatusLine().getStatusCode());
          inputStream.close();
          outputStream.close();
          System.out.println("Delete file : " + mediaFile.delete());
        } catch (Exception e) {
          inputStream.close();
          outputStream.close();
          System.out.println("Delete file : " + mediaFile.delete());
          e.printStackTrace();
        }
    }
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
