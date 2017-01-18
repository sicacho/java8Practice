package com.company.googledrive;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 * Created by tnkhang on 1/18/2017.
 */
public class OpenloadCopy {


  public static List<UrlDTO> getListUrlVideo(String id) throws NoSuchElementException {
    List<UrlDTO> result = null;
    try {
      URL url = new URL("https://drive.google.com/get_video_info?docid="+id+"&authuser=0");
      HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
      urlConnection.setRequestMethod("GET");
      urlConnection.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36");
      urlConnection.setRequestProperty("Referer","https://drive.google.com/drive/u/0/my-drive");
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
    } catch (MalformedURLException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return result;
  }


  public static void main(String[] args) throws IOException {
//    ApplicationContext ctx = null;
//    ctx = new SpringApplicationBuilder().sources(Main.class).web(false).run(args);
//    MovieService movieService = (MovieService) ctx.getBean("movieService");
//    URL url = new URL("https://r7---sn-8pxuuxa-nboel.googlevideo.com/videoplayback?id=03dbbc55fa47a970&itag=22&source=webdrive&requiressl=yes&ttl=transient&mm=31&mn=sn-8pxuuxa-nboel&ms=au&mv=m&pl=20&mime=video/mp4&lmt=1465612529083976&mt=1484750151&ip=115.77.107.130&ipbits=8&expire=1484764603&sparams=ip,ipbits,expire,id,itag,source,requiressl,ttl,mm,mn,ms,mv,pl,mime,lmt&signature=94A1F1B9D1DF623DF06368A79B4F54FDFBF44147.6B7DF319603C5596BD6CC3166C7A8CC77C9A5819&key=ck2&app=explorer&cpn=PP8hR4iRdWbQNI7e&c=WEB&cver=1.20170117");
//    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
//    urlConnection.setRequestMethod("GET");
//    urlConnection.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36");
//    urlConnection.setRequestProperty("Referer","https://drive.google.com/drive/u/0/my-drive");
//    urlConnection.setRequestProperty("Range","bytes=0-");
//    InputStream inputStream = urlConnection.getInputStream();
//    Files.copy(inputStream, Paths.get("C:\\video\\videotest.mp4"), StandardCopyOption.REPLACE_EXISTING);
    String uploadurl = "https://1fiafqi.oloadcdn.net/uls/7NQMmkINb7r8-j3l";
    URL urlupload = new URL(uploadurl);
    HttpURLConnection httpConn = (HttpURLConnection) urlupload.openConnection();
    httpConn.setUseCaches(false);
    httpConn.setDoOutput(true);
    httpConn.setRequestMethod("POST");
    httpConn.setRequestProperty("Content-Type","multipart/form-data");
    httpConn.setRequestProperty("fileName", "file1");

    // sets file name as a HTTP header

    // opens output stream of the HTTP connection for writing data
    OutputStream outputStream = httpConn.getOutputStream();
    FileInputStream fileInputStream = new FileInputStream("C:\\video\\videotest.mp4");
    // Opens input stream of the file for reading data

    byte[] buffer = new byte[1024];
    int bytesRead = -1;

    System.out.println("Start writing data...");

    while ((bytesRead = fileInputStream.read(buffer)) != -1) {
      outputStream.write(buffer, 0, bytesRead);
    }

    System.out.println("Data was written.");
    outputStream.close();
    fileInputStream.close();

    // always check HTTP response code from server
    int responseCode = httpConn.getResponseCode();
    if (responseCode == HttpURLConnection.HTTP_OK) {
      // reads server's response
      BufferedReader reader = new BufferedReader(new InputStreamReader(
          httpConn.getInputStream()));
      String response = reader.readLine();
      System.out.println("Server's response: " + response);
    } else {
      System.out.println("Server returned non-OK code: " + responseCode);
      BufferedReader reader = new BufferedReader(new InputStreamReader(
          httpConn.getInputStream()));
      String response = reader.readLine();
      System.out.println("Server's response: " + response);

    }
//    HttpEntity entity =
//        MultipartEntityBuilder.create()
//            .addTextBody("field1", "value1").addBinaryBody("file1", new File("/path/file1.txt"),
//            ContentType.create("application/x-www-form-urlencoded"), "file1.txt").build();
  }
}
