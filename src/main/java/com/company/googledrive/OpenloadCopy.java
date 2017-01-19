package com.company.googledrive;

import com.company.configuration.Constant;
import com.company.domain.Movie;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.HttpClients;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by tnkhang on 1/18/2017.
 */
public class OpenloadCopy {

  public static List<UrlDTO> getListUrlVideo(String id) throws NoSuchElementException {
    List<UrlDTO> result = null;
    try {
      URL url = new URL("https://drive.google.com/get_video_info?docid=" + id + "&authuser=0");
      HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
      urlConnection.setRequestMethod("GET");
      urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36");
      urlConnection.setRequestProperty("Referer", "https://drive.google.com/drive/u/0/my-drive");
      BufferedReader in = new BufferedReader(
              new InputStreamReader(urlConnection.getInputStream()));
      String inputLine;
      StringBuffer response = new StringBuffer();

      while ((inputLine = in.readLine()) != null) {
        response.append(inputLine);
      }
      in.close();
      String doc = response.toString();
      String[] params = doc.split("&");
      String linkRaw = Arrays.stream(params).
              filter(s -> s.startsWith("fmt_stream_map=")).
              map(s1 -> s1.replace("fmt_stream_map=", "")).findFirst().get();
      String[] links = null;
      try {
        links = URLDecoder.decode(linkRaw, "UTF-8").split(",");
      } catch (UnsupportedEncodingException e) {
        e.printStackTrace();
      }
      if (links != null) {
        result = Arrays.stream(links).
                filter(s -> StringHelper.isNotStartwith(s, Resolution.values())).
                map(s1 -> ConvertEntityToDto.urlDTO(StringEscapeUtils.unescapeJava(s1))).
                collect(Collectors.toList());
        Collections.sort(result, (o1, o2) -> o1.getResolution() - o2.getResolution());
      }
    } catch (MalformedURLException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return result;
  }


  public static void main(String[] args) throws IOException {
    ApplicationContext ctx = null;
    ctx = new SpringApplicationBuilder().sources(Main.class).web(false).run(args);
    MovieService movieService = (MovieService) ctx.getBean("movieService");
    String startFrom = "1";
    String endFrom = "2";
    int start = Integer.parseInt(startFrom);
    int end = Integer.parseInt(endFrom);
    for (int i = start; i < end; i++) {
      Iterable<Movie> movies = movieService.getMovies(i, null, null);
      List<Movie> movieList = new ArrayList<>();
      movies.forEach(movie -> movieList.add(movie));
      for (int j = 0; j < movieList.size(); j++) {
        Movie movie = movieList.get(j);
        Runnable run = new Runnable() {
          @Override
          public void run() {
            try {
              if (movie.getOpenLoadLink() == null) {
                String urlG = movie.getUrlVideos()[0];
                if (movie.isHD()) {
                  List<UrlDTO> urlDTOs = getListUrlVideo(urlG.replace("https://drive.google.com/open?id=", ""));
                  urlG = urlDTOs.get(urlDTOs.size() - 1).getFile();
                  System.out.println("Start upload " + movie.getCode());
                  migrateToOpenload(urlG,movie.getId().toString());
                  System.out.println("Finish upload "  + movie.getCode());
                } else {
                  List<UrlDTO> urlDTOs = getUrlDTOsFromDownloadLink(movie, urlG);
                  if (!urlDTOs.isEmpty()) {
                    urlG = urlDTOs.get(urlDTOs.size() - 1).getFile();
                    System.out.println("Start upload " + movie.getCode());
                    migrateToOpenload(urlG,movie.getId().toString());
                    System.out.println("Finish upload "  + movie.getCode());
                  }
                }
              }
            } catch (IOException e) {
              e.printStackTrace();
            }
          }
        };
        Thread thread = new Thread(run);
        thread.start();
      }
    }
  }

  private static List<UrlDTO> getUrlDTOsFromDownloadLink(Movie movie, String urlG) {
    List<UrlDTO> urlDTOs = getListUrlVideoDownload(urlG.replace("https://drive.google.com/open?id=", ""));
    if (urlDTOs.isEmpty()) {
      urlDTOs = getListUrlVideoDownload(movie.getCopyLinkOriginal());
    }
    return urlDTOs;
  }

  private static void migrateToOpenload(String urlG,String name) throws IOException {
    URL url = new URL(urlG);
    String uploadUrl = getLinkUpload("","");
    System.out.println("Download At : " + urlG);
    System.out.println("Upload At : " + uploadUrl);
    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
    urlConnection.setRequestMethod("GET");
    urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36");
    urlConnection.setRequestProperty("Referer", "https://drive.google.com/drive/u/0/my-drive");
    urlConnection.setRequestProperty("Range", "bytes=0-");
    System.out.println(urlConnection.getHeaderFields().toString());
    BufferedInputStream inputStream = new BufferedInputStream(urlConnection.getInputStream(),4194304);
    HttpClient client = HttpClients.createDefault();
    HttpPost httppost = new HttpPost(uploadUrl);
    HttpEntity entity =
            MultipartEntityBuilder.create().addBinaryBody("file1", inputStream,
                    ContentType.create("application/x-www-form-urlencoded"),name+".mp4").build();
    httppost.setEntity(entity);
    HttpResponse httpResponse = client.execute(httppost);
    System.out.println("Status Code = " + httpResponse.getStatusLine().getStatusCode());
  }

  public static List<UrlDTO> getListUrlVideoDownload(String url) throws NoSuchElementException {
    List<UrlDTO> result = new ArrayList<>();
    String[] params = url.split("\\|");
    url = params[0];
    url = Constant.HOST_DOWNLOAD + url + Constant.PARAM_DOWNLOAD;
    String label = params[1];
    try {
      Connection.Response response = Jsoup.connect(url).userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0").execute();
      Element element = response.parse().getElementById("uc-download-link");
      if (element != null) {
        String downloadLink = response.parse().getElementById("uc-download-link").attr("href");
        downloadLink = Jsoup.connect("https://docs.google.com" + downloadLink)
                .userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
                .referrer("https://docs.google.com" + downloadLink)
                .header("Upgrade-Insecure-Requests", "1")
                .cookies(response.cookies())
                .followRedirects(false)
                .method(Connection.Method.GET).execute().header("location");
        UrlDTO urlDTO = new UrlDTO();
        urlDTO.setFile(downloadLink);
        urlDTO.setLabel(label);
        urlDTO.setType("mp4");
        result.add(urlDTO);
      }
    } catch (Exception e) {
      System.out.println(url);
      e.printStackTrace();
    }
    return result;
  }

  public static String getLinkUpload(String username,String pass) throws IOException {
    String url = "https://api.openload.co/1/file/ul?login=2956f80c44359e25&key=rf8uEike&folder=2789453";
    RestTemplate restTemplate = new RestTemplate();
    String uploadUrl = restTemplate.getForObject(url,String.class);
    ObjectMapper mapper = new ObjectMapper();
    UploadUrlDTO uploadUrlDTO = mapper.readValue(uploadUrl,UploadUrlDTO.class);
    return uploadUrlDTO.getUrlUpload();
  }
}
