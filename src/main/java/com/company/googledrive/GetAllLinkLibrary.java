package com.company.googledrive;

import com.company.domain.Movie;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.Jsoup;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by khang on 3/26/2017.
 */
public class GetAllLinkLibrary {
  public static void main(String[] args) {
//    System.getProperties().put("http.proxyHost", "35.162.153.131");
//    System.getProperties().put("http.proxyPort", "3128");
//    System.setProperty("java.net.useSystemProxies", "true");
    ApplicationContext ctx = null;
    ctx = new SpringApplicationBuilder().sources(Main.class).web(false).run(args);
    MovieService movieService = (MovieService) ctx.getBean("movieService");
    BlockingQueue<LibraryLinkDTO> codes = new LinkedBlockingDeque<>();
    List<LibraryLinkDTO> libraryMovieLinks = new ArrayList<>();
    for (int i = 1; i <= 138; i++) {
      Iterable<Movie> movies = movieService.getMovies(i,null,null);
      movies.forEach(movie -> codes.add(new LibraryLinkDTO(movie.getId(),null,movie.getCode())));
    }
    final int movieCounter = codes.size();
    System.out.println("Total Movie : " + movieCounter);

    Runnable runnable = new Runnable() {
      @Override
      public void run() {
        while (codes.size()>0) {
          LibraryLinkDTO movie = codes.poll();
          try {
            System.out.println("Producer "+ Thread.currentThread().getName() + " is running with " + movie.code);
            String link = Jsoup.connect("http://www.javlibrary.com/en/vl_searchbyid.php?keyword="+movie.code)
                .header("Referer","http://www.javlibrary.com/en")
                .header("User-Agent","Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36").get().location();
            if(!link.contains("searchbyid")) {
              libraryMovieLinks.add(new LibraryLinkDTO(movie.id,movie.code,link));
              System.out.println("Success add movie : " + movie.code + "-" + link);
            }
          } catch (Exception e) {
            System.out.println(e.getMessage());
            try {
              codes.put(movie);
            } catch (InterruptedException e1) {
              e1.printStackTrace();
            }
          }
        }
      }
    };

    ExecutorService executorService = Executors.newFixedThreadPool(5);
    executorService.execute(runnable);
    executorService.execute(runnable);
    executorService.execute(runnable);
    executorService.execute(runnable);

    boolean consumerIsRunning = true;
    while (consumerIsRunning) {
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      if(codes.isEmpty()) {
          executorService.shutdown();
          consumerIsRunning = false;
      }
    }
    ObjectMapper mapper = new ObjectMapper();
    try {
      mapper.writerWithDefaultPrettyPrinter().writeValue(new File("libraryMapping.json"), libraryMovieLinks);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
