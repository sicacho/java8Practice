package com.company.downloadlink;

import com.company.configuration.RepositoryConfiguration;
import com.company.domain.Movie;
import com.company.googledrive.MovieService;
import com.company.repository.MovieRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

/**
 * Created by Administrator on 9/13/2016.
 */
@SpringBootApplication
@ComponentScan
@Import({RepositoryConfiguration.class})
public class MovieAnalyser {

    public static int movieCounter = 0;


    public static void main(String[] args) {
        String pageLink = "http://www.javlibrary.com/en/vl_genre.php?&mode=&g=aroa&page=";
        List<LinkCommentDTO> linkCommentDTOs = getCommentDTOsFromUrls(pageLink);
        ApplicationContext ctx = null;
        ctx = new SpringApplicationBuilder().sources(MovieAnalyser.class).web(false).run(args);
        MovieRepository movieRepository = (MovieRepository) ctx.getBean("movieRepository");
        Iterable<Movie> movies = movieRepository.findAll();
        List<String> existMovie = getCodes(movies);
        linkCommentDTOs = linkCommentDTOs.stream()
                .filter(linkCommentDTO -> existMovie
                                        .stream()
                                        .noneMatch(s -> s.toUpperCase().equals(linkCommentDTO.code_video)))
                .collect(Collectors.toList());
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File("C:\\linklist_drama.json"), linkCommentDTOs);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static List<LinkCommentDTO> getCommentDTOsFromUrls(String pageLink) {
        List<LinkCommentDTO> linkCommentDTOs = new ArrayList<>();

        BlockingQueue<String> pageLinks = new LinkedBlockingQueue<>();
        for(int i = 1;i < 202;i++) {
            pageLinks.add(pageLink+i);
        }
        BlockingQueue<String> movieLinks = new LinkedBlockingQueue<>();

        MovieLinkProducerThread movieLinkProducerThread  = new MovieLinkProducerThread(movieLinks,pageLinks);
        ExecutorService executorService_Producer = Executors.newSingleThreadExecutor();
        executorService_Producer.execute(movieLinkProducerThread);


        MovieLinkConsumerThread movieLinkConsumerThread = new MovieLinkConsumerThread(movieLinks,linkCommentDTOs,pageLinks);
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
            if(pageLinks.isEmpty()) {
                if(linkCommentDTOs.size()==movieCounter) {
                    executorService_Producer.shutdown();
                    executorService_Consumer.shutdown();
                    consumerIsRunning = false;
                }
            }
        }

        Collections.sort(linkCommentDTOs, (o1, o2) -> o2.numberWantIt-o1.numberWantIt);
        return linkCommentDTOs;
    }

    private static List<String> getCodes(Iterable<Movie> movies) {
        List<String> codes = new ArrayList<>();
        movies.forEach(movie -> codes.add(movie.getCode()));
        return codes;
    }
}
