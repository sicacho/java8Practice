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
//    String id = urlG.replace("https://drive.google.com/open?id=","");
//    urlG = "https://docs.google.com/get_video_info?"+"&docid=" + id;
    Credential credential = googleDriveService.authorize();
    Drive service = new Drive.Builder(new NetHttpTransport(), new JacksonFactory(), null)
        .setHttpRequestInitializer(credential).setApplicationName("432161060989").build();
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
//    HttpClientContext context = HttpClientContext.create();
//    List<UrlDTO> result = getUrlDTOs(urlG, context);
//    HttpResponse responseStreaming = getStreamingRespone(context, result);
//    BufferedInputStream inputStream = new BufferedInputStream(responseStreaming.getEntity().getContent(),16000000);
//    String contentLength  = responseStreaming.getFirstHeader("content-length").getValue();
//    System.out.println("Content-length : " + contentLength);
//    java.net.HttpCookie.parse(response.getHeaders().getCookie());
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
    java.io.File file = new java.io.File(java.io.File.separator + System.getProperty("user.home")+ java.io.File.separator +"downloadtest"+ java.io.File.separator +newname);
    file.createNewFile();
    OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file),16000000);
    downloader.setProgressListener(new CustomProgressListener(newname,service,outputStream));
    long startTime = System.currentTimeMillis();
    downloader.download(new GenericUrl(result.get(0).getFile()),outputStream);
    long endTime = System.currentTimeMillis();
    System.out.println("Download : " + (startTime - endTime));
  }

  private List<UrlDTO> parseFromInputStream(String doc,List<UrlDTO> result) {
    String[] params = doc.split("&");
    String linkRaw = Arrays.stream(params).
        filter(s -> s.startsWith("fmt_stream_map=")).
        map(s1 -> s1.replace("fmt_stream_map=","")).findFirst().get();
    String[] links = null;
    try {
      links = URLDecoder.decode(linkRaw,"UTF-8").split(",");
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    if(links!=null) {
      result = Arrays.stream(links).
          filter(s -> StringHelper.isNotStartwith(s, Resolution.values())).
          map(s1 -> ConvertEntityToDto.urlDTO(StringEscapeUtils.unescapeJava(s1))).
          collect(Collectors.toList());
      Collections.sort(result,(o1, o2) -> o1.getResolution()-o2.getResolution());
    }
    return result;
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

  private HttpResponse getStreamingRespone(HttpClientContext context, List<UrlDTO> result) throws IOException {
    HttpClient httpClientStreaming = HttpClients.createDefault();
    HttpGet getStreaming = new HttpGet(result.get(1).getFile());
    System.out.println("Start streaming : " + result.get(1).getFile());
    Header header = new BasicHeader("Range","bytes=0-");
    getStreaming.setHeader(header);
    return httpClientStreaming.execute(getStreaming,context);
  }

  private List<UrlDTO> getUrlDTOs(String urlG, HttpClientContext context) throws IOException {
    CloseableHttpClient httpClient = HttpClients.createDefault();
    HttpGet get = new HttpGet(urlG);
    BasicCookieStore cookieStore = new BasicCookieStore();
    context.setCookieStore(cookieStore);
    CloseableHttpResponse response = httpClient.execute(get,context);
    InputStream inputStreamDoc = response.getEntity().getContent();
    String doc = "";
    int ch = 0;
    while (ch != -1) {
      ch = inputStreamDoc.read();
      if (ch != -1) doc += (char)ch;
    }
    Document document = Jsoup.parse(doc);
    List<UrlDTO> result = null;
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
    response.close();
    httpClient.close();
    return result;
  }
}
