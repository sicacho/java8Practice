package com.company.googledrive;

import com.company.configuration.RepositoryConfiguration;
import com.company.downloadlink.LinkCommentDTO;
import com.company.repository.ActorRepository;
import com.company.repository.MovieRepository;
import com.company.repository.StudioRepository;
import com.company.repository.TypeRepository;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

import java.io.IOException;
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
        List<MovieDTO> movieDTOs = null;
        movieDTOs = getDataFromGoogleDrive("0B6iOGhAfgoxVRHVuZ3gzMEpkdk0");
        ApplicationContext ctx = null;
        ctx = new SpringApplicationBuilder().sources(Main.class).web(false).run(args);
        MovieService movieService = (MovieService) ctx.getBean("movieService");
//        insertMovieFromStudio(movieDTOs, movieService);
        try {
            insertMovieFromListType(movieDTOs,movieService);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private static List<MovieDTO> getDataFromGoogleDrive(String folderId) {
        Drive service = null;
        FileList result = null;
        List<MovieDTO> movieDTOs = new ArrayList<>();
        try {
            service = DriveQuickstart.getDriveService();
            result = service.files().list().setQ("'"+ folderId +"' in parents")
                    .setPageSize(1000)
                    .setFields("nextPageToken, files(id, name, videoMediaMetadata)")
                    .execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Print the names and IDs for up to 10 files.

        List<File> files = result.getFiles();
        if (files == null || files.size() == 0) {
            System.out.println("No files found.");
        } else {
            MovieDTO movieDTO = null;
            String fileName = "";
            for (File file : files) {
                if(!file.getName().equals("test")) {
                    try {
                        System.out.println("Files: " + file.getName() + " | " + file.getId());
                        movieDTO = new MovieDTO();
                        fileName = file.getName();
                        if(fileName.contains(".")) {
                            fileName = file.getName().substring(0,file.getName().indexOf("."));
                        }
                        fileName = fileName.replace("%","");
                        movieDTO.name = fileName;
                        movieDTO.googleId = file.getId();
                        movieDTO.isHD = file.getVideoMediaMetadata().getHeight() > 480 ? true : false;
                        movieDTOs.add(movieDTO);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

            }
        }
        System.out.println(movieDTOs.size());
        return  movieDTOs;
    }

    private static void insertMovieFromListType(List<MovieDTO> movieDTOs,MovieService movieService) throws InterruptedException {
        List<LinkCommentDTO>  linkCommentDTOs = JsonConverter.getLinkData("D:\\study\\javservice_metadata\\hitodzuma-library-20170321.json");
        BlockingQueue<String> movieLinks = new LinkedBlockingQueue<>();
        List<MovieDTO> moviesHaveDetailList = new ArrayList<>();
        Queue<String> pageLinks = new ArrayDeque<>();
        movieCounter = movieDTOs.size();
        for (MovieDTO movieDTO : movieDTOs) {
            for (LinkCommentDTO linkCommentDTO : linkCommentDTOs) {
                if(movieDTO.name.toUpperCase().equals(linkCommentDTO.code_video.toUpperCase())) {
                    System.out.println(linkCommentDTO.code_video);
                    movieLinks.put(linkCommentDTO.linkprimary);
                }
            }
        }
        System.out.println(movieLinks.size());
        MovieLinkConsumerThread movieLinkConsumerThread = new MovieLinkConsumerThread(movieLinks,moviesHaveDetailList,pageLinks,movieDTOs);
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
                    executorService_Consumer.shutdown();
                    consumerIsRunning = false;
                }
            }
        }
        moviesHaveDetailList.forEach(movieDTO -> System.out.println(movieDTO.name));
        movieService.insertMovies(moviesHaveDetailList);
    }

    private static void insertMovieFromStudio(List<MovieDTO> movieDTOs, MovieService movieService) {
        Queue<String> pageLinks = new ArrayDeque<>();
        for (int i = 1; i < 58; i++) {
            pageLinks.add("http://www.javlibrary.com/en/vl_maker.php?&mode=2&m=ayeq&page="+i);
        }
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
    }
}
