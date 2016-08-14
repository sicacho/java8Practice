package com.company.googledrive;

import com.company.configuration.RepositoryConfiguration;
import com.company.repository.ActorRepository;
import com.company.repository.MovieRepository;
import com.company.repository.StudioRepository;
import com.company.repository.TypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Administrator on 8/11/2016.
 */
@SpringBootApplication
@ComponentScan
@Import({RepositoryConfiguration.class})
public class Main {

    public static int movieCounter = 0;

    public static void main(String[] args) {
//        System.out.println(JsonConverter.getData().size());
        ApplicationContext ctx = null;
        ctx = new SpringApplicationBuilder().sources(Main.class).web(false).run(args);
        ActorRepository actorRepository = (ActorRepository) ctx.getBean("actorRepository");
        MovieRepository movieRepository = (MovieRepository) ctx.getBean("movieRepository");
        StudioRepository studioRepository = (StudioRepository) ctx.getBean("studioRepository");
        TypeRepository typeRepository = (TypeRepository) ctx.getBean("typeRepository");
        MovieService movieService = (MovieService) ctx.getBean("movieService");
        Queue<String> pageLinks = new ArrayDeque<>();
        for (int i = 1; i < 58; i++) {
            pageLinks.add("http://www.javlibrary.com/en/vl_maker.php?&mode=2&m=ayeq&page="+i);
        }
        List<MovieDTO> movieDTOs = JsonConverter.getData();
        BlockingQueue<String> movieLinks = new LinkedBlockingQueue<>();
        List<MovieDTO> moviesHaveDetailList = new ArrayList<>();
        MovieLinkProducerThread movieLinkProducerThread = new MovieLinkProducerThread(movieLinks,pageLinks,movieDTOs);
        MovieLinkConsumerThread movieLinkConsumerThread = new MovieLinkConsumerThread(movieLinks,moviesHaveDetailList,pageLinks,movieDTOs);

        ExecutorService executorService_Producer = Executors.newSingleThreadExecutor();
        executorService_Producer.execute(movieLinkProducerThread);


        ExecutorService executorService_Consumer = Executors.newWorkStealingPool();
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
                if(moviesHaveDetailList.size()==movieCounter) {
                    executorService_Producer.shutdown();
                    executorService_Consumer.shutdown();
                    consumerIsRunning = false;
                }
            }
        }
        moviesHaveDetailList.forEach(movieDTO -> System.out.println(movieDTO.name));
        movieService.insertMovies(moviesHaveDetailList);

//        boolean flag = true;
//        while (flag) {
//            if(moviesHaveDetailList.size()==movieCounter) {
//                movieLinkProducerThread.stop();
//                movieLinkConsumerThread.stop();
//                flag = false;
//            }
//        }
//        System.out.println("exit");

    }
}
