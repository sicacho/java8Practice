package com.company.googledrive;

import com.company.configuration.RepositoryConfiguration;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by khangtn on 5/29/2017.
 */
@SpringBootApplication
@ComponentScan
@Import({RepositoryConfiguration.class})
public class VideoInserter {
  public static void main(String[] args) throws IOException, InterruptedException {
    ApplicationContext ctx = null;
    ctx = new SpringApplicationBuilder().sources(Main.class).web(false).run(args);
    MovieService movieService = (MovieService) ctx.getBean("movieService");
    Drive service = null;
    FileList result = null;
    List<MovieDTO> movieDTOs = new ArrayList<>();
    service = DriveQuickstart.getDriveService();
    List<File> files = new ArrayList<>();
    getFileNameList(files, service, "0B6iOGhAfgoxVQVR6c1ZiZ3pKOVk", "");
    String rootUrl = "http://www.xvideos.com/";
    BlockingQueue<File> fileBlockingQueue = new LinkedBlockingQueue<>();
    files.stream().forEach(file -> {
      try {
        fileBlockingQueue.put(file);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    });
    System.out.println(fileBlockingQueue.size());
    Callable callable = () -> {
      while (fileBlockingQueue.size()>0) {
        File file = fileBlockingQueue.take();
        try {
          String originalUrl = rootUrl + file.getName() + "/124_4";
          Document doc = Jsoup.connect(originalUrl).followRedirects(true)
              .header("Referer", "http://www.javlibrary.com/en")
              .header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36").get();
          String title = doc.getElementsByAttributeValue("property", "og:title").get(0).attr("content");
          List<String> tags = doc.getElementsByClass("nu").stream().filter(element -> !element.text().equals("more tags")).map(Element::text).collect(Collectors.toList());
          MovieDTO movieDTO = new MovieDTO();
          movieDTO.googleId = file.getId();
          movieDTO.name = "uncen";
          movieDTO.description = title;
          movieDTO.types = tags;
          movieDTO.uncen = true;
          movieDTO.image = movieDTO.googleId;
          movieDTO.actors = Arrays.asList("Unknow");
          movieDTO.studio = "User upload";
          movieDTO.isHD = file.getVideoMediaMetadata().getHeight() > 480 ? true : false;
          System.out.println("Add : " + movieDTO.toString());
          movieDTOs.add(movieDTO);
        } catch (Exception e) {
          if(e.getMessage().equals("HTTP error fetching URL")){
            System.out.println("Video die " + file.getName());
          } else {
            e.printStackTrace();
            fileBlockingQueue.put(file);
          }
        }

      }
      return "Done";
    };
    ExecutorService executorService = Executors.newFixedThreadPool(10);
    List<Callable<String>> callables = new ArrayList<>();
    IntStream.range(0,10).forEach(value -> callables.add(callable));
    executorService.invokeAll(callables);
    movieService.insertMovies(movieDTOs);

  }

  public static void getFileNameList(List<File> files, Drive service, String folder, String nextPageToken) throws IOException {
    FileList fileList = null;
    if (nextPageToken != null && !nextPageToken.equals("")) {
      fileList = service.files().list().setQ("'" + folder + "' in parents")
          .setPageSize(100)
          .setPageToken(nextPageToken)
          .setFields("nextPageToken, files(id, name, videoMediaMetadata)")
          .execute();
    } else {
      fileList = service.files().list().setQ("'" + folder + "' in parents")
          .setPageSize(100)
          .setFields("nextPageToken, files(id, name, videoMediaMetadata)")
          .execute();
    }
    fileList.getFiles().stream().forEach(file -> files.add(file));
    if (fileList.getNextPageToken() != null) {
      getFileNameList(files, service, folder, fileList.getNextPageToken());
    }
  }
}
