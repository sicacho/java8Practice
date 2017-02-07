package com.company.googledrive;

import com.company.configuration.Constant;
import com.company.domain.Movie;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by tnkhang on 1/18/2017.
 */
public class OpenloadCopy {

  public static void main(String[] args) throws IOException {
    ApplicationContext ctx = null;
    ctx = new SpringApplicationBuilder().sources(Main.class).web(false).run(args);
    MovieService movieService = (MovieService) ctx.getBean("movieService");
    String startFrom = "15";
    String endFrom = "112";
    int start = Integer.parseInt(startFrom);
    int end = Integer.parseInt(endFrom);
    ExecutorService executor = Executors.newFixedThreadPool(50);
    MigrateService migrateService = new OpenloadMigrateService();
    System.out.println("Start : " + startFrom);
    System.out.println("End : " + endFrom);
    for (int i = start; i <= end; i++) {
      Iterable<Movie> movies = movieService.getMovies(i, null, null);
      List<Movie> movieList = new ArrayList<>();
      movies.forEach(movie -> movieList.add(movie));
      for (int j = 0; j < movieList.size(); j++) {
        Movie movie = movieList.get(j);
        Runnable run = new Runnable() {
          @Override
          public void run() {
            try {
              String urlG = movie.getUrlVideos()[0];
              System.out.println("Start upload " + movie.getCode());
              migrateService.migrate(urlG, movie.getId().toString());
              System.out.println("Finish upload " + movie.getCode());
            } catch (Exception e) {
              e.printStackTrace();
            }
          }
        };
        executor.execute(run);
      }
    }
    executor.shutdown();
/**
 * Movies still not upload
 */
//    BlockingQueue<Movie> movieBlockingQueue = new LinkedBlockingQueue<>();
//    Iterable<Movie> moviesNull = movieService.getMoviesHaveOriginalLinkNull();
//    moviesNull.forEach(movie -> {
//      try {
//        movieBlockingQueue.put(movie);
//      } catch (InterruptedException e) {
//        e.printStackTrace();
//      }
//    });
//    System.out.println("Movie blocking queue : " + movieBlockingQueue.size());

//    Runnable run = new Runnable() {
//
//      @Override
//      public void run() {
//        while (movieBlockingQueue.size()!=0) {
//          Movie movie = null;
//          try {
//            movie = movieBlockingQueue.take();
//            System.out.println("Block size " + movieBlockingQueue.size());
//          } catch (InterruptedException e) {
//            e.printStackTrace();
//          }
//          try {
//
//            String urlG = movie.getUrlVideos()[0];
//            System.out.println("Start upload " + movie.getCode());
//            migrateService.migrate(urlG,movie.getId().toString());
//            System.out.println("Finish upload "  + movie.getCode());
//          } catch (Exception e) {
//            e.printStackTrace();
//            System.out.println("Error at : " + movie.getCode());
//            try {
//              movieBlockingQueue.put(movie);
//            } catch (InterruptedException e1) {
//              e1.printStackTrace();
//            }
//          }
//        }
//
//      }
//    };
//    for(int i=0;i<50;i++){
//      executor.execute(run);
//    }
//    executor.shutdown();
  }

  private static List<UrlDTO> getUrlDTOsFromDownloadLink(Movie movie, String urlG) {
    List<UrlDTO> urlDTOs = getListUrlVideoDownload(urlG.replace("https://drive.google.com/open?id=", ""));
    if (urlDTOs.isEmpty()) {
      urlDTOs = getListUrlVideoDownload(movie.getCopyLinkOriginal());
    }
    return urlDTOs;
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


}
