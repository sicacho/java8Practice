package com.company.googledrive;

import com.company.configuration.Constant;
import com.company.domain.Movie;
import com.company.repository.MovieRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.company.googledrive.CopyDriveMain.getFileList;

/**
 * Created by tnkhang on 1/18/2017.
 */
public class OpenloadCopy {

  public static void main(String[] args) throws IOException, GeneralSecurityException {
    ApplicationContext ctx = null;
    ctx = new SpringApplicationBuilder().sources(Main.class).web(false).run(args);
    MovieService movieService = (MovieService) ctx.getBean("movieService");
    MovieRepository movieRepository = (MovieRepository) ctx.getBean("movieRepository");
    GoogleDriveService googleDriveService = new GoogleDriveService();
    Credential credential = googleDriveService.authorize();
    Drive service = new Drive.Builder(new NetHttpTransport(), new JacksonFactory(), null)
        .setHttpRequestInitializer(credential).setApplicationName("432161060989").build();
    List<String> ids = new ArrayList<>();
    getFileList(ids,service,"0B28pDkSFd-VZSjNCRERMM29EZ2M","");
    getFileList(ids,service,"0B28pDkSFd-VZdllaanJmRnhQLUk","");
    Long count = movieRepository.movieCount();
    String startFrom = "1";
    String endFrom = "213";
    int start = Integer.parseInt(startFrom);
    int end = Integer.parseInt(endFrom);
    ExecutorService executor = Executors.newFixedThreadPool(50);
    MigrateService migrateService = new GDriveMigrateService();
    System.out.println("Start : " + startFrom);
    System.out.println("End : " + endFrom);
    System.out.println("IDs Size : " + ids.size());
    while (ids.size()<count) {

      for (int i = start; i <= end; i++) {
        Iterable<Movie> movies = movieService.getMovies(i, 10, "date");
        List<Movie> movieList = new ArrayList<>();
        List<String> finalIds = ids;
        movies.forEach(movie -> {
          if(!finalIds.contains(movie.getId())) {
            movieList.add(movie);
          }
        });
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
                java.io.File file = new java.io.File(java.io.File.separator + System.getProperty("user.home")+ java.io.File.separator +"downloadtest"+ java.io.File.separator + movie.getId().toString());
                if(file.exists()) {
                  file.delete();
                }
                e.printStackTrace();
              }
            }
          };
          executor.execute(run);
        }
      }
      executor.shutdown();
      service = new Drive.Builder(new NetHttpTransport(), new JacksonFactory(), null)
          .setHttpRequestInitializer(credential).setApplicationName("432161060989").build();
      ids = new ArrayList<>();
      getFileList(ids,service,"0B28pDkSFd-VZSjNCRERMM29EZ2M","");
      getFileList(ids,service,"0B28pDkSFd-VZdllaanJmRnhQLUk","");
    }

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

  public static List<String> existedMovies() throws IOException {
    String url = "https://api.openload.co/1/file/listfolder?login=2956f80c44359e25&key=rf8uEike&folder=2789453";
    RestTemplate restTemplate = new RestTemplate();
    String listFolderUrl = restTemplate.getForObject(url, String.class);
    ObjectMapper mapper = new ObjectMapper();
    ListFileDTO listFileDTO = mapper.readValue(listFolderUrl, ListFileDTO.class);
    return listFileDTO.getNames();
  }
}
