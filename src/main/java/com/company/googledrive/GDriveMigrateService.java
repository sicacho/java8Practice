package com.company.googledrive;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.media.MediaHttpDownloader;
import com.google.api.client.http.*;
import com.google.api.client.http.apache.ApacheHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.Permission;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by Administrator on 1/21/2017.
 */
public class GDriveMigrateService implements MigrateService {

  GoogleDriveService googleDriveService = new GoogleDriveService();
  Permission permission = new Permission();

  @Override
  public void migrate(String urlG, String newname) throws Exception {
    Credential credential = googleDriveService.authorize();
    Drive service = new Drive.Builder(new NetHttpTransport(), new JacksonFactory(), null)
        .setHttpRequestInitializer(credential).setApplicationName("432161060989").build();
    java.io.File file = null;
    OutputStream outputStream = null;
    try {
      com.google.api.client.http.HttpResponse response= service.getRequestFactory().buildGetRequest(new GenericUrl(urlG)).execute();
      System.out.println("Download At : " + urlG);
      InputStream inputStreamDoc = response.getContent();
      String doc = "";
      int ch = 0;
      while (ch != -1) {
        ch = inputStreamDoc.read();
        if (ch != -1) doc += (char)ch;
      }
      List<UrlDTO> result = new ArrayList<>();
      result = parseFromHtml(doc, result);
      System.out.println("Download at : " + result.get(result.size()-1).getFile());
      HttpTransport httpTransport = new ApacheHttpTransport();
      MediaHttpDownloader downloader = new MediaHttpDownloader(httpTransport, httpTransport.createRequestFactory(new HttpRequestInitializer() {
        @Override
        public void initialize(HttpRequest request) throws IOException {
          HttpHeaders httpHeaders = new HttpHeaders();
          httpHeaders.set("cookie",response.getHeaders().getHeaderStringValues("set-cookie"));
          request.setHeaders(httpHeaders);
        }
      }).getInitializer());
      downloader.setChunkSize(4000000);
      file = new java.io.File(java.io.File.separator + System.getProperty("user.home")+ java.io.File.separator +"downloadtest"+ java.io.File.separator +newname);
      file.createNewFile();
      outputStream = new BufferedOutputStream(new FileOutputStream(file),16000000);
      downloader.setProgressListener(new CustomProgressListener(newname,service,outputStream));
      long startTime = System.currentTimeMillis();
      downloader.download(new GenericUrl(result.get(0).getFile()),outputStream);
      long endTime = System.currentTimeMillis();
      System.out.println("Download : " + (startTime - endTime));
    } catch (Exception e) {
      if(file.exists()) {
        outputStream.close();
        file.delete();
      }
      e.printStackTrace();
    }

  }


  private List<UrlDTO> parseFromHtml(String doc, List<UrlDTO> result) {
    Document document = Jsoup.parse(doc);
    Elements scriptElements = document.getElementsByTag("script");
    String text = scriptElements.get(scriptElements.size()-2).dataNodes().get(0).getWholeData();
    Pattern pattern = Pattern.compile("(?=fmt_stream_map\\\",\\\")(.*)(?=\\\")");
    Matcher matcher = pattern.matcher(text);
    if (matcher.find())
    {
      String[] listUrl = matcher.group(1).replace("\"","").split("\\,");
      result = Arrays.stream(listUrl).
          filter(s -> StringHelper.isNotStartwith(s, Resolution.values())).
          map(s1 -> ConvertEntityToDto.urlDTO(StringEscapeUtils.unescapeJava(s1))).
          collect(Collectors.toList());
      Collections.reverse(result);
    }
    return result;
  }

}
