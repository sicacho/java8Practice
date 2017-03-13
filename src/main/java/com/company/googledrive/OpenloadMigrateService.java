package com.company.googledrive;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.media.MediaHttpDownloader;
import com.google.api.client.http.*;
import com.google.api.client.http.apache.ApacheHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.HttpClients;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
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
public class OpenloadMigrateService implements MigrateService {

  GoogleDriveService googleDriveService = new GoogleDriveService();

  @Override
  public void migrate(String urlG, String newname) throws Exception {

    if (!existedMovies().contains(newname)) {
      Credential credential = googleDriveService.authorize();
      Drive service = new Drive.Builder(new NetHttpTransport(), new JacksonFactory(), null)
              .setHttpRequestInitializer(credential).setApplicationName("432161060989").build();
      com.google.api.client.http.HttpResponse response = service.getRequestFactory().buildGetRequest(new GenericUrl(urlG)).execute();
      System.out.println("Download At : " + urlG);
      InputStream inputStreamDoc = response.getContent();
      String doc = "";
      int ch = 0;
      while (ch != -1) {
        ch = inputStreamDoc.read();
        if (ch != -1) doc += (char) ch;
      }
      List<UrlDTO> result = new ArrayList<>();
      result = parseFromHtml(doc, result);
      System.out.println("Download at : " + result.get(0).getFile());
      java.io.File file = new java.io.File(java.io.File.separator + System.getProperty("user.home") + java.io.File.separator + "downloadtest" + java.io.File.separator + newname);
      file.createNewFile();
      OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file));
      try {
        HttpTransport httpTransport = new ApacheHttpTransport();
        MediaHttpDownloader downloader = new MediaHttpDownloader(httpTransport, httpTransport.createRequestFactory(new HttpRequestInitializer() {
          @Override
          public void initialize(HttpRequest request) throws IOException {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.set("cookie", response.getHeaders().getHeaderStringValues("set-cookie"));
            request.setHeaders(httpHeaders);
          }
        }).getInitializer());
        downloader.setChunkSize(4000000);
        downloader.setProgressListener(new OpenLoadProgessListener(newname, outputStream));
        long startTime = System.currentTimeMillis();
        downloader.download(new GenericUrl(result.get(0).getFile()), outputStream);
        long endTime = System.currentTimeMillis();
        System.out.println("Download : " + (startTime - endTime));
      } catch (Exception e) {
        e.printStackTrace();
        outputStream.close();
        System.out.println("Delete file : " + file.delete());
      }
    } else {
      System.out.println("Ignore " + newname);
    }
  }


  private List<UrlDTO> parseFromHtml(String doc, List<UrlDTO> result) {
    Document document = Jsoup.parse(doc);
    Elements scriptElements = document.getElementsByTag("script");
    String text = scriptElements.get(scriptElements.size() - 2).dataNodes().get(0).getWholeData();
    Pattern pattern = Pattern.compile("(?=fmt_stream_map\\\",\\\")(.*)(?=\\\")");
    Matcher matcher = pattern.matcher(text);
    if (matcher.find()) {
      String[] listUrl = matcher.group(1).replace("\"", "").split("\\,");
      result = Arrays.stream(listUrl).
              filter(s -> StringHelper.isNotStartwith(s, Resolution.values())).
              map(s1 -> ConvertEntityToDto.urlDTO(StringEscapeUtils.unescapeJava(s1))).
              collect(Collectors.toList());
      Collections.reverse(result);
    }
    return result;
  }

  public List<String> existedMovies() throws IOException {
    String url = "https://api.openload.co/1/file/listfolder?login=2956f80c44359e25&key=rf8uEike&folder=2789453";
    RestTemplate restTemplate = new RestTemplate();
    String listFolderUrl = restTemplate.getForObject(url, String.class);
    ObjectMapper mapper = new ObjectMapper();
    ListFileDTO listFileDTO = mapper.readValue(listFolderUrl, ListFileDTO.class);
    return listFileDTO.getNames();
  }
}
