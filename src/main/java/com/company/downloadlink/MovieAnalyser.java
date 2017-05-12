package com.company.downloadlink;

import com.company.configuration.RepositoryConfiguration;
import com.company.domain.Movie;
import com.company.repository.MovieRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Administrator on 9/13/2016.
 */
@SpringBootApplication
@ComponentScan
@Import({RepositoryConfiguration.class})
public class MovieAnalyser {

  public static int movieCounter = 0;


  public static void main(String[] args) throws IOException {
    String pageLink = "http://www.javlibrary.com/en/vl_searchbyid.php?&keyword=LXVS&page=";
    List<LinkCommentDTO> linkCommentDTOs = getCommentDTOsFromUrls(pageLink);
    ObjectMapper mapper = new ObjectMapper();
    ApplicationContext ctx = null;
    ctx = new SpringApplicationBuilder().sources(MovieAnalyser.class).web(false).run(args);
    MovieRepository movieRepository = (MovieRepository) ctx.getBean("movieRepository");
    removeExistData(linkCommentDTOs,movieRepository);
//        Iterable<Movie> movies = movieRepository.findAll();
//        List<String> existMovie = mapper.readValue(new File("C:\\exitsMovie.json"), new TypeReference<List<String>>(){});
//        linkCommentDTOs = linkCommentDTOs.stream()
//                .filter(linkCommentDTO -> existMovie
//                                        .stream()
//                                        .noneMatch(s -> s.toUpperCase().equals(linkCommentDTO.code_video)))
//                .collect(Collectors.toList());
    try {
      mapper.writerWithDefaultPrettyPrinter().writeValue(new File("C:\\linkLXVS.json"), linkCommentDTOs);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static void removeExistData(List<LinkCommentDTO> linkCommentDTOs, MovieRepository movieRepository) {
    Iterable<String> codes = movieRepository.movieCodes();
    List<String> codeList = new ArrayList<>();
    codes.forEach(s -> codeList.add(s));
    Iterator<LinkCommentDTO> iterator = linkCommentDTOs.iterator();
    while(iterator.hasNext())
    {
      String value = iterator.next().code_video;
      if (codeList.contains(value))
      {
        System.out.println("Remove : " + value);
        iterator.remove();
      }
    }
  }

  private static List<LinkCommentDTO> getCommentDTOsFromUrls(String pageLink) {
    List<LinkCommentDTO> linkCommentDTOs = new ArrayList<>();

    BlockingQueue<String> pageLinks = new LinkedBlockingQueue<>();
    for (int i = 1; i <= 2; i++) {
      pageLinks.add(pageLink + i);
    }
    BlockingQueue<String> movieLinks = new LinkedBlockingQueue<>();

    MovieLinkProducerThread movieLinkProducerThread = new MovieLinkProducerThread(movieLinks, pageLinks);
    ExecutorService executorService_Producer = Executors.newSingleThreadExecutor();
    executorService_Producer.execute(movieLinkProducerThread);


    MovieLinkConsumerThread movieLinkConsumerThread = new MovieLinkConsumerThread(movieLinks, linkCommentDTOs, pageLinks);
    ExecutorService executorService_Consumer = Executors.newWorkStealingPool();
    executorService_Consumer.execute(movieLinkConsumerThread);
    executorService_Consumer.execute(movieLinkConsumerThread);
    executorService_Consumer.execute(movieLinkConsumerThread);
    executorService_Consumer.execute(movieLinkConsumerThread);
    executorService_Consumer.execute(movieLinkConsumerThread);

    boolean consumerIsRunning = true;
    while (consumerIsRunning) {
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      if (pageLinks.isEmpty()) {
        if (linkCommentDTOs.size() == movieCounter) {
          executorService_Producer.shutdown();
          executorService_Consumer.shutdown();
          consumerIsRunning = false;
        }
      }
    }
    DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    Collections.sort(linkCommentDTOs, (o1, o2) -> {
      int result = 0;
      try {
        result = o2.numberWantIt.compareTo(o1.numberWantIt);
//                result = df.parse(o2.create_date).compareTo(df.parse(o1.create_date));
      } catch (Exception e) {
        e.printStackTrace();
      }
      return result;
    });
    return linkCommentDTOs;
  }

  private static List<String> getCodes(Iterable<Movie> movies) {
    List<String> codes = new ArrayList<>();
    movies.forEach(movie -> codes.add(movie.getCode()));
    return codes;
  }
}
